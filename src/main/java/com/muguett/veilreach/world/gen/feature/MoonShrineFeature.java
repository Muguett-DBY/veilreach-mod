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

public class MoonShrineFeature extends Feature<DefaultFeatureConfig> {
    public MoonShrineFeature(Codec<DefaultFeatureConfig> codec) {
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
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState altar = VeilreachBlocks.AWAKENED_ALTAR.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 4, 5, 4);

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos pos = center.add(dx, -1, dz);
                world.setBlockState(pos, (Math.abs(dx) == 3 || Math.abs(dz) == 3) ? polished : veilstone, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (BlockPos pillarBase : new BlockPos[]{center.add(-3, 0, -3), center.add(3, 0, -3), center.add(-3, 0, 3), center.add(3, 0, 3)}) {
            world.setBlockState(pillarBase, polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(pillarBase.up(), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(pillarBase.up(2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        world.setBlockState(center, altar, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.north(), VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.south(), VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeLootChest(world, center.east(2), Direction.WEST, "chests/moon_shrine");
        HushStructureFeatureSupport.placeLootChest(world, center.west(2), Direction.EAST, "chests/moon_shrine");
        return true;
    }
}

