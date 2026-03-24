package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RiftglassBasinFeature extends Feature<DefaultFeatureConfig> {
    public RiftglassBasinFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin());
        BlockPos floor = center.down();
        if (!HushStructureFeatureSupport.isHushGround(world, floor)) {
            return false;
        }

        BlockState riftglass = VeilreachBlocks.RIFTGLASS.getDefaultState();
        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState moonmoss = VeilreachBlocks.MOONMOSS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 4, 4, 4);

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                double dist = Math.sqrt((dx * dx) + (dz * dz));
                BlockPos pos = center.add(dx, -1, dz);
                if (dist <= 1.7) {
                    world.setBlockState(pos, riftglass, Block.NOTIFY_ALL);
                } else if (dist <= 2.8) {
                    world.setBlockState(pos, moonmoss, Block.NOTIFY_ALL);
                } else {
                    world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                }
            }
        }

        world.setBlockState(center, VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), Block.NOTIFY_ALL);
        HushStructureFeatureSupport.placeLootChest(world, center.east(2), Direction.WEST, "chests/riftglass_basin");
        world.setBlockState(center.west(2), VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState(), Block.NOTIFY_ALL);
        return true;
    }
}
