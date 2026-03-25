package com.muguett.veilreach.block;

import com.muguett.veilreach.entity.vehicle.AbstractVehicleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;

final class VehicleSupportHelper {
    private VehicleSupportHelper() {
    }

    static AbstractVehicleEntity findVehicle(World world, BlockPos pos, PlayerEntity player, double range) {
        if (player.getVehicle() instanceof AbstractVehicleEntity vehicle && vehicle.squaredDistanceTo(pos.toCenterPos()) <= (range * range)) {
            return vehicle;
        }
        Box searchBox = new Box(pos).expand(range);
        return world.getEntitiesByClass(AbstractVehicleEntity.class, searchBox, entity -> entity.isAlive() && !entity.isRemoved())
                .stream()
                .min(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(pos.toCenterPos())))
                .orElse(null);
    }

    static void moveVehicleToPad(AbstractVehicleEntity vehicle, Vec3d destination, float yaw) {
        vehicle.stopRiding();
        vehicle.setVelocity(Vec3d.ZERO);
        vehicle.refreshPositionAndAngles(destination.x, destination.y, destination.z, yaw, 0.0F);
    }
}
