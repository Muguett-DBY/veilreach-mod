package com.muguett.veilreach.client;

import com.muguett.veilreach.world.dimension.VeilreachDimensions;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public final class HushlandsClientEffects {
    private HushlandsClientEffects() {
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(HushlandsClientEffects::spawnAmbientParticles);
    }

    private static void spawnAmbientParticles(MinecraftClient client) {
        ClientWorld world = client.world;
        if (world == null || client.player == null || !world.getRegistryKey().equals(VeilreachDimensions.HUSHLANDS_WORLD)) {
            return;
        }

        Random random = world.random;
        if (random.nextInt(3) != 0) {
            return;
        }

        BlockPos origin = client.player.getBlockPos();
        RegistryEntry<Biome> biome = world.getBiome(origin);
        String biomeId = biome.getKey().map(key -> key.getValue().toString()).orElse("");
        boolean moonlit = biomeId.contains("moonlit_thicket");
        boolean rift = biomeId.contains("rift_meadows");
        boolean badlands = biomeId.contains("silent_badlands");
        for (int i = 0; i < 5; i++) {
            double x = origin.getX() + 0.5D + random.nextDouble() * 22.0D - 11.0D;
            double y = origin.getY() + 0.6D + random.nextDouble() * 5.5D;
            double z = origin.getZ() + 0.5D + random.nextDouble() * 22.0D - 11.0D;
            BlockPos samplePos = BlockPos.ofFloored(x, y, z);
            boolean nearPortalPalette = world.getBlockState(samplePos.down()).getBlock().getTranslationKey().contains("moon")
                    || world.getBlockState(samplePos.down()).getBlock().getTranslationKey().contains("veil");

            if (random.nextBoolean()) {
                world.addParticleClient((nearPortalPalette || moonlit) ? ParticleTypes.WHITE_ASH : ParticleTypes.ASH, x, y, z, 0.0D, 0.008D, 0.0D);
            }
            if (random.nextInt(4) == 0) {
                world.addParticleClient(ParticleTypes.GLOW, x, y - 0.5D, z, 0.0D, 0.01D, 0.0D);
            }
            if ((moonlit || rift) && random.nextInt(5) == 0) {
                world.addParticleClient(ParticleTypes.END_ROD, x, y, z, (random.nextDouble() - 0.5D) * 0.01D, 0.015D, (random.nextDouble() - 0.5D) * 0.01D);
            }
            if (rift && random.nextInt(6) == 0) {
                world.addParticleClient(ParticleTypes.REVERSE_PORTAL, x, y, z, 0.0D, 0.01D, 0.0D);
            }
            if (badlands && random.nextInt(5) == 0) {
                world.addParticleClient(ParticleTypes.SMOKE, x, y - 0.3D, z, 0.0D, 0.01D, 0.0D);
            }
            if (world.getFluidState(samplePos.down()).isIn(FluidTags.WATER) && random.nextInt(5) == 0) {
                world.addParticleClient(ParticleTypes.GLOW_SQUID_INK, x, y - 0.3D, z, 0.0D, 0.02D, 0.0D);
            }
        }

        if (random.nextInt(220) == 0) {
            double sx = client.player.getX() + (random.nextDouble() * 16.0D - 8.0D);
            double sy = client.player.getY() + 3.5D + random.nextDouble() * 2.5D;
            double sz = client.player.getZ() + (random.nextDouble() * 16.0D - 8.0D);
            world.addParticleClient(ParticleTypes.END_ROD, sx, sy, sz, 0.18D, -0.03D, 0.18D);
            world.addParticleClient(ParticleTypes.GLOW, sx, sy, sz, 0.0D, 0.0D, 0.0D);
        }

        if (random.nextInt(320) == 0) {
            net.minecraft.sound.SoundEvent sound = badlands ? SoundEvents.AMBIENT_BASALT_DELTAS_MOOD.value() : (moonlit ? SoundEvents.AMBIENT_WARPED_FOREST_MOOD.value() : SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE);
            world.playSoundClient(
                    client.player.getX(),
                    client.player.getY(),
                    client.player.getZ(),
                    sound,
                    SoundCategory.AMBIENT,
                    0.18F,
                    0.7F + random.nextFloat() * 0.3F,
                    false
            );
        }
    }
}
