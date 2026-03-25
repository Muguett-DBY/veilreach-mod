package com.muguett.veilreach.block;

import com.muguett.veilreach.entity.vehicle.AbstractArmedVehicleEntity;
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

public final class VehicleRearmNodeBlock extends Block {
    public VehicleRearmNodeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }
        AbstractVehicleEntity vehicle = VehicleSupportHelper.findVehicle(world, pos, player, 8.0D);
        if (!(vehicle instanceof AbstractArmedVehicleEntity armedVehicle)) {
            player.sendMessage(Text.translatable("message.veilreach.vehicle.rearm_node.missing"), true);
            return ActionResult.SUCCESS;
        }

        armedVehicle.rearmVehicle();
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 0.92F, 1.2F);
        player.sendMessage(Text.translatable("message.veilreach.vehicle.rearm_node.used"), true);
        return ActionResult.SUCCESS;
    }
}
