package com.muguett.veilreach.block;

import com.muguett.veilreach.item.VehicleDeployItem;
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

public final class VehicleDeploymentPadBlock extends Block {
    public VehicleDeploymentPadBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack held = player.getStackInHand(Hand.MAIN_HAND);
        if (!(held.getItem() instanceof VehicleDeployItem vehicleDeployItem)) {
            if (!world.isClient()) {
                player.sendMessage(Text.translatable("message.veilreach.vehicle.deployment_pad.ready"), true);
            }
            return ActionResult.SUCCESS;
        }

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        ActionResult result = vehicleDeployItem.spawnVehicle(held, serverWorld, pos.toCenterPos().add(0.0D, 1.2D, 0.0D), player.getYaw(), player);
        if (result.isAccepted()) {
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_ACTIVATE, SoundCategory.BLOCKS, 0.85F, 1.05F);
        }
        return result;
    }
}
