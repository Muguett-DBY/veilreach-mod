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

public class ShimmerleafGroveFeature extends Feature<DefaultFeatureConfig> {
    public ShimmerleafGroveFeature(Codec<DefaultFeatureConfig> codec) {
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

        HushStructureFeatureSupport.clearBox(world, center, 5, 8, 5);

        for (BlockPos treeCenter : new BlockPos[]{center, center.add(3, 0, 1), center.add(-2, 0, -2)}) {
            growTree(world, treeCenter);
        }

        world.setBlockState(center.add(1, -1, -3), VeilreachBlocks.MOONMOSS.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(-1, -1, 3), VeilreachBlocks.MIRROR_GRASS.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 0, 4), Direction.NORTH, "chests/shimmerleaf_grove");
        return true;
    }

    private void growTree(StructureWorldAccess world, BlockPos pos) {
        BlockState log = VeilreachBlocks.GLOAMWOOD_LOG.getDefaultState();
        BlockState leaves = VeilreachBlocks.SHIMMERLEAF_LEAVES.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        for (int dy = 0; dy <= 3; dy++) {
            world.setBlockState(pos.up(dy), log, HushStructureFeatureSupport.GEN_FLAGS);
        }

        BlockPos canopy = pos.up(4);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                    continue;
                }
                world.setBlockState(canopy.add(dx, 0, dz), leaves, HushStructureFeatureSupport.GEN_FLAGS);
                if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) {
                    world.setBlockState(canopy.add(dx, 1, dz), leaves, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        world.setBlockState(canopy.down().north(), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }
}

