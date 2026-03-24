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

public class MoonShrineFeature extends Feature<DefaultFeatureConfig> {
    public MoonShrineFeature(Codec<DefaultFeatureConfig> codec) {
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
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState altar = VeilreachBlocks.AWAKENED_ALTAR.getDefaultState();
        BlockState glass = Blocks.IRON_BARS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 7, 9, 7);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -6, 6, 0, polished);

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                int ring = Math.max(Math.abs(dx), Math.abs(dz));
                BlockState floor = ring >= 4 ? polished : veilstone;
                world.setBlockState(center.add(dx, 0, dz), floor, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, 1, dz), Math.abs(dx) == 3 || Math.abs(dz) == 2 ? polished : veilstone, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int step = 0; step <= 2; step++) {
            for (int dx = -2; dx <= 2; dx++) {
                world.setBlockState(center.add(dx, step, 3 + step), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (BlockPos pillarBase : new BlockPos[]{center.add(-3, 2, -2), center.add(3, 2, -2), center.add(-3, 2, 2), center.add(3, 2, 2)}) {
            for (int dy = 0; dy <= 3; dy++) {
                world.setBlockState(pillarBase.up(dy), dy >= 2 ? frame : polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
            world.setBlockState(pillarBase.up(4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = 2; dy <= 5; dy++) {
                if (Math.abs(dx) == 4 || dy == 5) {
                    world.setBlockState(center.add(dx, dy, -4), polished, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        world.setBlockState(center.add(-2, 3, -4), glass, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(2, 3, -4), glass, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.up(2), altar, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.up(2).north(), VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.up(2).south(), VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.east(4).up(1), Direction.WEST, "chests/moon_shrine");
        HushStructureFeatureSupport.placeLootChest(world, center.west(4).up(1), Direction.EAST, "chests/moon_shrine");
        return true;
    }
}

