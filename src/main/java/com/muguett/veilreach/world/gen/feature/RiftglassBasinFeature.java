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

public class RiftglassBasinFeature extends Feature<DefaultFeatureConfig> {
    public RiftglassBasinFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 7, 7, 2, 9, 8)) {
            return false;
        }

        BlockState riftglass = VeilreachBlocks.RIFTGLASS.getDefaultState();
        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState moonmoss = VeilreachBlocks.MOONMOSS.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState bars = Blocks.IRON_BARS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 7, 7, 7);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -6, 6, -2, polished);

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                int ring = Math.max(Math.abs(dx), Math.abs(dz));
                int y = ring >= 4 ? 0 : (ring >= 2 ? -1 : -2);
                BlockState state = ring >= 4 ? polished : (ring >= 2 ? moonmoss : riftglass);
                world.setBlockState(center.add(dx, y, dz), state, HushStructureFeatureSupport.GEN_FLAGS);
                for (int fill = y - 1; fill >= -2; fill--) {
                    world.setBlockState(center.add(dx, fill, dz), state, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int step = 0; step <= 2; step++) {
            world.setBlockState(center.add(0, -step, 5 - step), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(0, -step, 4 - step), polished, HushStructureFeatureSupport.GEN_FLAGS);
        }

        for (Direction direction : new Direction[]{Direction.EAST, Direction.WEST}) {
            BlockPos shard = center.offset(direction, 3).up(1);
            world.setBlockState(shard, frame, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(shard.up(), bars, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(shard.up(2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        world.setBlockState(center.down(1), VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.east(4), Direction.WEST, "chests/riftglass_basin");
        HushStructureFeatureSupport.placeLootChest(world, center.west(4), Direction.EAST, "chests/riftglass_basin");
        return true;
    }
}

