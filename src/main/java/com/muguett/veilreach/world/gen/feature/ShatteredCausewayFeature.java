package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ShatteredCausewayFeature extends Feature<DefaultFeatureConfig> {
    public ShatteredCausewayFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!HushStructureFeatureSupport.hasStableGround(world, center, 8, 8, 2, 16, 10)) {
            return false;
        }

        BlockState base = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState wall = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState trim = Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 7, 14, 7);
        HushStructureFeatureSupport.supportFootprint(world, center, -7, 7, -4, 4, -1, base);

        for (int span = -7; span <= 7; span++) {
            if (span == 0) {
                continue;
            }
            int deckY = Math.abs(span) >= 5 ? 1 : 0;
            int widthLimit = Math.abs(span) >= 6 ? 1 : 2;
            for (int width = -widthLimit; width <= widthLimit; width++) {
                BlockPos deck = center.add(span, deckY, width);
                world.setBlockState(deck.down(), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(deck, Math.abs(width) == widthLimit ? trim : base, HushStructureFeatureSupport.GEN_FLAGS);
                HushStructureFeatureSupport.fillDown(world, deck.down(), wall);
            }
            if (Math.abs(span) == 4 || Math.abs(span) == 7) {
                buildPylon(world, center.add(span, deckY, 0), wall, frame, lantern);
            }
        }

        for (int side = -1; side <= 1; side += 2) {
            for (int segment = 0; segment <= 1; segment++) {
                BlockPos shard = center.add(side * (4 + segment * 2), 2 + segment, side * (1 + segment));
                for (int y = 0; y <= 4 + segment; y++) {
                    world.setBlockState(shard.up(y), y >= 3 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        HushStructureFeatureSupport.placeLootChest(world, center.add(-6, 1, 0), Direction.EAST, "chests/echo_monolith");
        HushStructureFeatureSupport.placeLootChest(world, center.add(6, 1, 0), Direction.WEST, "chests/echo_monolith");
        world.setBlockState(center.add(-3, 1, 0), VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(3, 1, 0), VeilreachBlocks.STARLIGHT_CACHE.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        return true;
    }

    private static void buildPylon(StructureWorldAccess world, BlockPos center, BlockState wall, BlockState frame, BlockState lantern) {
        for (int y = 0; y <= 8; y++) {
            world.setBlockState(center.up(y), y >= 6 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
        }
        world.setBlockState(center.up(9), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }
}
