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

public class GloamwoodCampFeature extends Feature<DefaultFeatureConfig> {
    public GloamwoodCampFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 7, 7, 2, 9, 8)) {
            return false;
        }

        BlockState planks = VeilreachBlocks.GLOAMWOOD_PLANKS.getDefaultState();
        BlockState log = VeilreachBlocks.GLOAMWOOD_LOG.getDefaultState();
        BlockState leaves = VeilreachBlocks.SHIMMERLEAF_LEAVES.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState fence = Blocks.DARK_OAK_FENCE.getDefaultState();
        BlockState campfire = Blocks.CAMPFIRE.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 8, 7, 8);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -6, 6, 0, polished);

        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                if (Math.abs(dx) == 6 || Math.abs(dz) == 6) {
                    if (!(dz == 6 && Math.abs(dx) <= 1)) {
                        world.setBlockState(center.add(dx, 0, dz), log, HushStructureFeatureSupport.GEN_FLAGS);
                        world.setBlockState(center.add(dx, 1, dz), fence, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                } else if (Math.abs(dx) <= 5 && Math.abs(dz) <= 5) {
                    world.setBlockState(center.add(dx, 0, dz), dx == 0 || dz == 0 ? polished : VeilreachBlocks.MIRROR_GRASS.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            world.setBlockState(center.add(dx, 0, 6), polished, HushStructureFeatureSupport.GEN_FLAGS);
        }
        world.setBlockState(center, campfire, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.north(), planks, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.south(), planks, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.east(), planks, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.west(), planks, HushStructureFeatureSupport.GEN_FLAGS);

        for (int dx = -5; dx <= -1; dx++) {
            for (int dz = -4; dz <= -1; dz++) {
                world.setBlockState(center.add(dx, 0, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
                if (dx == -5 || dx == -1 || dz == -4 || dz == -1) {
                    world.setBlockState(center.add(dx, 1, dz), log, HushStructureFeatureSupport.GEN_FLAGS);
                    world.setBlockState(center.add(dx, 2, dz), log, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }
        for (int dx = -5; dx <= -1; dx++) {
            for (int dz = -4; dz <= -1; dz++) {
                if (dx == -5 || dx == -1 || dz == -4 || dz == -1) {
                    world.setBlockState(center.add(dx, 3, dz), leaves, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int dx = 2; dx <= 5; dx++) {
            for (int dz = -3; dz <= -1; dz++) {
                world.setBlockState(center.add(dx, 1, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (BlockPos post : new BlockPos[]{center.add(2, 2, -3), center.add(5, 2, -3), center.add(2, 2, -1), center.add(5, 2, -1)}) {
            world.setBlockState(post, log, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(post.up(), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        HushStructureFeatureSupport.placeLootChest(world, center.add(-3, 1, -2), Direction.SOUTH, "chests/gloamwood_camp");
        HushStructureFeatureSupport.placeLootChest(world, center.add(4, 2, -2), Direction.WEST, "chests/gloamwood_camp");
        world.setBlockState(center.add(0, 0, -5), VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        return true;
    }
}

