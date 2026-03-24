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

public class EchoMonolithFeature extends Feature<DefaultFeatureConfig> {
    public EchoMonolithFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 6, 6, 2, 9, 10)) {
            return false;
        }

        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState altar = VeilreachBlocks.SEALED_ALTAR.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState cache = VeilreachBlocks.STARLIGHT_CACHE.getDefaultState();
        BlockState chiseled = Blocks.CHISELED_DEEPSLATE.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 7, 11, 7);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -6, 6, 0, polished);

        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                int ring = Math.max(Math.abs(dx), Math.abs(dz));
                BlockPos pos = center.add(dx, 0, dz);
                BlockState floor = ring >= 5 ? polished : (dx == 0 || dz == 0 ? chiseled : veilstone);
                world.setBlockState(pos, floor, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, 1, dz), Math.abs(dx) == 2 || Math.abs(dz) == 2 ? polished : chiseled, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int step = 0; step <= 2; step++) {
            for (int dx = -2; dx <= 2; dx++) {
                world.setBlockState(center.add(dx, step, 4 + step), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos pylon = center.offset(direction, 4).up(1);
            for (int dy = 0; dy <= 3; dy++) {
                world.setBlockState(pylon.up(dy), dy == 3 ? frame : polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
            world.setBlockState(pylon.up(4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        for (int dy = 2; dy <= 7; dy++) {
            world.setBlockState(center.up(dy), dy >= 6 ? frame : polished, HushStructureFeatureSupport.GEN_FLAGS);
        }
        world.setBlockState(center.up(8), lantern, HushStructureFeatureSupport.GEN_FLAGS);

        world.setBlockState(center.up(2), altar, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.up(2).north(), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.up(2).south(), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.up(2).east(), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.up(2).west(), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.north(5).up(1), cache, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.south(1).up(1), cache, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.east(4).up(1), Direction.WEST, "chests/echo_monolith");
        HushStructureFeatureSupport.placeLootChest(world, center.west(4).up(1), Direction.EAST, "chests/echo_monolith");
        return true;
    }
}

