package com.muguett.veilreach.world.biome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.muguett.veilreach.world.HushlandsRegionResolver;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.stream.Stream;

public final class HushlandsRegionalBiomeSource extends BiomeSource {
    public static final MapCodec<HushlandsRegionalBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.REGISTRY_CODEC.fieldOf("outer_primary").forGetter(source -> source.outerPrimary),
            Biome.REGISTRY_CODEC.fieldOf("outer_secondary").forGetter(source -> source.outerSecondary),
            Biome.REGISTRY_CODEC.fieldOf("outer_tertiary").forGetter(source -> source.outerTertiary),
            Biome.REGISTRY_CODEC.fieldOf("middle").forGetter(source -> source.middle),
            Biome.REGISTRY_CODEC.fieldOf("inner").forGetter(source -> source.inner)
    ).apply(instance, HushlandsRegionalBiomeSource::new));

    private final RegistryEntry<Biome> outerPrimary;
    private final RegistryEntry<Biome> outerSecondary;
    private final RegistryEntry<Biome> outerTertiary;
    private final RegistryEntry<Biome> middle;
    private final RegistryEntry<Biome> inner;

    public HushlandsRegionalBiomeSource(
            RegistryEntry<Biome> outerPrimary,
            RegistryEntry<Biome> outerSecondary,
            RegistryEntry<Biome> outerTertiary,
            RegistryEntry<Biome> middle,
            RegistryEntry<Biome> inner
    ) {
        this.outerPrimary = outerPrimary;
        this.outerSecondary = outerSecondary;
        this.outerTertiary = outerTertiary;
        this.middle = middle;
        this.inner = inner;
    }

    @Override
    protected MapCodec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.of(outerPrimary, outerSecondary, outerTertiary, middle, inner);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        int blockX = BiomeCoords.toBlock(x);
        int blockZ = BiomeCoords.toBlock(z);
        double distance = Math.sqrt((double) blockX * blockX + (double) blockZ * blockZ);

        if (distance >= HushlandsRegionResolver.MID_RADIUS) {
            return inner;
        }
        if (distance >= HushlandsRegionResolver.OUTER_RADIUS) {
            return middle;
        }

        double pattern = Math.sin(blockX * 0.0085D)
                + Math.cos(blockZ * 0.0105D)
                + Math.sin((blockX + blockZ) * 0.005D);
        if (pattern > 0.95D) {
            return outerTertiary;
        }
        if (pattern > -0.15D) {
            return outerSecondary;
        }
        return outerPrimary;
    }
}
