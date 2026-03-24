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

public class RiftSpireFeature extends Feature<DefaultFeatureConfig> {
    public RiftSpireFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 10, 10, 2, 20, 16)) {
            return false;
        }

        BlockState base = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState wall = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState glass = VeilreachBlocks.RIFTGLASS.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 8, 30, 8);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -6, 6, -1, base);

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                if (Math.abs(dx) <= 3 && Math.abs(dz) <= 3) {
                    world.setBlockState(center.add(dx, 0, dz), Math.abs(dx) == 3 || Math.abs(dz) == 3 ? wall : glass, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int y = 1; y <= 20; y++) {
            world.setBlockState(center.up(y), y >= 15 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
            if (y >= 6 && y <= 16 && y % 2 == 0) {
                world.setBlockState(center.up(y).north(), glass, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.up(y).south(), glass, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.up(y).east(), glass, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.up(y).west(), glass, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos shard = center.offset(direction, 3);
            for (int y = 1; y <= 9; y++) {
                world.setBlockState(shard.up(y), y >= 7 ? frame : glass, HushStructureFeatureSupport.GEN_FLAGS);
            }
            world.setBlockState(shard.up(10), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        world.setBlockState(center.up(21), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 1, 2), VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        return true;
    }
}
