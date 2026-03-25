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

public final class VehicleRepairPadBlock extends Block {
    public VehicleRepairPadBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }
        AbstractVehicleEntity vehicle = VehicleSupportHelper.findVehicle(world, pos, player, 8.0D);
        if (vehicle == null) {
            player.sendMessage(Text.translatable("message.veilreach.vehicle.repair_pad.missing"), true);
            return ActionResult.SUCCESS;
        }

        vehicle.repairFully();
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.95F, 1.05F);
        player.sendMessage(Text.translatable("message.veilreach.vehicle.repair_pad.used"), true);
        return ActionResult.SUCCESS;
    }
}
