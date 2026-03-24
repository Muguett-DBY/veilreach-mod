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

public class FallenGatewayFeature extends Feature<DefaultFeatureConfig> {
    public FallenGatewayFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 7, 6, 2, 9, 9)) {
            return false;
        }

        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState ironBars = Blocks.IRON_BARS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 8, 8, 6);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -5, 5, 0, polished);

        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockState floor = Math.abs(dx) == 6 || Math.abs(dz) == 5 ? polished : veilstone;
                world.setBlockState(center.add(dx, 0, dz), floor, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int step = 0; step <= 2; step++) {
            for (int dx = -2; dx <= 2; dx++) {
                world.setBlockState(center.add(dx, step, 4 + step), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        BlockPos origin = center.add(-1, 0, 0);
        origin = center.add(-2, 1, 0);
        for (int w = 0; w <= 4; w++) {
            for (int h = 0; h <= 5; h++) {
                boolean edge = w == 0 || w == 4 || h == 0 || h == 5;
                if (!edge) {
                    continue;
                }
                if (w == 2 && h <= 4) {
                    continue;
                }
                if ((w == 4 && h >= 4) || (w == 3 && h == 5)) {
                    continue;
                }
                world.setBlockState(origin.add(w, h, 0), frame, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int side = -1; side <= 1; side += 2) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 1; dy <= 3; dy++) {
                    world.setBlockState(center.add(side * 4, dy, dz), dy == 3 ? polished : veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = 1; dy <= 3; dy++) {
                world.setBlockState(center.add(dx, dy, -4), dy == 3 ? polished : veilstone, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        world.setBlockState(center.add(-1, 2, -4), ironBars, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(1, 2, -4), ironBars, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(-4, 2, 0), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(4, 1, 0), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 6, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, -3), Direction.SOUTH, "chests/fallen_gateway");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, 4), Direction.NORTH, "chests/fallen_gateway");
        return true;
    }
}

