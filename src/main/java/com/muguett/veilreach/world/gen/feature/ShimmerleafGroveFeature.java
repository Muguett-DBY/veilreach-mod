package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 7, 7, 2, 9, 8)) {
            return false;
        }

        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState moss = VeilreachBlocks.MOONMOSS.getDefaultState();
        BlockState blossom = VeilreachBlocks.MOONBLOSSOM.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState bars = Blocks.IRON_BARS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 7, 9, 7);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -6, 6, 0, polished);

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockState floor = Math.abs(dx) == 4 || Math.abs(dz) == 4 ? polished : (Math.abs(dx) <= 1 && Math.abs(dz) <= 1 ? moss : veilstone);
                world.setBlockState(center.add(dx, 0, dz), floor, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (BlockPos treeCenter : new BlockPos[]{
                center.add(0, 0, -5),
                center.add(4, 0, -2),
                center.add(-4, 0, 2),
                center.add(1, 0, 5)
        }) {
            growTree(world, treeCenter);
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos arbor = center.offset(direction, 3).up(1);
            world.setBlockState(arbor, bars, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(arbor.up(), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        world.setBlockState(center.up(1), blossom, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.north().up(1), blossom, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.south().up(1), blossom, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, 4), Direction.NORTH, "chests/shimmerleaf_grove");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-4, 1, 2), Direction.EAST, "chests/shimmerleaf_grove");
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

