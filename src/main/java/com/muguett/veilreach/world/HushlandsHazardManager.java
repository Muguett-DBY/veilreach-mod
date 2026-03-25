package com.muguett.veilreach.world;

import com.muguett.veilreach.progress.HushlandsProgression;
import com.muguett.veilreach.world.dimension.VeilreachDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public final class HushlandsHazardManager {
    private HushlandsHazardManager() {
    }

    public static void initialize() {
        ServerTickEvents.END_WORLD_TICK.register(HushlandsHazardManager::tickWorld);
    }

    private static void tickWorld(ServerWorld world) {
        if (world.getRegistryKey() != VeilreachDimensions.HUSHLANDS_WORLD) {
            return;
        }

        for (ServerPlayerEntity player : world.getPlayers()) {
            boolean changed = HushlandsProgression.refreshDerivedMilestones(player);
            if (changed || player.age % 20 == 0) {
                HushlandsProgression.sync(player, changed);
            }

            if (player.isCreative() || player.isSpectator() || player.age % 40 != 0) {
                continue;
            }

            switch (HushlandsRegionResolver.getRegion(player)) {
                case OUTER -> applyOuterPressure(player);
                case MID -> applyMidPressure(player, world);
                case INNER -> applyInnerPressure(player, world);
            }
        }
    }

    private static void applyOuterPressure(ServerPlayerEntity player) {
        if (HushlandsProgression.hasAnyWard(player)) {
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60, 0, false, false));
        if (player.age % 200 == 0) {
            player.sendMessage(Text.translatable("message.veilreach.hushlands.outer"), true);
        }
    }

    private static void applyMidPressure(ServerPlayerEntity player, ServerWorld world) {
        if (HushlandsProgression.hasMidWard(player)) {
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 80, 0, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 0, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 0, false, true));
        if (player.age % 120 == 0) {
            player.damage(world, player.getDamageSources().magic(), 1.0F);
        }
        if (player.age % 160 == 0) {
            player.sendMessage(Text.translatable("message.veilreach.hushlands.mid"), true);
        }
    }

    private static void applyInnerPressure(ServerPlayerEntity player, ServerWorld world) {
        if (HushlandsProgression.hasInnerWard(player)) {
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 0, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 1, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 100, 0, false, true));
        if (player.age % 80 == 0) {
            player.damage(world, player.getDamageSources().magic(), 2.0F);
        }
        if (player.age % 140 == 0) {
            player.sendMessage(Text.translatable("message.veilreach.hushlands.inner"), true);
        }
    }
}
