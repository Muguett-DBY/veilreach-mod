package com.muguett.veilreach.world;

import com.muguett.veilreach.VeilreachMod;
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
            if (player.isCreative() || player.isSpectator()) {
                continue;
            }
            if (player.age % 40 != 0) {
                continue;
            }
            if (player.hasStatusEffect(StatusEffects.RESISTANCE)) {
                continue;
            }

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 80, 0, false, true));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 0, false, true));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 0, false, true));

            if (player.age % 200 == 0) {
                player.sendMessage(Text.translatable("message.veilreach.hushlands.hazard"), true);
            }
        }
    }
}
