package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
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
        BlockPos center = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin());
        BlockPos base = center.down();
        if (!world.getBlockState(base).isOf(VeilreachBlocks.MIRROR_GRASS) && !world.getBlockState(base).isOf(VeilreachBlocks.VEILSTONE)) {
            return false;
        }

        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState altar = VeilreachBlocks.SEALED_ALTAR.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState cache = VeilreachBlocks.STARLIGHT_CACHE.getDefaultState();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos floor = center.add(dx, -1, dz);
                world.setBlockState(floor, Math.abs(dx) == 2 || Math.abs(dz) == 2 ? polished : veilstone, Block.NOTIFY_ALL);
                for (int clearY = 0; clearY <= 4; clearY++) {
                    world.setBlockState(center.add(dx, clearY, dz), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos pillarBase = center.offset(direction, 2);
            world.setBlockState(pillarBase, polished, Block.NOTIFY_ALL);
            world.setBlockState(pillarBase.up(), polished, Block.NOTIFY_ALL);
            world.setBlockState(pillarBase.up(2), lantern, Block.NOTIFY_ALL);
        }

        world.setBlockState(center, altar, Block.NOTIFY_ALL);
        world.setBlockState(center.north(), barrier, Block.NOTIFY_ALL);
        world.setBlockState(center.south(), barrier, Block.NOTIFY_ALL);
        world.setBlockState(center.north(2), cache, Block.NOTIFY_ALL);
        world.setBlockState(center.south(2), cache, Block.NOTIFY_ALL);
        return true;
    }
}
