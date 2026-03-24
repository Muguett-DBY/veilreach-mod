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

public class WatcherObeliskFeature extends Feature<DefaultFeatureConfig> {
    public WatcherObeliskFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin());
        BlockPos floor = center.down();
        if (!HushStructureFeatureSupport.isHushGround(world, floor)) {
            return false;
        }

        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 3, 7, 3);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, -1, dz), (Math.abs(dx) == 2 || Math.abs(dz) == 2) ? polished : veilstone, Block.NOTIFY_ALL);
            }
        }

        for (int dy = 0; dy <= 4; dy++) {
            world.setBlockState(center.up(dy), polished, Block.NOTIFY_ALL);
        }
        world.setBlockState(center.up(5), lantern, Block.NOTIFY_ALL);
        world.setBlockState(center.north(), barrier, Block.NOTIFY_ALL);
        world.setBlockState(center.south(), barrier, Block.NOTIFY_ALL);
        world.setBlockState(center.east(), barrier, Block.NOTIFY_ALL);
        world.setBlockState(center.west(), barrier, Block.NOTIFY_ALL);

        HushStructureFeatureSupport.placeLootChest(world, center.south(2), Direction.NORTH, "chests/watcher_obelisk");
        world.setBlockState(center.north(2), VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), Block.NOTIFY_ALL);
        return true;
    }
}
