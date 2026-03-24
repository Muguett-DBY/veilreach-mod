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

public class SilentArchiveFeature extends Feature<DefaultFeatureConfig> {
    public SilentArchiveFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin());
        if (!HushStructureFeatureSupport.isHushGround(world, center.down())) {
            return false;
        }

        BlockState planks = VeilreachBlocks.GLOAMWOOD_PLANKS.getDefaultState();
        BlockState log = VeilreachBlocks.GLOAMWOOD_LOG.getDefaultState();
        BlockState leaves = VeilreachBlocks.SHIMMERLEAF_LEAVES.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 6, 9, 6);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                world.setBlockState(center.add(dx, -1, dz), planks, Block.NOTIFY_ALL);
            }
        }

        for (int x = -4; x <= 4; x += 8) {
            for (int z = -4; z <= 4; z += 8) {
                for (int dy = 0; dy <= 5; dy++) {
                    world.setBlockState(center.add(x, dy, z), log, Block.NOTIFY_ALL);
                }
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.abs(dx) == 4 || Math.abs(dz) == 4) {
                    world.setBlockState(center.add(dx, 6, dz), leaves, Block.NOTIFY_ALL);
                } else {
                    world.setBlockState(center.add(dx, 3, dz), planks, Block.NOTIFY_ALL);
                }
            }
        }

        world.setBlockState(center, barrier, Block.NOTIFY_ALL);
        HushStructureFeatureSupport.placeLootChest(world, center.east(3), Direction.WEST, "chests/silent_archive");
        HushStructureFeatureSupport.placeLootChest(world, center.west(3), Direction.EAST, "chests/silent_archive");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 4, -2), Direction.SOUTH, "chests/silent_archive");
        return true;
    }
}
