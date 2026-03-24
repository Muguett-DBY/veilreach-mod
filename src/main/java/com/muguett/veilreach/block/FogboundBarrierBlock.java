package com.muguett.veilreach.block;

import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
        ItemStack held = player.getStackInHand(Hand.MAIN_HAND);
        if (!held.isOf(VeilreachItems.HUSH_WARD_CHARM)) {
            if (!world.isClient()) {
                player.sendMessage(Text.literal("A heavy veil seals the way. A Hush Ward Charm should steady it."), true);
            }
            return ActionResult.SUCCESS;
        }

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        serverWorld.setBlockState(pos, VeilreachBlocks.RIFTGLASS.getDefaultState(), Block.NOTIFY_ALL);
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.7F, 0.7F);
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 0.9F, 1.2F);
        player.sendMessage(Text.literal("The fogbound seal loosens and the passage opens."), true);
        return ActionResult.SUCCESS;
    }
}
