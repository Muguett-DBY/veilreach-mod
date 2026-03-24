package com.muguett.veilreach.registry;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.world.gen.feature.EchoMonolithFeature;
import com.muguett.veilreach.world.gen.feature.FallenGatewayFeature;
import com.muguett.veilreach.world.gen.feature.ForgottenManorFeature;
import com.muguett.veilreach.world.gen.feature.GloamwoodCampFeature;
import com.muguett.veilreach.world.gen.feature.LanternWatchpostFeature;
import com.muguett.veilreach.world.gen.feature.MoonfallCathedralFeature;
import com.muguett.veilreach.world.gen.feature.MoonblossomPatchFeature;
import com.muguett.veilreach.world.gen.feature.MoonShrineFeature;
import com.muguett.veilreach.world.gen.feature.RiftglassBasinFeature;
import com.muguett.veilreach.world.gen.feature.RiftBloomPatchFeature;
import com.muguett.veilreach.world.gen.feature.ShimmerleafGroveFeature;
import com.muguett.veilreach.world.gen.feature.SilentThroneFeature;
import com.muguett.veilreach.world.gen.feature.SilentArchiveFeature;
import com.muguett.veilreach.world.gen.feature.SunkenSanctumFeature;
import com.muguett.veilreach.world.gen.feature.WatcherObeliskFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public final class VeilreachFeatures {
    public static final Feature<DefaultFeatureConfig> ECHO_MONOLITH = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "echo_monolith"),
            new EchoMonolithFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> MOON_SHRINE = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "moon_shrine"),
            new MoonShrineFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> MOONBLOSSOM_PATCH = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "moonblossom_patch"),
            new MoonblossomPatchFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> WATCHER_OBELISK = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "watcher_obelisk"),
            new WatcherObeliskFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> GLOAMWOOD_CAMP = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "gloamwood_camp"),
            new GloamwoodCampFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> SHIMMERLEAF_GROVE = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "shimmerleaf_grove"),
            new ShimmerleafGroveFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> RIFTGLASS_BASIN = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "riftglass_basin"),
            new RiftglassBasinFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> RIFT_BLOOM_PATCH = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "rift_bloom_patch"),
            new RiftBloomPatchFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> FALLEN_GATEWAY = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "fallen_gateway"),
            new FallenGatewayFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> FORGOTTEN_MANOR = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "forgotten_manor"),
            new ForgottenManorFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> LANTERN_WATCHPOST = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "lantern_watchpost"),
            new LanternWatchpostFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> MOONFALL_CATHEDRAL = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "moonfall_cathedral"),
            new MoonfallCathedralFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> SILENT_THRONE = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "silent_throne"),
            new SilentThroneFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> SILENT_ARCHIVE = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "silent_archive"),
            new SilentArchiveFeature(DefaultFeatureConfig.CODEC)
    );
    public static final Feature<DefaultFeatureConfig> SUNKEN_SANCTUM = Registry.register(
            Registries.FEATURE,
            Identifier.of(VeilreachMod.MOD_ID, "sunken_sanctum"),
            new SunkenSanctumFeature(DefaultFeatureConfig.CODEC)
    );

    private VeilreachFeatures() {
    }

    public static void initialize() {
    }
}
