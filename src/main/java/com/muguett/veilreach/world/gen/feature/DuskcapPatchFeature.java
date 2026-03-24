package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DuskcapPatchFeature extends Feature<DefaultFeatureConfig> {
    public DuskcapPatchFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin());
        if (!HushStructureFeatureSupport.isHushGround(world, origin.down())) {
            return false;
        }

        BlockState cap = VeilreachBlocks.DUSKCAP.getDefaultState();
        int placed = 0;
        for (int i = 0; i < 20; i++) {
            BlockPos pos = origin.add(context.getRandom().nextInt(8) - 4, 0, context.getRandom().nextInt(8) - 4);
            pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
            if (world.getBlockState(pos).isAir() && HushStructureFeatureSupport.isHushGround(world, pos.down())) {
                world.setBlockState(pos, cap, Block.NOTIFY_ALL);
                placed++;
            }
        }
        return placed > 0;
    }
}
