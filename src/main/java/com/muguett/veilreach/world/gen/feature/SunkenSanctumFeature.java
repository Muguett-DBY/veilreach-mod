package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachEntities;
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

public class SunkenSanctumFeature extends Feature<DefaultFeatureConfig> {
    public SunkenSanctumFeature(Codec<DefaultFeatureConfig> codec) {
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
        BlockState altar = VeilreachBlocks.AWAKENED_ALTAR.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState glass = VeilreachBlocks.RIFTGLASS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 9, 12, 9);

        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                if (Math.abs(dx) == 6 || Math.abs(dz) == 6) {
                    world.setBlockState(center.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
                } else {
                    world.setBlockState(center.add(dx, -1, dz), veilstone, Block.NOTIFY_ALL);
                }
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            world.setBlockState(center.add(dx, 0, -5), polished, Block.NOTIFY_ALL);
        }
        for (int dy = 0; dy <= 4; dy++) {
            world.setBlockState(center.add(-4, dy, -5), frame, Block.NOTIFY_ALL);
            world.setBlockState(center.add(4, dy, -5), frame, Block.NOTIFY_ALL);
        }
        for (int dx = -4; dx <= 4; dx++) {
            world.setBlockState(center.add(dx, 4, -5), frame, Block.NOTIFY_ALL);
        }

        for (int dx = -1; dx <= 1; dx++) {
            world.setBlockState(center.add(dx, 1, -5), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            world.setBlockState(center.add(dx, 2, -5), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }

        for (int step = 0; step <= 6; step++) {
            int y = -step;
            int z = -4 + step;
            for (int dx = -1; dx <= 1; dx++) {
                world.setBlockState(center.add(dx, y - 1, z), polished, Block.NOTIFY_ALL);
                for (int dy = 0; dy <= 3; dy++) {
                    world.setBlockState(center.add(dx, y + dy, z), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        BlockPos chamberCenter = center.down(7);
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                for (int dy = -1; dy <= 5; dy++) {
                    BlockPos pos = chamberCenter.add(dx, dy, dz);
                    if (dy == -1) {
                        world.setBlockState(pos, polished, Block.NOTIFY_ALL);
                    } else if (Math.abs(dx) == 6 || Math.abs(dz) == 6 || dy == 5) {
                        world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
            }
        }

        for (int x = -5; x <= 5; x += 10) {
            for (int z = -5; z <= 5; z += 10) {
                for (int dy = 0; dy <= 4; dy++) {
                    world.setBlockState(chamberCenter.add(x, dy, z), frame, Block.NOTIFY_ALL);
                }
                world.setBlockState(chamberCenter.add(x, 4, z), lantern, Block.NOTIFY_ALL);
            }
        }

        for (int dx = -3; dx <= 3; dx++) {
            world.setBlockState(chamberCenter.add(dx, 2, -6), glass, Block.NOTIFY_ALL);
            world.setBlockState(chamberCenter.add(dx, 2, 6), glass, Block.NOTIFY_ALL);
        }

        world.setBlockState(chamberCenter, altar, Block.NOTIFY_ALL);
        world.setBlockState(chamberCenter.north(2), barrier, Block.NOTIFY_ALL);
        world.setBlockState(chamberCenter.south(2), barrier, Block.NOTIFY_ALL);
        carveSideCells(world, chamberCenter, polished, veilstone, lantern);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamberCenter.add(0, 0, -2), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamberCenter.add(2, 0, 2), VeilreachEntities.LANTERNKEEPER);

        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(-4, 0, -4), Direction.SOUTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(4, 0, -4), Direction.SOUTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(0, 0, 4), Direction.NORTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(-4, 0, 4), Direction.NORTH, "chests/sunken_sanctum");
        return true;
    }

    private static void carveSideCells(StructureWorldAccess world, BlockPos chamberCenter, BlockState polished, BlockState veilstone, BlockState lantern) {
        for (int side = -1; side <= 1; side += 2) {
            BlockPos roomCenter = chamberCenter.add(side * 5, 0, 0);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    for (int dy = 0; dy <= 3; dy++) {
                        BlockPos pos = roomCenter.add(dx, dy, dz);
                        if (Math.abs(dx) == 1 || Math.abs(dz) == 2 || dy == 3) {
                            world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                        } else {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                        }
                    }
                    world.setBlockState(roomCenter.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
                }
            }
            world.setBlockState(roomCenter.add(0, 1, 0), lantern, Block.NOTIFY_ALL);
            world.setBlockState(roomCenter.add(0, 0, side * -2), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            world.setBlockState(roomCenter.add(0, 1, side * -2), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        for (int dx = -6; dx <= 6; dx += 3) {
            for (int dz = -6; dz <= 6; dz += 3) {
                if (!HushStructureFeatureSupport.isHushGround(world, center.add(dx, -1, dz))) {
                    return false;
                }
            }
        }
        return true;
    }
}
