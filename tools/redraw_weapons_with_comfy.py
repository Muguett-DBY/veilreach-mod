from __future__ import annotations

import argparse
import json
import random
import time
import urllib.error
import urllib.request
from pathlib import Path

from PIL import Image, ImageChops, ImageEnhance, ImageFilter, ImageOps


API_URL = "http://127.0.0.1:8000"
CLIENT_ID = "veilreach-weapon-redraw"
OUTPUT_ROOT = Path(r"E:\ComfyUISOFT\output")

ITEMS = {
    "mirror_blade": {
        "prompt": (
            "ornate moonforged longsword item icon, mirrorsteel blade, silver metal with teal moonlit reflections, "
            "dark wrapped hilt, elegant fantasy weapon, centered diagonal composition, dark plain background, "
            "high detail game icon, readable silhouette"
        ),
        "accent": (126, 214, 224),
    },
    "mirror_pickaxe": {
        "prompt": (
            "ornate mirrorsteel pickaxe item icon, silver and icy teal moonlit metal, carved shaft, refined fantasy mining tool, "
            "centered diagonal composition, dark plain background, high detail game icon, readable silhouette"
        ),
        "accent": (129, 206, 214),
    },
    "mirror_axe": {
        "prompt": (
            "ornate mirrorsteel battle axe item icon, silver metal edge with cool teal reflections, wrapped haft, "
            "elegant fantasy tool-weapon hybrid, centered diagonal composition, dark plain background, "
            "high detail game icon, readable silhouette"
        ),
        "accent": (112, 198, 205),
    },
    "mirror_shovel": {
        "prompt": (
            "ornate mirrorsteel shovel item icon, silver moonlit spade with teal shine, carved dark haft, "
            "fantasy tool, centered diagonal composition, dark plain background, high detail game icon, readable silhouette"
        ),
        "accent": (116, 198, 212),
    },
    "sovereign_glaive": {
        "prompt": (
            "regal sovereign glaive item icon, long ceremonial polearm, silver-black metal, pale gold trim, luminous teal core, "
            "crescent blade, centered diagonal composition, dark plain background, high detail fantasy game icon, readable silhouette"
        ),
        "accent": (214, 194, 122),
    },
}


def api_request(method: str, url: str, payload: dict | None = None) -> dict:
    data = None
    headers = {}
    if payload is not None:
        data = json.dumps(payload).encode("utf-8")
        headers["Content-Type"] = "application/json"
    request = urllib.request.Request(url, data=data, headers=headers, method=method)
    with urllib.request.urlopen(request, timeout=30) as response:
        return json.loads(response.read().decode("utf-8"))


def comfy_available(api_url: str) -> bool:
    try:
        api_request("GET", f"{api_url}/history")
        return True
    except Exception:
        return False


def build_prompt(prompt_text: str, seed: int, filename_prefix: str) -> dict:
    return {
        "28": {
            "inputs": {"weight_dtype": "default", "unet_name": "z_image_turbo_bf16.safetensors"},
            "class_type": "UNETLoader",
        },
        "11": {
            "inputs": {"model": ["28", 0], "shift": 3.0},
            "class_type": "ModelSamplingAuraFlow",
        },
        "30": {
            "inputs": {"device": "default", "clip_name": "qwen_3_4b.safetensors", "type": "lumina2"},
            "class_type": "CLIPLoader",
        },
        "29": {"inputs": {"vae_name": "ae.safetensors"}, "class_type": "VAELoader"},
        "27": {"inputs": {"clip": ["30", 0], "text": prompt_text}, "class_type": "CLIPTextEncode"},
        "33": {"inputs": {"conditioning": ["27", 0]}, "class_type": "ConditioningZeroOut"},
        "13": {
            "inputs": {"batch_size": 1, "width": 512, "height": 512},
            "class_type": "EmptySD3LatentImage",
        },
        "3": {
            "inputs": {
                "denoise": 1.0,
                "seed": seed,
                "negative": ["33", 0],
                "cfg": 1.0,
                "sampler_name": "res_multistep",
                "latent_image": ["13", 0],
                "scheduler": "simple",
                "model": ["11", 0],
                "steps": 4,
                "positive": ["27", 0],
            },
            "class_type": "KSampler",
        },
        "8": {"inputs": {"samples": ["3", 0], "vae": ["29", 0]}, "class_type": "VAEDecode"},
        "99": {"inputs": {"images": ["8", 0], "filename_prefix": filename_prefix}, "class_type": "SaveImage"},
    }


def request_generation(api_url: str, prompt_text: str, filename_prefix: str) -> Path:
    seed = random.randint(1, 2**31 - 1)
    prompt_payload = {"prompt": build_prompt(prompt_text, seed, filename_prefix), "client_id": CLIENT_ID}
    prompt_data = api_request("POST", f"{api_url}/prompt", prompt_payload)
    prompt_id = prompt_data["prompt_id"]

    for _ in range(120):
        history = api_request("GET", f"{api_url}/history")
        entry = history.get(prompt_id)
        if not entry:
            time.sleep(1)
            continue
        if entry.get("status", {}).get("status_str") == "success":
            image_info = entry["outputs"]["99"]["images"][0]
            subfolder = image_info.get("subfolder", "")
            return OUTPUT_ROOT / subfolder / image_info["filename"]
        time.sleep(1)

    raise RuntimeError(f"Timed out waiting for ComfyUI prompt {prompt_id}")


def nontransparent_bbox(image: Image.Image) -> tuple[int, int, int, int] | None:
    alpha = image.getchannel("A")
    bbox = alpha.getbbox()
    return bbox


def estimate_subject_bbox(image: Image.Image) -> tuple[int, int, int, int]:
    rgb = image.convert("RGB")
    corners = [
        rgb.getpixel((0, 0)),
        rgb.getpixel((rgb.width - 1, 0)),
        rgb.getpixel((0, rgb.height - 1)),
        rgb.getpixel((rgb.width - 1, rgb.height - 1)),
    ]
    bg = tuple(sum(color[i] for color in corners) / 4.0 for i in range(3))

    mask = Image.new("L", rgb.size, 0)
    for x in range(rgb.width):
        for y in range(rgb.height):
            pixel = rgb.getpixel((x, y))
            diff = abs(pixel[0] - bg[0]) + abs(pixel[1] - bg[1]) + abs(pixel[2] - bg[2])
            lum = (pixel[0] + pixel[1] + pixel[2]) / 3.0
            if diff > 60 or lum > 72:
                mask.putpixel((x, y), 255)
    mask = mask.filter(ImageFilter.GaussianBlur(radius=2))
    bbox = mask.getbbox()
    if bbox is None:
        return (32, 32, rgb.width - 32, rgb.height - 32)
    return bbox


def tint_image(image: Image.Image, accent: tuple[int, int, int]) -> Image.Image:
    gray = ImageOps.grayscale(image)
    tinted = ImageOps.colorize(gray, black="#101419", white=f"#{accent[0]:02x}{accent[1]:02x}{accent[2]:02x}")
    mixed = Image.blend(image.convert("RGB"), tinted, 0.3)
    mixed = ImageEnhance.Contrast(mixed).enhance(1.15)
    mixed = ImageEnhance.Color(mixed).enhance(1.1)
    return mixed


def rim_light(alpha: Image.Image, accent: tuple[int, int, int]) -> Image.Image:
    spread = alpha.filter(ImageFilter.MaxFilter(5))
    edge = ImageChops.subtract(spread, alpha)
    glow = Image.new("RGBA", alpha.size, (*accent, 0))
    glow.putalpha(edge.point(lambda value: min(255, value * 2)))
    return glow.filter(ImageFilter.GaussianBlur(radius=1))


def merge_texture(original_path: Path, render_path: Path, accent: tuple[int, int, int]) -> None:
    original = Image.open(original_path).convert("RGBA")
    render = Image.open(render_path).convert("RGBA")

    mask_bbox = nontransparent_bbox(original)
    if mask_bbox is None:
        raise RuntimeError(f"{original_path} has no visible pixels")
    subject_bbox = estimate_subject_bbox(render)

    subject = render.crop(subject_bbox)
    subject = tint_image(subject, accent)

    mask_width = mask_bbox[2] - mask_bbox[0]
    mask_height = mask_bbox[3] - mask_bbox[1]
    fitted = ImageOps.contain(subject, (max(1, mask_width - 10), max(1, mask_height - 10)), Image.Resampling.LANCZOS)

    canvas = Image.new("RGBA", original.size, (0, 0, 0, 0))
    offset_x = mask_bbox[0] + (mask_width - fitted.width) // 2
    offset_y = mask_bbox[1] + (mask_height - fitted.height) // 2
    canvas.alpha_composite(fitted.convert("RGBA"), (offset_x, offset_y))

    alpha = original.getchannel("A")
    canvas.putalpha(alpha)

    base_rgb = Image.alpha_composite(Image.new("RGBA", original.size, (0, 0, 0, 0)), canvas)
    highlight = rim_light(alpha, accent)
    result = Image.alpha_composite(base_rgb, highlight)

    result = result.filter(ImageFilter.UnsharpMask(radius=1.4, percent=155, threshold=3))
    result.save(original_path)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo-root", required=True)
    parser.add_argument("--api-url", default=API_URL)
    args = parser.parse_args()

    repo_root = Path(args.repo_root)
    item_dir = repo_root / "src" / "main" / "resources" / "assets" / "veilreach" / "textures" / "item"

    if not comfy_available(args.api_url):
        print("ComfyUI API is unavailable; preserving current weapon textures.")
        return 0

    timestamp = time.strftime("%Y%m%d-%H%M%S")
    for item_name, spec in ITEMS.items():
        original_path = item_dir / f"{item_name}.png"
        filename_prefix = f"veilreach/weapons/{item_name}-{timestamp}"
        render_path = request_generation(args.api_url, spec["prompt"], filename_prefix)
        merge_texture(original_path, render_path, spec["accent"])
        print(f"Redrew {item_name} from {render_path.name}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
