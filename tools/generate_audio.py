from __future__ import annotations

import math
from pathlib import Path

import numpy as np
import soundfile as sf


ROOT = Path(__file__).resolve().parents[1]
SOUND_DIR = ROOT / "src" / "main" / "resources" / "assets" / "veilreach" / "sounds"
SAMPLE_RATE = 44100


def ensure_dir(path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)


def normalize(audio: np.ndarray, peak: float = 0.9) -> np.ndarray:
    max_value = float(np.max(np.abs(audio)))
    if max_value <= 1.0e-6:
        return audio.astype(np.float32)
    return (audio * (peak / max_value)).astype(np.float32)


def envelope(length: int, attack: float, release: float) -> np.ndarray:
    env = np.ones(length, dtype=np.float32)
    attack_len = max(1, int(length * attack))
    release_len = max(1, int(length * release))
    env[:attack_len] = np.linspace(0.0, 1.0, attack_len, dtype=np.float32)
    env[-release_len:] = np.linspace(1.0, 0.0, release_len, dtype=np.float32)
    return env


def sine(freq: float, duration: float, phase: float = 0.0) -> np.ndarray:
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    return np.sin((2.0 * math.pi * freq * t) + phase)


def noise(duration: float, seed: int, color: str = "white") -> np.ndarray:
    rng = np.random.default_rng(seed)
    data = rng.uniform(-1.0, 1.0, int(SAMPLE_RATE * duration)).astype(np.float32)
    if color == "pink":
        fft = np.fft.rfft(data)
        freqs = np.fft.rfftfreq(data.size, 1.0 / SAMPLE_RATE)
        scale = np.ones_like(freqs)
        scale[1:] = 1.0 / np.sqrt(freqs[1:])
        data = np.fft.irfft(fft * scale, n=data.size).astype(np.float32)
    elif color == "brown":
        data = np.cumsum(data)
        data /= np.max(np.abs(data))
    return data.astype(np.float32)


def apply_reverb(signal: np.ndarray, delay_s: float, decay: float, taps: int) -> np.ndarray:
    delay = int(SAMPLE_RATE * delay_s)
    output = signal.astype(np.float32).copy()
    for tap in range(1, taps + 1):
        start = delay * tap
        if start >= output.size:
            break
        output[start:] += signal[:-start] * (decay ** tap)
    return output


def pan_stereo(signal: np.ndarray, pan: float) -> np.ndarray:
    left = signal * math.sqrt((1.0 - pan) * 0.5)
    right = signal * math.sqrt((1.0 + pan) * 0.5)
    return np.column_stack((left, right)).astype(np.float32)


def write_ogg(relative_path: str, data: np.ndarray) -> None:
    output = SOUND_DIR / relative_path
    ensure_dir(output)
    sf.write(output, normalize(data), SAMPLE_RATE, format="OGG", subtype="VORBIS")
    print(f"wrote {output.relative_to(ROOT)}")


def layered_chime(freqs: list[float], duration: float, seed: int, drift: float = 0.004) -> np.ndarray:
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    signal = np.zeros_like(t)
    for index, freq in enumerate(freqs):
        partial = np.sin(2 * math.pi * freq * t + (index * 0.7))
        partial += 0.5 * np.sin(2 * math.pi * freq * 2.01 * t + (index * 1.3))
        partial += 0.18 * np.sin(2 * math.pi * freq * 3.98 * t + (index * 0.2))
        wobble = 1.0 + (drift * np.sin(2 * math.pi * (0.12 + index * 0.03) * t))
        signal += partial * wobble * (0.45 / (index + 1))
    signal *= envelope(signal.size, 0.01, 0.82)
    signal = apply_reverb(signal, 0.11, 0.58, 4)
    signal += noise(duration, seed, "pink") * 0.018
    return signal


def make_ui_sigil() -> np.ndarray:
    base = layered_chime([392.0, 523.25, 783.99], 1.65, 1)
    whisper = noise(1.65, 2, "pink") * np.linspace(0.15, 0.02, base.size, dtype=np.float32)
    pulse = sine(1046.5, 1.65) * np.linspace(0.0, 0.18, base.size, dtype=np.float32)
    return pan_stereo(base + whisper + pulse, -0.1)


def make_grenade_pin_pull() -> np.ndarray:
    duration = 0.42
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    click = noise(duration, 101, "white") * np.linspace(0.24, 0.0, t.size, dtype=np.float32)
    ping = sine(1380.0, duration) * envelope(t.size, 0.01, 0.82) * 0.22
    rattle = sine(460.0, duration, phase=0.4) * np.linspace(0.18, 0.02, t.size, dtype=np.float32)
    signal = apply_reverb(click + ping + rattle, 0.025, 0.28, 2)
    return pan_stereo(signal, -0.06)


def make_molotov_ignite() -> np.ndarray:
    duration = 0.66
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    scratch = noise(duration, 102, "white") * np.linspace(0.2, 0.04, t.size, dtype=np.float32)
    flare = noise(duration, 103, "pink") * np.linspace(0.05, 0.12, t.size, dtype=np.float32)
    flame = sine(190.0, duration) * np.linspace(0.05, 0.16, t.size, dtype=np.float32)
    signal = apply_reverb(scratch + flare + flame, 0.03, 0.34, 3)
    return pan_stereo(signal * envelope(t.size, 0.01, 0.32), 0.05)


def make_altar_unlock() -> np.ndarray:
    low = layered_chime([130.81, 196.0, 261.63], 2.8, 3, drift=0.003)
    rise = np.linspace(0.2, 1.0, low.size, dtype=np.float32)
    shock = sine(55.0, 2.8) * rise * 0.35
    glass = layered_chime([659.25, 880.0, 1174.66], 2.8, 4) * 0.45
    return pan_stereo(low + shock + glass, 0.0)


def make_portal_hum() -> np.ndarray:
    duration = 5.5
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    hum = 0.42 * np.sin(2 * math.pi * 61.0 * t)
    hum += 0.24 * np.sin(2 * math.pi * 122.0 * t + 0.3)
    hum += 0.08 * np.sin(2 * math.pi * 245.0 * t + 1.4)
    modulation = 0.78 + 0.22 * np.sin(2 * math.pi * 0.18 * t)
    hiss = noise(duration, 5, "pink") * 0.04
    shimmer = layered_chime([415.3, 622.25], duration, 6) * 0.22
    signal = (hum * modulation) + hiss + shimmer
    signal = apply_reverb(signal, 0.16, 0.52, 3)
    stereo = pan_stereo(signal, 0.12)
    stereo[:, 1] *= 0.95
    return stereo


def make_whisper_ambient() -> np.ndarray:
    duration = 6.0
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    bed = noise(duration, 7, "brown") * 0.11
    air = noise(duration, 8, "pink") * 0.04
    tones = layered_chime([174.61, 261.63, 349.23], duration, 9) * 0.25
    breaths = np.sin(2 * math.pi * 0.23 * t) * 0.5 + 0.5
    signal = (bed * breaths) + air + tones
    return pan_stereo(apply_reverb(signal, 0.19, 0.48, 4), -0.2)


def make_rift_ambient() -> np.ndarray:
    duration = 5.0
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    bass = 0.34 * np.sin(2 * math.pi * 73.42 * t + 0.4)
    wobble = 0.18 * np.sin(2 * math.pi * 146.83 * t * (1.0 + 0.008 * np.sin(2 * math.pi * 0.31 * t)))
    static = noise(duration, 10, "pink") * 0.05
    spark = layered_chime([493.88, 739.99], duration, 11, drift=0.01) * 0.2
    signal = apply_reverb(bass + wobble + static + spark, 0.13, 0.55, 4)
    return pan_stereo(signal, 0.25)


def make_hushling_attack() -> np.ndarray:
    duration = 0.75
    scrape = noise(duration, 12, "white") * np.linspace(0.55, 0.0, int(SAMPLE_RATE * duration), dtype=np.float32)
    sting = sine(220.0, duration) * np.linspace(0.0, 0.4, scrape.size, dtype=np.float32)
    signal = apply_reverb(scrape + sting, 0.04, 0.38, 2)
    return pan_stereo(signal * envelope(signal.size, 0.01, 0.55), -0.15)


def make_sovereign_pulse() -> np.ndarray:
    duration = 1.6
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    throb = sine(82.41, duration) * np.linspace(0.25, 0.7, t.size, dtype=np.float32)
    choir = layered_chime([329.63, 415.3, 554.37], duration, 13) * 0.45
    flare = sine(987.77, duration) * envelope(t.size, 0.02, 0.9) * 0.1
    signal = apply_reverb(throb + choir + flare, 0.09, 0.62, 4)
    return pan_stereo(signal, 0.0)


def make_hushlands_arrival_intro() -> np.ndarray:
    duration = 3.2
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    signal = np.zeros_like(t)
    notes = [
        (0.00, 0.30, 293.66),
        (0.34, 0.30, 329.63),
        (0.68, 0.36, 392.00),
        (1.10, 0.44, 329.63),
        (1.60, 0.34, 493.88),
        (1.98, 0.52, 440.00),
    ]
    for index, (start, note_duration, freq) in enumerate(notes):
        start_index = int(start * SAMPLE_RATE)
        note = layered_chime([freq, freq * 1.5, freq * 2.0], note_duration, 70 + index, drift=0.006) * 0.42
        synth = sine(freq * 0.5, note_duration) * 0.18
        pad = sine(freq * 0.25, note_duration, phase=index * 0.6) * 0.1
        chunk = note + synth + pad
        end_index = min(signal.size, start_index + chunk.size)
        signal[start_index:end_index] += chunk[: end_index - start_index]

    bass = 0.14 * np.sin(2 * math.pi * 73.42 * t + 0.15)
    bass += 0.09 * np.sin(2 * math.pi * 110.0 * t * (1.0 + 0.002 * np.sin(2 * math.pi * 0.8 * t)))
    shimmer = layered_chime([587.33, 783.99], duration, 90, drift=0.01) * 0.1
    air = noise(duration, 91, "pink") * 0.012
    sweep = np.sin(2 * math.pi * (180.0 + 120.0 * np.linspace(0.0, 1.0, t.size, dtype=np.float32)) * t) * np.linspace(0.0, 0.05, t.size, dtype=np.float32)
    signal = apply_reverb(signal + bass + shimmer + air + sweep, 0.12, 0.52, 4)
    return pan_stereo(signal * envelope(signal.size, 0.02, 0.24), 0.02)


def make_little_bird_startup() -> np.ndarray:
    duration = 1.8
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    rotor = np.sin(2 * math.pi * (18.0 + (30.0 * t / duration)) * t)
    rotor += 0.4 * np.sin(2 * math.pi * (36.0 + (62.0 * t / duration)) * t + 0.4)
    whine = sine(320.0, duration) * np.linspace(0.02, 0.16, t.size, dtype=np.float32)
    grit = noise(duration, 121, "pink") * np.linspace(0.03, 0.08, t.size, dtype=np.float32)
    signal = apply_reverb((rotor * np.linspace(0.12, 0.45, t.size, dtype=np.float32)) + whine + grit, 0.05, 0.32, 3)
    return pan_stereo(signal * envelope(signal.size, 0.01, 0.16), 0.06)


def make_little_bird_rotor_loop() -> np.ndarray:
    duration = 4.4
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    chop = np.sin(2 * math.pi * 21.5 * t) * 0.33
    chop += np.sin(2 * math.pi * 43.0 * t + 0.3) * 0.16
    body = np.sin(2 * math.pi * 86.0 * t + (0.06 * np.sin(2 * math.pi * 0.7 * t))) * 0.12
    whirr = noise(duration, 122, "pink") * 0.05
    mid = sine(245.0, duration) * (0.05 + (0.02 * np.sin(2 * math.pi * 0.35 * t)))
    signal = apply_reverb(chop + body + whirr + mid, 0.08, 0.28, 3)
    return pan_stereo(signal * envelope(signal.size, 0.02, 0.06), -0.02)


def make_little_bird_rocket_launch() -> np.ndarray:
    duration = 0.62
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    burst = noise(duration, 123, "white") * np.linspace(0.44, 0.02, t.size, dtype=np.float32)
    hiss = noise(duration, 124, "pink") * np.linspace(0.18, 0.04, t.size, dtype=np.float32)
    thump = sine(74.0, duration) * envelope(t.size, 0.01, 0.6) * 0.3
    signal = apply_reverb(burst + hiss + thump, 0.03, 0.28, 2)
    return pan_stereo(signal, 0.14)


def make_little_bird_rocket_impact() -> np.ndarray:
    duration = 1.1
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    boom = noise(duration, 125, "brown") * np.linspace(0.5, 0.0, t.size, dtype=np.float32)
    crack = noise(duration, 126, "white") * envelope(t.size, 0.01, 0.75) * 0.18
    blast = sine(58.0, duration) * np.linspace(0.36, 0.02, t.size, dtype=np.float32)
    signal = apply_reverb(boom + crack + blast, 0.07, 0.45, 4)
    return pan_stereo(signal, -0.04)


def make_little_bird_bomb_drop() -> np.ndarray:
    duration = 0.5
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    clank = noise(duration, 127, "white") * np.linspace(0.2, 0.0, t.size, dtype=np.float32)
    whistle = sine(980.0, duration) * np.linspace(0.02, 0.12, t.size, dtype=np.float32)
    low = sine(120.0, duration) * np.linspace(0.08, 0.02, t.size, dtype=np.float32)
    signal = apply_reverb(clank + whistle + low, 0.025, 0.22, 2)
    return pan_stereo(signal, 0.0)


def make_little_bird_bomb_impact() -> np.ndarray:
    duration = 1.3
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    boom = noise(duration, 128, "brown") * np.linspace(0.65, 0.0, t.size, dtype=np.float32)
    dirt = noise(duration, 129, "pink") * np.linspace(0.18, 0.02, t.size, dtype=np.float32)
    thud = sine(46.0, duration) * np.linspace(0.46, 0.04, t.size, dtype=np.float32)
    signal = apply_reverb(boom + dirt + thud, 0.08, 0.52, 4)
    return pan_stereo(signal, 0.0)


def make_little_bird_hit() -> np.ndarray:
    duration = 0.42
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    metal = noise(duration, 130, "white") * np.linspace(0.18, 0.0, t.size, dtype=np.float32)
    ping = sine(620.0, duration) * envelope(t.size, 0.01, 0.82) * 0.18
    low = sine(90.0, duration) * np.linspace(0.15, 0.01, t.size, dtype=np.float32)
    signal = apply_reverb(metal + ping + low, 0.03, 0.24, 2)
    return pan_stereo(signal, -0.08)


def make_little_bird_explosion() -> np.ndarray:
    duration = 2.1
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    boom = noise(duration, 131, "brown") * np.linspace(0.8, 0.0, t.size, dtype=np.float32)
    debris = noise(duration, 132, "white") * envelope(t.size, 0.01, 0.84) * 0.2
    sub = sine(34.0, duration) * np.linspace(0.5, 0.03, t.size, dtype=np.float32)
    flame = sine(140.0, duration) * np.linspace(0.08, 0.0, t.size, dtype=np.float32)
    signal = apply_reverb(boom + debris + sub + flame, 0.1, 0.6, 5)
    return pan_stereo(signal, 0.0)


def make_rift_skimmer_loop() -> np.ndarray:
    duration = 4.2
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    hum = sine(168.0, duration) * 0.18
    shimmer = sine(522.0, duration) * (0.06 + 0.02 * np.sin(2 * math.pi * 0.9 * t))
    air = noise(duration, 141, "pink") * 0.03
    pulse = np.sin(2 * math.pi * 9.5 * t) * 0.05
    signal = apply_reverb(hum + shimmer + air + pulse, 0.06, 0.3, 3)
    return pan_stereo(signal * envelope(signal.size, 0.02, 0.04), 0.12)


def make_rift_skimmer_pulse() -> np.ndarray:
    duration = 0.45
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    snap = noise(duration, 142, "white") * np.linspace(0.3, 0.02, t.size, dtype=np.float32)
    beam = sine(760.0, duration) * envelope(t.size, 0.01, 0.74) * 0.24
    low = sine(112.0, duration) * np.linspace(0.14, 0.0, t.size, dtype=np.float32)
    signal = apply_reverb(snap + beam + low, 0.025, 0.22, 2)
    return pan_stereo(signal, 0.16)


def make_rift_skimmer_explosion() -> np.ndarray:
    duration = 1.3
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    boom = noise(duration, 143, "brown") * np.linspace(0.5, 0.0, t.size, dtype=np.float32)
    glass = layered_chime([392.0, 587.33], duration, 144) * 0.12
    sub = sine(42.0, duration) * np.linspace(0.32, 0.02, t.size, dtype=np.float32)
    signal = apply_reverb(boom + glass + sub, 0.08, 0.46, 4)
    return pan_stereo(signal, 0.02)


def make_rift_siege_hum() -> np.ndarray:
    duration = 4.8
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    bass = sine(72.0, duration) * 0.26
    body = sine(144.0, duration, phase=0.3) * 0.12
    air = noise(duration, 145, "pink") * 0.022
    pulse = np.sin(2 * math.pi * 6.2 * t) * 0.03
    signal = apply_reverb(bass + body + air + pulse, 0.08, 0.34, 3)
    return pan_stereo(signal * envelope(signal.size, 0.02, 0.04), -0.05)


def make_rift_siege_cannon() -> np.ndarray:
    duration = 1.0
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    crack = noise(duration, 146, "white") * np.linspace(0.45, 0.0, t.size, dtype=np.float32)
    boom = sine(58.0, duration) * np.linspace(0.34, 0.0, t.size, dtype=np.float32)
    metal = sine(240.0, duration) * envelope(t.size, 0.01, 0.72) * 0.08
    signal = apply_reverb(crack + boom + metal, 0.05, 0.42, 3)
    return pan_stereo(signal, -0.02)


def make_rift_siege_suppress() -> np.ndarray:
    duration = 0.72
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    pop = noise(duration, 147, "white") * np.linspace(0.3, 0.0, t.size, dtype=np.float32)
    warp = sine(312.0, duration) * (0.1 + 0.08 * np.sin(2 * math.pi * 12.0 * t))
    low = sine(86.0, duration) * np.linspace(0.18, 0.03, t.size, dtype=np.float32)
    signal = apply_reverb(pop + warp + low, 0.03, 0.28, 3)
    return pan_stereo(signal, 0.06)


def make_rift_siege_explosion() -> np.ndarray:
    duration = 1.9
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    boom = noise(duration, 148, "brown") * np.linspace(0.72, 0.0, t.size, dtype=np.float32)
    debris = noise(duration, 149, "white") * envelope(t.size, 0.01, 0.8) * 0.18
    sub = sine(36.0, duration) * np.linspace(0.48, 0.02, t.size, dtype=np.float32)
    signal = apply_reverb(boom + debris + sub, 0.1, 0.58, 5)
    return pan_stereo(signal, 0.0)


def make_music(seed: int, roots: list[float], duration: float, pan: float) -> np.ndarray:
    t = np.linspace(0.0, duration, int(SAMPLE_RATE * duration), endpoint=False, dtype=np.float32)
    signal = np.zeros_like(t)
    for idx, freq in enumerate(roots):
        slow = np.sin(2 * math.pi * (0.03 + idx * 0.012) * t)
        layer = np.sin(2 * math.pi * freq * t + idx * 0.4 + slow * 0.12)
        layer += 0.28 * np.sin(2 * math.pi * (freq * 0.5) * t + idx)
        signal += layer * (0.16 / (1 + idx * 0.35))
    shimmer = layered_chime([roots[-1] * 2.0], duration, seed) * 0.08
    wind = noise(duration, seed + 20, "pink") * 0.018
    pulse = np.sin(2 * math.pi * 0.055 * t) * 0.05
    signal = apply_reverb(signal + shimmer + wind + pulse, 0.18, 0.6, 3)
    stereo = pan_stereo(signal, pan)
    blend = np.linspace(0.0, 1.0, int(SAMPLE_RATE * 4.0), dtype=np.float32)
    stereo[:blend.size] *= blend[:, None]
    stereo[-blend.size:] *= blend[::-1, None]
    return stereo


def main() -> None:
    write_ogg("ui/grenade_pin_pull.ogg", make_grenade_pin_pull())
    write_ogg("ui/molotov_ignite.ogg", make_molotov_ignite())
    write_ogg("ui/astral_sigil.ogg", make_ui_sigil())
    write_ogg("ui/sealed_altar_unlock.ogg", make_altar_unlock())
    write_ogg("ui/hushlands_arrival_intro.ogg", make_hushlands_arrival_intro())
    write_ogg("vehicle/little_bird_startup.ogg", make_little_bird_startup())
    write_ogg("vehicle/little_bird_rotor_loop.ogg", make_little_bird_rotor_loop())
    write_ogg("vehicle/little_bird_rocket_launch.ogg", make_little_bird_rocket_launch())
    write_ogg("vehicle/little_bird_rocket_impact.ogg", make_little_bird_rocket_impact())
    write_ogg("vehicle/little_bird_bomb_drop.ogg", make_little_bird_bomb_drop())
    write_ogg("vehicle/little_bird_bomb_impact.ogg", make_little_bird_bomb_impact())
    write_ogg("vehicle/little_bird_hit.ogg", make_little_bird_hit())
    write_ogg("vehicle/little_bird_explosion.ogg", make_little_bird_explosion())
    write_ogg("vehicle/rift_skimmer_loop.ogg", make_rift_skimmer_loop())
    write_ogg("vehicle/rift_skimmer_pulse.ogg", make_rift_skimmer_pulse())
    write_ogg("vehicle/rift_skimmer_explosion.ogg", make_rift_skimmer_explosion())
    write_ogg("vehicle/rift_siege_hum.ogg", make_rift_siege_hum())
    write_ogg("vehicle/rift_siege_cannon.ogg", make_rift_siege_cannon())
    write_ogg("vehicle/rift_siege_suppress.ogg", make_rift_siege_suppress())
    write_ogg("vehicle/rift_siege_explosion.ogg", make_rift_siege_explosion())
    write_ogg("block/hush_portal_loop.ogg", make_portal_hum())
    write_ogg("ambient/hushlands_whispers.ogg", make_whisper_ambient())
    write_ogg("ambient/rift_meadows_resonance.ogg", make_rift_ambient())
    write_ogg("entity/hushling_lunge.ogg", make_hushling_attack())
    write_ogg("entity/hush_sovereign_pulse.ogg", make_sovereign_pulse())
    write_ogg("music/hushlands_dream.ogg", make_music(31, [130.81, 196.0, 261.63, 392.0], 10.0, -0.12))
    write_ogg("music/hushlands_depths.ogg", make_music(47, [98.0, 146.83, 220.0, 329.63], 10.0, 0.08))


if __name__ == "__main__":
    main()
