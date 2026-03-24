from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter


ROOT = Path(__file__).resolve().parents[1]
GUI_DIR = ROOT / "src" / "main" / "resources" / "assets" / "veilreach" / "textures" / "gui"


def ensure() -> None:
    GUI_DIR.mkdir(parents=True, exist_ok=True)


def rgba(hex_value: str, alpha: int = 255) -> tuple[int, int, int, int]:
    hex_value = hex_value.lstrip("#")
    return tuple(int(hex_value[index:index + 2], 16) for index in (0, 2, 4)) + (alpha,)


def save(name: str, image: Image.Image) -> None:
    path = GUI_DIR / name
    image.save(path)
    print(f"wrote {path.relative_to(ROOT)}")


def create_hud_frame() -> Image.Image:
    image = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)

    for radius, alpha in ((106, 20), (96, 28), (86, 36)):
        draw.ellipse((128 - radius, 128 - radius, 128 + radius, 128 + radius), outline=rgba("#B7EAF2", alpha), width=2)

    points = [(128, 18), (238, 128), (128, 238), (18, 128)]
    draw.polygon(points, outline=rgba("#DFF8FF", 110))
    draw.polygon([(128, 34), (222, 128), (128, 222), (34, 128)], outline=rgba("#7CD0E0", 90))

    for offset in range(-2, 3):
        draw.line((128 + offset, 48, 128 + offset, 208), fill=rgba("#FFF0C4", 80), width=1)
        draw.line((48, 128 + offset, 208, 128 + offset), fill=rgba("#9EDDEB", 65), width=1)

    for cx, cy in ((128, 56), (200, 128), (128, 200), (56, 128), (128, 128)):
        size = 7 if (cx, cy) == (128, 128) else 5
        draw.rectangle((cx - 1, cy - size, cx + 1, cy + size), fill=rgba("#FFF5D4", 130))
        draw.rectangle((cx - size, cy - 1, cx + size, cy + 1), fill=rgba("#C5F4FF", 130))

    image = image.filter(ImageFilter.GaussianBlur(0.45))
    return image


def create_corner_crest() -> Image.Image:
    image = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.polygon([(32, 4), (52, 20), (46, 48), (18, 60), (8, 24)], fill=rgba("#10242E", 175), outline=rgba("#DFF8FF", 200))
    draw.polygon([(32, 11), (45, 22), (40, 44), (22, 52), (15, 25)], outline=rgba("#7FCBE1", 190))
    draw.line((32, 15, 32, 45), fill=rgba("#FFF0C4", 220), width=2)
    draw.line((20, 31, 44, 31), fill=rgba("#BEEAF7", 220), width=2)
    draw.ellipse((24, 23, 40, 39), outline=rgba("#92DFF2", 200), width=2)
    return image.filter(ImageFilter.GaussianBlur(0.3))


def create_fog_strip() -> Image.Image:
    image = Image.new("RGBA", (256, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    for y in range(64):
        alpha = int(max(0, 80 - y))
        draw.rectangle((0, y, 255, y), fill=rgba("#A4D8E2", alpha))
    for x in range(0, 256, 24):
        draw.ellipse((x - 18, 10, x + 32, 58), fill=rgba("#DDF8FF", 18))
        draw.ellipse((x - 8, 0, x + 42, 42), fill=rgba("#7EC8D8", 12))
    return image.filter(ImageFilter.GaussianBlur(2.2))


def main() -> None:
    ensure()
    save("hushlands_hud_frame.png", create_hud_frame())
    save("hushlands_corner_crest.png", create_corner_crest())
    save("hushlands_fog_strip.png", create_fog_strip())


if __name__ == "__main__":
    main()
