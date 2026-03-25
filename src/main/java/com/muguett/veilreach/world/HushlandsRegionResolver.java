package com.muguett.veilreach.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class HushlandsRegionResolver {
    public static final int OUTER_RADIUS = 448;
    public static final int MID_RADIUS = 896;

    private HushlandsRegionResolver() {
    }

    public static HushlandsRegion getRegion(Entity entity) {
        return getRegion(new Vec3d(entity.getX(), entity.getY(), entity.getZ()));
    }

    public static HushlandsRegion getRegion(BlockPos pos) {
        return getRegion(pos.toCenterPos());
    }

    public static HushlandsRegion getRegion(Vec3d pos) {
        double horizontalDistance = Math.sqrt((pos.x * pos.x) + (pos.z * pos.z));
        if (horizontalDistance < OUTER_RADIUS) {
            return HushlandsRegion.OUTER;
        }
        if (horizontalDistance < MID_RADIUS) {
            return HushlandsRegion.MID;
        }
        return HushlandsRegion.INNER;
    }
}
