package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
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
        BlockPos center = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin());
        if (!HushStructureFeatureSupport.isHushGround(world, center.down())) {
            return false;
        }

        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 4, 13, 4);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
            }
        }

        for (int x = -2; x <= 2; x += 4) {
            for (int z = -2; z <= 2; z += 4) {
                for (int dy = 0; dy <= 10; dy++) {
                    world.setBlockState(center.add(x, dy, z), frame, Block.NOTIFY_ALL);
                }
            }
        }

        for (int level = 2; level <= 8; level += 3) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                        world.setBlockState(center.add(dx, level, dz), polished, Block.NOTIFY_ALL);
                    }
                }
            }
        }

        world.setBlockState(center.up(11), lantern, Block.NOTIFY_ALL);
        world.setBlockState(center.east(2).up(9), lantern, Block.NOTIFY_ALL);
        world.setBlockState(center.west(2).up(9), lantern, Block.NOTIFY_ALL);
        world.setBlockState(center.north(2).up(9), lantern, Block.NOTIFY_ALL);
        world.setBlockState(center.south(2).up(9), lantern, Block.NOTIFY_ALL);
        HushStructureFeatureSupport.placeLootChest(world, center.south(2), Direction.NORTH, "chests/lantern_watchpost");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 6, -1), Direction.SOUTH, "chests/lantern_watchpost");
        return true;
    }
}
