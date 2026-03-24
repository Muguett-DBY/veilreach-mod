package com.muguett.veilreach.block;

import com.muguett.veilreach.world.dimension.HushPortalManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HushPortalBlock extends Block {
    public HushPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean fallback) {
        super.onEntityCollision(state, world, pos, entity, handler, fallback);
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }
        if (player.hasPortalCooldown() || player.isSpectator() || !player.canUsePortals(false)) {
            return;
        }
        HushPortalManager.teleport(player, pos);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(18) == 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.45F, 0.8F + (random.nextFloat() * 0.35F));
        }
    }
}
