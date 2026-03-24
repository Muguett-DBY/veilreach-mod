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

public class FallenGatewayFeature extends Feature<DefaultFeatureConfig> {
    public FallenGatewayFeature(Codec<DefaultFeatureConfig> codec) {
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

        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 4, 6, 3);

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
            }
        }

        BlockPos origin = center.add(-1, 0, 0);
        for (int w = 0; w <= 3; w++) {
            for (int h = 0; h <= 4; h++) {
                boolean edge = w == 0 || w == 3 || h == 0 || h == 4;
                if (!edge) {
                    continue;
                }
                if ((w == 3 && h >= 3) || (w == 2 && h == 4)) {
                    continue;
                }
                world.setBlockState(origin.add(w, h, 0), frame, Block.NOTIFY_ALL);
            }
        }

        world.setBlockState(origin.add(-1, 1, 0), VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState(), Block.NOTIFY_ALL);
        world.setBlockState(origin.add(4, 0, 0), VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState(), Block.NOTIFY_ALL);
        world.setBlockState(origin.add(1, 4, -1), lantern, Block.NOTIFY_ALL);
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 0, 2), Direction.NORTH, "chests/fallen_gateway");
        return true;
    }
}
