package com.muguett.veilreach.world.biome;

import com.muguett.veilreach.VeilreachMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class HushlandsBiomes {
    private static boolean initialized;

    private HushlandsBiomes() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        Registry.register(Registries.BIOME_SOURCE, Identifier.of(VeilreachMod.MOD_ID, "hushlands_regions"), HushlandsRegionalBiomeSource.CODEC);
    }
}
