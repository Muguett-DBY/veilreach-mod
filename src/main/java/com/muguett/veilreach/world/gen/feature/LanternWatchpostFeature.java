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

public class LanternWatchpostFeature extends Feature<DefaultFeatureConfig> {
    public LanternWatchpostFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 6, 6, 2, 9, 8)) {
            return false;
        }

        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState planks = Blocks.DARK_OAK_PLANKS.getDefaultState();
        BlockState bars = Blocks.IRON_BARS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 6, 14, 6);
        HushStructureFeatureSupport.supportFootprint(world, center, -4, 4, -4, 4, 0, polished);

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                world.setBlockState(center.add(dx, 0, dz), Math.abs(dx) == 4 || Math.abs(dz) == 4 ? polished : planks, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int x = -3; x <= 3; x += 6) {
            for (int z = -3; z <= 3; z += 6) {
                for (int dy = 1; dy <= 10; dy++) {
                    world.setBlockState(center.add(x, dy, z), frame, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int level : new int[]{3, 7, 10}) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    if (Math.abs(dx) == 3 || Math.abs(dz) == 3) {
                        world.setBlockState(center.add(dx, level, dz), level == 10 ? polished : planks, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int dy = 1; dy <= 3; dy++) {
            world.setBlockState(center.add(0, dy, 4), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int step = 0; step <= 4; step++) {
            world.setBlockState(center.add(-2 + step, 1 + step, 2), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(-2 + step, 2 + step, 2), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }

        world.setBlockState(center.add(0, 11, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.east(3).up(8), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.west(3).up(8), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.north(3).up(8), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.south(3).up(8), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 2, -3), bars, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, -2), Direction.SOUTH, "chests/lantern_watchpost");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 8, -1), Direction.SOUTH, "chests/lantern_watchpost");
        return true;
    }
}

