package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class MoonwovenArchFeature extends Feature<DefaultFeatureConfig> {
    public MoonwovenArchFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 14, 14, 3, 20, 12)) {
            return false;
        }

        BlockState base = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState wall = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 12, 22, 8);
        HushStructureFeatureSupport.supportFootprint(world, center, -10, 10, -4, 4, -1, base);

        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                if (Math.abs(dx) <= 7) {
                    world.setBlockState(center.add(dx, 0, dz), Math.abs(dx) == 7 || dz == 0 ? wall : base, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int side = -1; side <= 1; side += 2) {
            for (int y = 0; y <= 14; y++) {
                for (int z = -2; z <= 2; z++) {
                    world.setBlockState(center.add(side * 8, y, z), y >= 10 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
            world.setBlockState(center.add(side * 8, 15, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        for (int x = -7; x <= 7; x++) {
            int archY = 14 + (int) Math.round(3.0D * (1.0D - (Math.abs(x) / 7.0D)));
            for (int thickness = -1; thickness <= 1; thickness++) {
                world.setBlockState(center.add(x, archY + thickness, 0), frame, HushStructureFeatureSupport.GEN_FLAGS);
            }
            if (Math.abs(x) <= 4) {
                world.setBlockState(center.add(x, archY - 2, 0), wall, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos spur = center.offset(direction, 6);
            world.setBlockState(spur.up(2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }
        return true;
    }
}
