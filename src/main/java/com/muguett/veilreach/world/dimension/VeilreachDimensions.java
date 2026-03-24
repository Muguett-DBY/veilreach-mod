package com.muguett.veilreach.world.dimension;

import com.muguett.veilreach.VeilreachMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public final class VeilreachDimensions {
    public static final Identifier HUSHLANDS_ID = Identifier.of(VeilreachMod.MOD_ID, "hushlands");
    public static final RegistryKey<World> HUSHLANDS_WORLD = RegistryKey.of(RegistryKeys.WORLD, HUSHLANDS_ID);
    public static final RegistryKey<DimensionType> HUSHLANDS_DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, HUSHLANDS_ID);
    public static final RegistryKey<DimensionOptions> HUSHLANDS_DIMENSION = RegistryKey.of(RegistryKeys.DIMENSION, HUSHLANDS_ID);

    private VeilreachDimensions() {
    }

    public static void initialize() {
    }
}
