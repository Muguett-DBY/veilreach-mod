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

public class GloamwoodCampFeature extends Feature<DefaultFeatureConfig> {
    public GloamwoodCampFeature(Codec<DefaultFeatureConfig> codec) {
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

        BlockState planks = VeilreachBlocks.GLOAMWOOD_PLANKS.getDefaultState();
        BlockState log = VeilreachBlocks.GLOAMWOOD_LOG.getDefaultState();
        BlockState leaves = VeilreachBlocks.SHIMMERLEAF_LEAVES.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 3, 5, 3);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, -1, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (BlockPos pillar : new BlockPos[]{center.add(-2, 0, -2), center.add(2, 0, -2), center.add(-2, 0, 2), center.add(2, 0, 2)}) {
            world.setBlockState(pillar, log, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(pillar.up(), log, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(pillar.up(2), log, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(pillar.up(3), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                    world.setBlockState(center.add(dx, 3, dz), leaves, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        HushStructureFeatureSupport.placeLootChest(world, center, Direction.SOUTH, "chests/gloamwood_camp");
        world.setBlockState(center.north(), VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        return true;
    }
}

