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
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 8, 8, 2, 16, 10)) {
            return false;
        }

        BlockState base = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState wall = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 7, 18, 6);
        HushStructureFeatureSupport.supportFootprint(world, center, -7, 7, -4, 4, -1, base);

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                if (Math.abs(dx) <= 5) {
                    world.setBlockState(center.add(dx, 0, dz), Math.abs(dx) == 5 || dz == 0 ? wall : base, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int side = -1; side <= 1; side += 2) {
            for (int y = 0; y <= 12; y++) {
                for (int z = -2; z <= 2; z++) {
                    world.setBlockState(center.add(side * 6, y, z), y >= 8 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
            world.setBlockState(center.add(side * 6, 13, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        for (int x = -5; x <= 5; x++) {
            int archY = 12 + (int) Math.round(2.0D * (1.0D - (Math.abs(x) / 5.0D)));
            for (int thickness = -1; thickness <= 1; thickness++) {
                world.setBlockState(center.add(x, archY + thickness, 0), frame, HushStructureFeatureSupport.GEN_FLAGS);
            }
            if (Math.abs(x) <= 3) {
                world.setBlockState(center.add(x, archY - 2, 0), wall, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos spur = center.offset(direction, 4);
            world.setBlockState(spur.up(2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }
        return true;
    }
}
