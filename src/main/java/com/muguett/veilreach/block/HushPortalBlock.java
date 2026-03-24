package com.muguett.veilreach.block;

import com.muguett.veilreach.world.dimension.HushPortalManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class HushPortalBlock extends Block {
    private static final VoxelShape PORTAL_SHAPE = VoxelShapes.empty();

    public HushPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean fallback) {
        super.onEntityCollision(state, world, pos, entity, handler, fallback);
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }
        if (player.hasPortalCooldown() || player.isSpectator()) {
            return;
        }
        HushPortalManager.teleport(player, pos);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer && !serverPlayer.hasPortalCooldown() && !serverPlayer.isSpectator()) {
            HushPortalManager.teleport(serverPlayer, pos);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return PORTAL_SHAPE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return PORTAL_SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(18) == 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.45F, 0.8F + (random.nextFloat() * 0.35F));
        }
    }
}
