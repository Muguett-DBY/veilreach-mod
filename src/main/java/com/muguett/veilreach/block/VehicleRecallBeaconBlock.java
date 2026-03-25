package com.muguett.veilreach.block;

import com.muguett.veilreach.entity.vehicle.AbstractVehicleEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class VehicleRecallBeaconBlock extends Block {
    public VehicleRecallBeaconBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }
        AbstractVehicleEntity vehicle = VehicleSupportHelper.findVehicle(world, pos, player, 48.0D);
        if (vehicle == null) {
            player.sendMessage(Text.translatable("message.veilreach.vehicle.recall_beacon.missing"), true);
            return ActionResult.SUCCESS;
        }

        VehicleSupportHelper.moveVehicleToPad(vehicle, pos.toCenterPos().add(0.0D, 1.2D, 0.0D), player.getYaw());
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 0.9F, 1.15F);
        player.sendMessage(Text.translatable("message.veilreach.vehicle.recall_beacon.used"), true);
        return ActionResult.SUCCESS;
    }
}
