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

public class WatcherObeliskFeature extends Feature<DefaultFeatureConfig> {
    public WatcherObeliskFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 6, 6, 2, 9, 8)) {
            return false;
        }

        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState chain = Blocks.END_ROD.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 6, 11, 6);
        HushStructureFeatureSupport.supportFootprint(world, center, -5, 5, -5, 5, 0, polished);

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                int ring = Math.max(Math.abs(dx), Math.abs(dz));
                BlockState floor = ring >= 4 ? polished : veilstone;
                world.setBlockState(center.add(dx, 0, dz), floor, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int step = 0; step <= 2; step++) {
            for (int dx = -1; dx <= 1; dx++) {
                world.setBlockState(center.add(dx, step, 4 + step), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int dy = 1; dy <= 7; dy++) {
            world.setBlockState(center.up(dy), dy >= 5 ? frame : polished, HushStructureFeatureSupport.GEN_FLAGS);
        }
        world.setBlockState(center.up(8), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.north().up(5), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.south().up(5), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.east().up(5), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.west().up(5), barrier, HushStructureFeatureSupport.GEN_FLAGS);

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos wing = center.offset(direction, 3).up(1);
            for (int dy = 0; dy <= 2; dy++) {
                world.setBlockState(wing.up(dy), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
            world.setBlockState(wing.up(3), chain, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(wing.up(4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        HushStructureFeatureSupport.placeLootChest(world, center.south(4).up(1), Direction.NORTH, "chests/watcher_obelisk");
        world.setBlockState(center.north(4).up(1), VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        return true;
    }
}

