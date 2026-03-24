package com.muguett.veilreach.registry;

import com.muguett.veilreach.VeilreachMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class VeilreachSoundEvents {
    public static final SoundEvent HUSHLANDS_WHISPERS = register("ambient.hushlands_whispers");
    public static final SoundEvent RIFT_MEADOWS_RESONANCE = register("ambient.rift_meadows_resonance");
    public static final SoundEvent HUSH_PORTAL_LOOP = register("block.hush_portal_loop");
    public static final SoundEvent HUSHLING_LUNGE = register("entity.hushling_lunge");
    public static final SoundEvent HUSH_SOVEREIGN_PULSE = register("entity.hush_sovereign_pulse");
    public static final SoundEvent ASTRAL_SIGIL_USE = register("ui.astral_sigil");
    public static final SoundEvent SEALED_ALTAR_UNLOCK = register("ui.sealed_altar_unlock");
    public static final SoundEvent HUSHLANDS_ARRIVAL_INTRO = register("ui.hushlands_arrival_intro");
    public static final SoundEvent HUSHLANDS_DREAM = register("music.hushlands_dream");
    public static final SoundEvent HUSHLANDS_DEPTHS = register("music.hushlands_depths");
    public static final SoundEvent HUSHLANDS_HUSH_GROVE = register("music.hushlands.hush_grove");
    public static final SoundEvent HUSHLANDS_GLOAM_FOREST = register("music.hushlands.gloam_forest");
    public static final SoundEvent HUSHLANDS_MOONLIT_THICKET = register("music.hushlands.moonlit_thicket");
    public static final SoundEvent HUSHLANDS_RIFT_MEADOWS = register("music.hushlands.rift_meadows");
    public static final SoundEvent HUSHLANDS_SILENT_BADLANDS = register("music.hushlands.silent_badlands");

    private VeilreachSoundEvents() {
    }

    public static void initialize() {
    }

    private static SoundEvent register(String path) {
        Identifier id = Identifier.of(VeilreachMod.MOD_ID, path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}
