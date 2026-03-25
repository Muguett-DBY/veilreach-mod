package com.muguett.veilreach.block;

import com.muguett.veilreach.progress.HushlandsProgression;
import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.world.HushlandsRegion;
import com.muguett.veilreach.world.HushlandsRegionResolver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FogboundBarrierBlock extends Block {
    public FogboundBarrierBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!canOpenBarrier(player, pos)) {
            if (!world.isClient()) {
                player.sendMessage(Text.translatable(getLockedMessageKey(pos)), true);
            }
            return ActionResult.SUCCESS;
        }

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        serverWorld.setBlockState(pos, VeilreachBlocks.RIFTGLASS.getDefaultState(), Block.NOTIFY_ALL);
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.7F, 0.7F);
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 0.9F, 1.2F);
        player.sendMessage(Text.translatable("message.veilreach.fogbound_barrier.opened"), true);
        return ActionResult.SUCCESS;
    }

    private static boolean canOpenBarrier(PlayerEntity player, BlockPos pos) {
        if (player.isCreative()) {
            return true;
        }

        HushlandsRegion region = HushlandsRegionResolver.getRegion(pos);
        if (region == HushlandsRegion.INNER) {
            return hasItem(player, VeilreachItems.SOVEREIGN_WARD)
                    || hasItem(player, VeilreachItems.WARDEN_SIGIL)
                    || HushlandsProgression.hasInnerWard(player);
        }

        return hasItem(player, VeilreachItems.HUSH_WARD_CHARM)
                || hasItem(player, VeilreachItems.SOVEREIGN_WARD)
                || HushlandsProgression.hasMidWard(player);
    }

    private static String getLockedMessageKey(BlockPos pos) {
        return HushlandsRegionResolver.getRegion(pos) == HushlandsRegion.INNER
                ? "message.veilreach.fogbound_barrier.locked_inner"
                : "message.veilreach.fogbound_barrier.locked_mid";
    }

    private static boolean hasItem(PlayerEntity player, net.minecraft.item.Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(item)) {
                return true;
            }
        }
        return player.getStackInHand(Hand.MAIN_HAND).isOf(item) || player.getStackInHand(Hand.OFF_HAND).isOf(item);
    }
}
