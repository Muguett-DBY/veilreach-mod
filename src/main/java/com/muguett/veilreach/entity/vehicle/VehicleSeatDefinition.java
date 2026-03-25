package com.muguett.veilreach.entity.vehicle;

import net.minecraft.util.math.Vec3d;

public record VehicleSeatDefinition(
        VehicleSeatRole role,
        Vec3d seatOffset,
        Vec3d dismountOffset,
        boolean usesFlightHud
) {
}
