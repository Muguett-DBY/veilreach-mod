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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class StarlightCacheBlock extends Block {
    public StarlightCacheBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        Random random = serverWorld.getRandom();
        Block.dropStack(serverWorld, pos.up(), new ItemStack(VeilreachItems.MIRROR_INGOT, 2 + random.nextInt(3)));
        Block.dropStack(serverWorld, pos.up(), new ItemStack(VeilreachItems.STARFRAGMENT, 1 + random.nextInt(2)));
        if (random.nextFloat() < 0.55F) {
            Block.dropStack(serverWorld, pos.up(), new ItemStack(VeilreachItems.LUNAR_FIBER, 2));
        }

        serverWorld.setBlockState(pos, VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState(), Block.NOTIFY_ALL);
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.BLOCKS, 0.9F, 1.0F);
        player.sendMessage(Text.translatable("message.veilreach.starlight_cache.opened"), true);
        return ActionResult.SUCCESS;
    }
}
