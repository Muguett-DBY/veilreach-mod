package com.muguett.veilreach.client;

import com.muguett.veilreach.registry.VeilreachSoundEvents;
import com.muguett.veilreach.world.dimension.VeilreachDimensions;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class HushlandsClientEffects {
    private static RegistryKey<World> lastWorldKey;
    private static int arrivalBurstTicks;
    private static int ambientPulseTicks;
    private static String lastBiomeId = "";
    private static int biomeTitleCooldown;

    private HushlandsClientEffects() {
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(HushlandsClientEffects::spawnAmbientParticles);
    }

    private static void spawnAmbientParticles(MinecraftClient client) {
        ClientWorld world = client.world;
        if (world == null || client.player == null) {
            lastWorldKey = null;
            arrivalBurstTicks = 0;
            ambientPulseTicks = 0;
            lastBiomeId = "";
            biomeTitleCooldown = 0;
            return;
        }

        RegistryKey<World> currentWorldKey = world.getRegistryKey();
        if (!currentWorldKey.equals(lastWorldKey)) {
            if (currentWorldKey.equals(VeilreachDimensions.HUSHLANDS_WORLD)) {
                arrivalBurstTicks = 40;
                ambientPulseTicks = 140;
                showArrivalTitle(client);
                playArrivalChime(world, client);
            }
            lastWorldKey = currentWorldKey;
        }

        if (!currentWorldKey.equals(VeilreachDimensions.HUSHLANDS_WORLD)) {
            return;
        }

        Random random = world.random;
        BlockPos origin = client.player.getBlockPos();
        RegistryEntry<Biome> biome = world.getBiome(origin);
        String biomeId = biome.getKey().map(key -> key.getValue().toString()).orElse("");
        boolean moonlit = biomeId.contains("moonlit_thicket");
        boolean rift = biomeId.contains("rift_meadows");
        boolean badlands = biomeId.contains("silent_badlands");
        boolean forest = biomeId.contains("gloam_forest");
        boolean grove = biomeId.contains("hush_grove");

        if (biomeTitleCooldown > 0) {
            biomeTitleCooldown--;
        }
        if (!biomeId.equals(lastBiomeId) && biomeTitleCooldown == 0) {
            showBiomeTitle(client, biomeId);
            biomeTitleCooldown = 120;
            lastBiomeId = biomeId;
        }

        if (arrivalBurstTicks > 0) {
            spawnArrivalBurst(world, client, origin, random, moonlit || rift);
            arrivalBurstTicks--;
        }

        if (ambientPulseTicks > 0) {
            ambientPulseTicks--;
        } else if (random.nextInt(180) == 0) {
            ambientPulseTicks = 100 + random.nextInt(120);
            spawnSkyPulse(world, client, random, badlands || rift);
        }

        if (random.nextInt(3) != 0) {
            return;
        }

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
            if (forest && random.nextInt(5) == 0) {
                world.addParticleClient(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 0.0D, 0.006D, 0.0D);
            }
            if (grove && random.nextInt(5) == 0) {
                world.addParticleClient(ParticleTypes.FALLING_OBSIDIAN_TEAR, x, y + 0.3D, z, 0.0D, 0.004D, 0.0D);
            }
            if (badlands && random.nextInt(5) == 0) {
                world.addParticleClient(ParticleTypes.SMOKE, x, y - 0.3D, z, 0.0D, 0.01D, 0.0D);
            }
            if (world.getFluidState(samplePos.down()).isIn(FluidTags.WATER) && random.nextInt(5) == 0) {
                world.addParticleClient(ParticleTypes.GLOW_SQUID_INK, x, y - 0.3D, z, 0.0D, 0.02D, 0.0D);
            }
            if (random.nextInt(5) == 0) {
                world.addParticleClient(ParticleTypes.MYCELIUM, x, origin.getY() + 0.1D + random.nextDouble() * 0.7D, z, 0.0D, 0.004D, 0.0D);
            }
        }

        if (random.nextInt(220) == 0) {
            double sx = client.player.getX() + (random.nextDouble() * 16.0D - 8.0D);
            double sy = client.player.getY() + 3.5D + random.nextDouble() * 2.5D;
            double sz = client.player.getZ() + (random.nextDouble() * 16.0D - 8.0D);
            world.addParticleClient((badlands || rift) ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.END_ROD, sx, sy, sz, 0.18D, -0.03D, 0.18D);
            world.addParticleClient(ParticleTypes.GLOW, sx, sy, sz, 0.0D, 0.0D, 0.0D);
        }

        if (random.nextInt(260) == 0) {
            spawnHorizonVeil(world, client, random, moonlit, rift, badlands, forest, grove);
        }

        if (random.nextInt(320) == 0) {
            net.minecraft.sound.SoundEvent sound = (rift || badlands)
                    ? VeilreachSoundEvents.RIFT_MEADOWS_RESONANCE
                    : VeilreachSoundEvents.HUSHLANDS_WHISPERS;
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

    private static void playArrivalChime(ClientWorld world, MinecraftClient client) {
        world.playSoundClient(client.player.getX(), client.player.getY(), client.player.getZ(), VeilreachSoundEvents.ASTRAL_SIGIL_USE, SoundCategory.AMBIENT, 0.65F, 0.88F, false);
        world.playSoundClient(client.player.getX(), client.player.getY(), client.player.getZ(), VeilreachSoundEvents.HUSH_PORTAL_LOOP, SoundCategory.AMBIENT, 0.3F, 0.95F, false);
        world.playSoundClient(client.player.getX(), client.player.getY(), client.player.getZ(), VeilreachSoundEvents.HUSHLANDS_WHISPERS, SoundCategory.AMBIENT, 0.2F, 1.0F, false);
    }

    private static void showArrivalTitle(MinecraftClient client) {
        if (client.inGameHud == null) {
            return;
        }
        client.inGameHud.setTitle(net.minecraft.text.Text.translatable("title.veilreach.hushlands"));
        client.inGameHud.setSubtitle(net.minecraft.text.Text.translatable("subtitle.veilreach.hushlands"));
        client.inGameHud.setTitleTicks(15, 80, 35);
    }

    private static void showBiomeTitle(MinecraftClient client, String biomeId) {
        if (client.inGameHud == null || biomeId.isBlank() || !biomeId.startsWith("veilreach:")) {
            return;
        }
        String biomeKey = biomeId.substring("veilreach:".length());
        client.inGameHud.setTitle(net.minecraft.text.Text.translatable("biome.veilreach." + biomeKey));
        client.inGameHud.setSubtitle(net.minecraft.text.Text.translatable("subtitle.veilreach.biome." + biomeKey));
        client.inGameHud.setTitleTicks(10, 55, 20);
    }

    private static void spawnArrivalBurst(ClientWorld world, MinecraftClient client, BlockPos origin, Random random, boolean bright) {
        for (int i = 0; i < 16; i++) {
            double angle = (Math.PI * 2.0D * i) / 16.0D;
            double radius = 1.6D + random.nextDouble() * 1.8D;
            double x = client.player.getX() + Math.cos(angle) * radius;
            double z = client.player.getZ() + Math.sin(angle) * radius;
            double y = origin.getY() + 0.15D + random.nextDouble() * 2.2D;
            world.addParticleClient(ParticleTypes.REVERSE_PORTAL, x, y, z, 0.0D, 0.035D, 0.0D);
            world.addParticleClient(bright ? ParticleTypes.END_ROD : ParticleTypes.WHITE_ASH, x, y, z, 0.0D, 0.02D, 0.0D);
            if (random.nextBoolean()) {
                world.addParticleClient(ParticleTypes.GLOW, x, y, z, 0.0D, 0.01D, 0.0D);
            }
        }
    }

    private static void spawnSkyPulse(ClientWorld world, MinecraftClient client, Random random, boolean intense) {
        double baseX = client.player.getX() + (random.nextDouble() * 28.0D - 14.0D);
        double baseY = client.player.getY() + 8.0D + random.nextDouble() * 7.0D;
        double baseZ = client.player.getZ() + (random.nextDouble() * 28.0D - 14.0D);
        for (int i = 0; i < (intense ? 10 : 6); i++) {
            double x = baseX + random.nextDouble() * 4.0D - 2.0D;
            double y = baseY + random.nextDouble() * 3.5D;
            double z = baseZ + random.nextDouble() * 4.0D - 2.0D;
            world.addParticleClient(intense ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.END_ROD, x, y, z, 0.0D, -0.03D, 0.0D);
            if (random.nextBoolean()) {
                world.addParticleClient(ParticleTypes.GLOW, x, y, z, 0.0D, -0.01D, 0.0D);
            }
        }
    }

    private static void spawnHorizonVeil(ClientWorld world, MinecraftClient client, Random random, boolean moonlit, boolean rift, boolean badlands, boolean forest, boolean grove) {
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double distance = 18.0D + random.nextDouble() * 12.0D;
        double baseX = client.player.getX() + Math.cos(angle) * distance;
        double baseZ = client.player.getZ() + Math.sin(angle) * distance;
        double baseY = client.player.getY() + 4.0D + random.nextDouble() * 6.0D;
        for (int i = 0; i < 12; i++) {
            double x = baseX + random.nextDouble() * 2.6D - 1.3D;
            double y = baseY + i * 0.24D;
            double z = baseZ + random.nextDouble() * 2.6D - 1.3D;
            if (badlands) {
                world.addParticleClient(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.01D, 0.0D);
            } else if (rift) {
                world.addParticleClient(ParticleTypes.REVERSE_PORTAL, x, y, z, 0.0D, 0.012D, 0.0D);
            } else if (moonlit) {
                world.addParticleClient(ParticleTypes.END_ROD, x, y, z, 0.0D, 0.01D, 0.0D);
            } else if (forest) {
                world.addParticleClient(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 0.0D, 0.006D, 0.0D);
            } else if (grove) {
                world.addParticleClient(ParticleTypes.WHITE_ASH, x, y, z, 0.0D, 0.006D, 0.0D);
            }
        }
    }
}
