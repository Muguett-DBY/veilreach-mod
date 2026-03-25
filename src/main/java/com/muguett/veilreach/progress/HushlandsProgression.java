package com.muguett.veilreach.progress;

import com.muguett.veilreach.network.HushlandsProgressPayload;
import com.muguett.veilreach.registry.VeilreachItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HushlandsProgression {
    public static final int DEFEATED_WARDEN = 1;
    public static final int COMPLETED_CATHEDRAL = 1 << 1;
    public static final int COMPLETED_SANCTUM = 1 << 2;
    public static final int DEFEATED_SOVEREIGN = 1 << 3;
    private static final Map<UUID, Integer> LAST_SYNCED_FLAGS = new HashMap<>();

    private HushlandsProgression() {
    }

    public static HushlandsProgressAccess get(PlayerEntity player) {
        return (HushlandsProgressAccess) player;
    }

    public static boolean refreshDerivedMilestones(ServerPlayerEntity player) {
        boolean changed = false;
        HushlandsProgressAccess progress = get(player);

        if (!progress.veilreach$hasDefeatedWarden() && hasItem(player, VeilreachItems.WARDEN_SIGIL)) {
            progress.veilreach$setDefeatedWarden(true);
            changed = true;
        }
        if (!progress.veilreach$hasCompletedCathedral() && hasItem(player, VeilreachItems.CATHEDRAL_SEAL)) {
            progress.veilreach$setCompletedCathedral(true);
            changed = true;
        }
        if (!progress.veilreach$hasCompletedSanctum() && hasItem(player, VeilreachItems.SANCTUM_KEY)) {
            progress.veilreach$setCompletedSanctum(true);
            changed = true;
        }
        if (!progress.veilreach$hasDefeatedSovereign() && (hasItem(player, VeilreachItems.SOVEREIGN_WARD) || hasItem(player, VeilreachItems.SOVEREIGN_CORE))) {
            progress.veilreach$setDefeatedSovereign(true);
            changed = true;
        }

        return changed;
    }

    public static void markWardenDefeated(ServerPlayerEntity player) {
        HushlandsProgressAccess progress = get(player);
        if (!progress.veilreach$hasDefeatedWarden()) {
            progress.veilreach$setDefeatedWarden(true);
        }
    }

    public static void markCathedralCompleted(ServerPlayerEntity player) {
        HushlandsProgressAccess progress = get(player);
        if (!progress.veilreach$hasCompletedCathedral()) {
            progress.veilreach$setCompletedCathedral(true);
        }
    }

    public static void markSanctumCompleted(ServerPlayerEntity player) {
        HushlandsProgressAccess progress = get(player);
        if (!progress.veilreach$hasCompletedSanctum()) {
            progress.veilreach$setCompletedSanctum(true);
        }
    }

    public static void markSovereignDefeated(ServerPlayerEntity player) {
        HushlandsProgressAccess progress = get(player);
        if (!progress.veilreach$hasDefeatedSovereign()) {
            progress.veilreach$setDefeatedSovereign(true);
        }
    }

    public static int getFlags(PlayerEntity player) {
        HushlandsProgressAccess progress = get(player);
        int flags = 0;
        if (progress.veilreach$hasDefeatedWarden()) {
            flags |= DEFEATED_WARDEN;
        }
        if (progress.veilreach$hasCompletedCathedral()) {
            flags |= COMPLETED_CATHEDRAL;
        }
        if (progress.veilreach$hasCompletedSanctum()) {
            flags |= COMPLETED_SANCTUM;
        }
        if (progress.veilreach$hasDefeatedSovereign()) {
            flags |= DEFEATED_SOVEREIGN;
        }
        return flags;
    }

    public static void sync(ServerPlayerEntity player, boolean force) {
        int flags = getFlags(player);
        Integer previous = LAST_SYNCED_FLAGS.get(player.getUuid());
        if (!force && previous != null && previous == flags) {
            return;
        }
        LAST_SYNCED_FLAGS.put(player.getUuid(), flags);
        ServerPlayNetworking.send(player, new HushlandsProgressPayload(flags));
    }

    public static void clearSyncCache(ServerPlayerEntity player) {
        LAST_SYNCED_FLAGS.remove(player.getUuid());
    }

    public static boolean hasMidWard(PlayerEntity player) {
        return hasItem(player, VeilreachItems.HUSH_WARD_CHARM) || hasItem(player, VeilreachItems.SOVEREIGN_WARD);
    }

    public static boolean hasInnerWard(PlayerEntity player) {
        return hasItem(player, VeilreachItems.SOVEREIGN_WARD) || (getFlags(player) & DEFEATED_SOVEREIGN) != 0;
    }

    public static boolean hasAnyWard(PlayerEntity player) {
        return hasMidWard(player) || hasInnerWard(player);
    }

    public static int clampFlags(int flags) {
        return MathHelper.clamp(flags, 0, DEFEATED_WARDEN | COMPLETED_CATHEDRAL | COMPLETED_SANCTUM | DEFEATED_SOVEREIGN);
    }

    private static boolean hasItem(PlayerEntity player, net.minecraft.item.Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(item)) {
                return true;
            }
        }
        return false;
    }
}
