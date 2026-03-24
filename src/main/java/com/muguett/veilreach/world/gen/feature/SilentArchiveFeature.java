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

public class SilentArchiveFeature extends Feature<DefaultFeatureConfig> {
    public SilentArchiveFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 7, 6, 2, 9, 8)) {
            return false;
        }

        BlockState planks = VeilreachBlocks.GLOAMWOOD_PLANKS.getDefaultState();
        BlockState log = VeilreachBlocks.GLOAMWOOD_LOG.getDefaultState();
        BlockState leaves = VeilreachBlocks.SHIMMERLEAF_LEAVES.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState books = Blocks.BOOKSHELF.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 8, 11, 7);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -5, 5, 0, polished);
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                world.setBlockState(center.add(dx, 0, dz), Math.abs(dx) == 6 || Math.abs(dz) == 5 ? polished : planks, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int x = -6; x <= 6; x += 12) {
            for (int z = -5; z <= 5; z += 10) {
                for (int dy = 1; dy <= 7; dy++) {
                    world.setBlockState(center.add(x, dy, z), log, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int dy = 1; dy <= 7; dy++) {
            for (int dx = -6; dx <= 6; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    if (Math.abs(dx) != 6 && Math.abs(dz) != 5) {
                        continue;
                    }
                    if (dy == 3 && dz == 5 && Math.abs(dx) <= 1) {
                        world.setBlockState(center.add(dx, dy, dz), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(center.add(dx, dy, dz), dy == 7 ? leaves : planks, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int dx = -5; dx <= 5; dx++) {
            world.setBlockState(center.add(dx, 4, -3), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(dx, 4, 3), polished, HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int dz = -3; dz <= 3; dz++) {
            world.setBlockState(center.add(-5, 4, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(5, 4, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
        }

        for (int dz = -3; dz <= 3; dz += 6) {
            for (int dx = -4; dx <= 4; dx += 2) {
                world.setBlockState(center.add(dx, 1, dz), books, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 2, dz), books, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int step = 0; step <= 3; step++) {
            world.setBlockState(center.add(-4 + step, 1 + step, 3), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(-4 + step, 2 + step, 3), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }

        world.setBlockState(center.add(-4, 2, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(4, 2, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 5, -3), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.east(4).up(1), Direction.WEST, "chests/silent_archive");
        HushStructureFeatureSupport.placeLootChest(world, center.west(4).up(1), Direction.EAST, "chests/silent_archive");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 5, -4), Direction.SOUTH, "chests/silent_archive");
        return true;
    }
}

