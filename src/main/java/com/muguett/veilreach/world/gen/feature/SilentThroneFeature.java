package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SilentThroneFeature extends Feature<DefaultFeatureConfig> {
    public SilentThroneFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin());
        if (!hasStableGround(world, center)) {
            return false;
        }

        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState altar = VeilreachBlocks.SEALED_ALTAR.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 13, 16, 13);

        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                BlockState floor = (Math.abs(dx) == 10 || Math.abs(dz) == 10) ? polished : veilstone;
                world.setBlockState(center.add(dx, -1, dz), floor, Block.NOTIFY_ALL);
            }
        }

        for (int ring = 10; ring >= 4; ring -= 3) {
            for (int dx = -ring; dx <= ring; dx++) {
                world.setBlockState(center.add(dx, 0, -ring), polished, Block.NOTIFY_ALL);
                world.setBlockState(center.add(dx, 0, ring), polished, Block.NOTIFY_ALL);
            }
            for (int dz = -ring; dz <= ring; dz++) {
                world.setBlockState(center.add(-ring, 0, dz), polished, Block.NOTIFY_ALL);
                world.setBlockState(center.add(ring, 0, dz), polished, Block.NOTIFY_ALL);
            }
        }

        for (int x = -8; x <= 8; x += 16) {
            for (int z = -8; z <= 8; z += 16) {
                for (int dy = 0; dy <= 9; dy++) {
                    world.setBlockState(center.add(x, dy, z), frame, Block.NOTIFY_ALL);
                }
                world.setBlockState(center.add(x, 10, z), lantern, Block.NOTIFY_ALL);
            }
        }

        for (int step = 0; step <= 4; step++) {
            for (int x = -2; x <= 2; x++) {
                world.setBlockState(center.add(x, step, -6 + step), polished, Block.NOTIFY_ALL);
            }
        }

        for (int x = -2; x <= 2; x++) {
            for (int y = 5; y <= 7; y++) {
                world.setBlockState(center.add(x, y, -2), barrier, Block.NOTIFY_ALL);
            }
        }

        world.setBlockState(center.add(0, 5, -4), altar, Block.NOTIFY_ALL);
        world.setBlockState(center.add(-2, 5, -5), lantern, Block.NOTIFY_ALL);
        world.setBlockState(center.add(2, 5, -5), lantern, Block.NOTIFY_ALL);

        HushStructureFeatureSupport.placeLootChest(world, center.add(-6, 1, 6), Direction.NORTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(6, 1, 6), Direction.NORTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 6, -6), Direction.SOUTH, "chests/silent_throne");
        return true;
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        for (int dx = -10; dx <= 10; dx += 4) {
            for (int dz = -10; dz <= 10; dz += 4) {
                if (!HushStructureFeatureSupport.isHushGround(world, center.add(dx, -1, dz))) {
                    return false;
                }
            }
        }
        return true;
    }
}
