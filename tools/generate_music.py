import math
import random
import subprocess
import wave
from pathlib import Path

import numpy as np


ROOT = Path(__file__).resolve().parents[1]
ASSET_ROOT = ROOT / "src" / "main" / "resources" / "assets" / "veilreach" / "sounds" / "music" / "hushlands"
TEMP_ROOT = ROOT / "build" / "generated-music"
FFMPEG = Path(r"E:\MOD\ffmpeg-local-2\ffmpeg-8.1-essentials_build\bin\ffmpeg.exe")
SAMPLE_RATE = 44_100


TRACKS = {
    "ambient_hushlands_whispers": {
        "length": 58,
        "root": 83.0,
        "pad_chord": [1.0, 1.333, 1.777],
        "pulse_rate": 0.032,
        "pulse_depth": 0.05,
        "noise": 0.032,
        "bell_interval": 19.5,
        "bell_scale": [1.0, 1.2],
        "stereo_spin": 0.08,
    },
    "ambient_rift_meadows_resonance": {
        "length": 52,
        "root": 130.81,
        "pad_chord": [1.0, 1.414, 2.0],
        "pulse_rate": 0.044,
        "pulse_depth": 0.07,
        "noise": 0.026,
        "bell_interval": 13.0,
        "bell_scale": [1.0, 1.5, 2.0],
        "stereo_spin": 0.16,
    },
    "hush_grove": {
        "length": 108,
        "root": 110.0,
        "pad_chord": [1.0, 1.5, 2.25],
        "pulse_rate": 0.065,
        "pulse_depth": 0.10,
        "noise": 0.025,
        "bell_interval": 11.5,
        "bell_scale": [1.0, 1.125, 1.333, 1.5],
        "stereo_spin": 0.18,
    },
    "gloam_forest": {
        "length": 101,
        "root": 92.5,
        "pad_chord": [1.0, 1.2, 1.6],
        "pulse_rate": 0.052,
        "pulse_depth": 0.07,
        "noise": 0.04,
        "bell_interval": 14.0,
        "bell_scale": [0.75, 1.0, 1.2],
        "stereo_spin": 0.12,
    },
    "moonlit_thicket": {
        "length": 96,
        "root": 146.83,
        "pad_chord": [1.0, 1.333, 2.0],
        "pulse_rate": 0.082,
        "pulse_depth": 0.12,
        "noise": 0.02,
        "bell_interval": 9.0,
        "bell_scale": [1.0, 1.25, 1.5, 2.0],
        "stereo_spin": 0.22,
    },
    "rift_meadows": {
        "length": 99,
        "root": 123.47,
        "pad_chord": [1.0, 1.414, 2.0],
        "pulse_rate": 0.11,
        "pulse_depth": 0.14,
        "noise": 0.03,
        "bell_interval": 8.0,
        "bell_scale": [1.0, 1.5, 2.0, 2.25],
        "stereo_spin": 0.28,
    },
    "silent_badlands": {
        "length": 92,
        "root": 77.78,
        "pad_chord": [1.0, 1.125, 1.6],
        "pulse_rate": 0.045,
        "pulse_depth": 0.08,
        "noise": 0.055,
        "bell_interval": 15.5,
        "bell_scale": [0.5, 1.0, 1.25],
        "stereo_spin": 0.09,
    },
}


def smooth_noise(rng: np.random.Generator, size: int, strength: float) -> np.ndarray:
    noise = rng.normal(0.0, 1.0, size).astype(np.float32)
    kernel = np.hanning(1201).astype(np.float32)
    kernel /= kernel.sum()
    return np.convolve(noise, kernel, mode="same") * strength


def envelope(length: int, attack: float, release: float) -> np.ndarray:
    env = np.ones(length, dtype=np.float32)
    a = max(1, int(length * attack))
    r = max(1, int(length * release))
    env[:a] = np.linspace(0.0, 1.0, a, dtype=np.float32)
    env[-r:] = np.linspace(1.0, 0.0, r, dtype=np.float32)
    return env


def bell(sr: int, freq: float, duration: float, pan: float, amp: float) -> np.ndarray:
    n = int(sr * duration)
    t = np.arange(n, dtype=np.float32) / sr
    env = np.exp(-3.7 * t, dtype=np.float32)
    body = np.sin(2 * math.pi * freq * t)
    overtone = 0.5 * np.sin(2 * math.pi * freq * 2.73 * t + 0.4)
    sparkle = 0.22 * np.sin(2 * math.pi * freq * 4.1 * t + 1.1)
    mono = (body + overtone + sparkle) * env * amp
    left = mono * math.sqrt((1 - pan) * 0.5)
    right = mono * math.sqrt((1 + pan) * 0.5)
    return np.stack([left, right], axis=1).astype(np.float32)


def build_track(name: str, config: dict) -> np.ndarray:
    rng = np.random.default_rng(abs(hash(name)) % (2**32))
    duration = config["length"]
    count = SAMPLE_RATE * duration
    t = np.arange(count, dtype=np.float32) / SAMPLE_RATE

    stereo = np.zeros((count, 2), dtype=np.float32)

    pad = np.zeros(count, dtype=np.float32)
    for i, ratio in enumerate(config["pad_chord"]):
        detune = 1.0 + (i - 1) * 0.003
        freq = config["root"] * ratio * detune
        phase = rng.random() * math.tau
        lfo = 1.0 + 0.005 * np.sin(2 * math.pi * (0.012 + i * 0.004) * t + phase)
        pad += np.sin(2 * math.pi * freq * lfo * t + phase, dtype=np.float32) * (0.22 / (i + 1))
    pad *= envelope(count, 0.02, 0.08)

    sub = np.sin(2 * math.pi * (config["root"] * 0.5) * t, dtype=np.float32) * 0.06
    pulse = (0.5 + 0.5 * np.sin(2 * math.pi * config["pulse_rate"] * t - 1.2)) ** 2
    pulse = 1.0 - config["pulse_depth"] + pulse * config["pulse_depth"]

    bed = (pad + sub) * pulse
    haze = smooth_noise(rng, count, config["noise"])

    pan_left = 0.84 - config["stereo_spin"] * np.sin(2 * math.pi * 0.009 * t)
    pan_right = 0.84 + config["stereo_spin"] * np.sin(2 * math.pi * 0.011 * t + 0.8)
    stereo[:, 0] += bed * pan_left + haze * 0.85
    stereo[:, 1] += bed * pan_right + haze * 0.72

    for seconds in np.arange(3.0, duration - 4.0, config["bell_interval"]):
        start = int(seconds * SAMPLE_RATE)
        freq = config["root"] * random.choice(config["bell_scale"]) * (1.0 + rng.uniform(-0.02, 0.02))
        hit = bell(SAMPLE_RATE, freq, 4.5 + rng.uniform(-0.6, 0.8), rng.uniform(-0.8, 0.8), 0.18)
        end = min(count, start + hit.shape[0])
        stereo[start:end] += hit[: end - start]

    # Slow shimmer band to stop the tracks from feeling like a plain drone.
    shimmer = np.sin(2 * math.pi * (config["root"] * 3.0) * t + 0.7, dtype=np.float32)
    shimmer *= (0.5 + 0.5 * np.sin(2 * math.pi * 0.021 * t + 1.2)) * 0.018
    stereo[:, 0] += shimmer * 0.7
    stereo[:, 1] += shimmer

    peak = float(np.max(np.abs(stereo)))
    if peak > 0:
        stereo *= 0.82 / peak
    return stereo.astype(np.float32)


def write_wav(path: Path, data: np.ndarray) -> None:
    pcm = np.clip(data * 32767.0, -32768, 32767).astype(np.int16)
    with wave.open(str(path), "wb") as wav_file:
        wav_file.setnchannels(2)
        wav_file.setsampwidth(2)
        wav_file.setframerate(SAMPLE_RATE)
        wav_file.writeframes(pcm.tobytes())


def encode_ogg(wav_path: Path, ogg_path: Path) -> None:
    ogg_path.parent.mkdir(parents=True, exist_ok=True)
    subprocess.run(
        [
            str(FFMPEG),
            "-y",
            "-i",
            str(wav_path),
            "-c:a",
            "libvorbis",
            "-q:a",
            "4",
            str(ogg_path),
        ],
        check=True,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )


def main() -> None:
    TEMP_ROOT.mkdir(parents=True, exist_ok=True)
    ASSET_ROOT.mkdir(parents=True, exist_ok=True)
    for name, config in TRACKS.items():
        wav_path = TEMP_ROOT / f"{name}.wav"
        if name.startswith("ambient_"):
            ogg_path = ROOT / "src" / "main" / "resources" / "assets" / "veilreach" / "sounds" / f"{name}.ogg"
        else:
            ogg_path = ASSET_ROOT / f"{name}.ogg"
        track = build_track(name, config)
        write_wav(wav_path, track)
        encode_ogg(wav_path, ogg_path)
        print(f"generated {ogg_path}")


if __name__ == "__main__":
    main()
