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

public class SilentThroneFeature extends Feature<DefaultFeatureConfig> {
    public SilentThroneFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!hasStableGround(world, center)) {
            return false;
        }

        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState altar = VeilreachBlocks.SEALED_ALTAR.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 19, 24, 20);
        HushStructureFeatureSupport.supportFootprint(world, center, -10, 10, -10, 10, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -6, 6, -18, -11, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -14, -8, -2, 8, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, 8, 14, -2, 8, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -17, -11, 11, 17, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, 11, 17, 11, 17, -1, polished);

        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                BlockState floor = (Math.abs(dx) == 10 || Math.abs(dz) == 10) ? polished : veilstone;
                world.setBlockState(center.add(dx, -1, dz), floor, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        buildProcessionalWay(world, center, polished, veilstone, frame, lantern);
        buildSideBastion(world, center.add(-11, 0, 3), polished, veilstone, frame, lantern, barrier);
        buildSideBastion(world, center.add(11, 0, 3), polished, veilstone, frame, lantern, barrier);
        buildRearTower(world, center.add(-14, 0, 14), polished, veilstone, frame, lantern);
        buildRearTower(world, center.add(14, 0, 14), polished, veilstone, frame, lantern);

        for (int ring = 10; ring >= 4; ring -= 3) {
            for (int dx = -ring; dx <= ring; dx++) {
                world.setBlockState(center.add(dx, 0, -ring), polished, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, ring), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
            for (int dz = -ring; dz <= ring; dz++) {
                world.setBlockState(center.add(-ring, 0, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(ring, 0, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int x = -8; x <= 8; x += 16) {
            for (int z = -8; z <= 8; z += 16) {
                for (int dy = 0; dy <= 9; dy++) {
                    world.setBlockState(center.add(x, dy, z), frame, HushStructureFeatureSupport.GEN_FLAGS);
                }
                world.setBlockState(center.add(x, 10, z), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int step = 0; step <= 4; step++) {
            for (int x = -2; x <= 2; x++) {
                world.setBlockState(center.add(x, step, -6 + step), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int x = -2; x <= 2; x++) {
            for (int y = 5; y <= 7; y++) {
                world.setBlockState(center.add(x, y, -2), barrier, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        world.setBlockState(center.add(0, 5, -4), altar, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(-2, 5, -5), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(2, 5, -5), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        carveUndercroft(world, center, polished, veilstone, lantern, barrier);

        HushStructureFeatureSupport.placeLootChest(world, center.add(-6, 1, 6), Direction.NORTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(6, 1, 6), Direction.NORTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 6, -6), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, -16), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-11, 1, 3), Direction.EAST, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(11, 1, 3), Direction.WEST, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-14, 10, 14), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, -5, 0), Direction.NORTH, "chests/silent_throne");
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-11, 1, 3), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(11, 1, 3), VeilreachEntities.WARDEN_OF_SILENCE);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-14, 1, 14), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(14, 10, 14), VeilreachEntities.LANTERNKEEPER);
        return true;
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        return HushStructureFeatureSupport.hasStableGround(world, center, 16, 18, 4, 24, 18);
    }

    private static void buildProcessionalWay(
            StructureWorldAccess world,
            BlockPos center,
            BlockState polished,
            BlockState veilstone,
            BlockState frame,
            BlockState lantern
    ) {
        BlockPos hall = center.add(0, 0, -15);
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                world.setBlockState(hall.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(hall.add(dx, 0, dz), veilstone, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int dy = 1; dy <= 8; dy++) {
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos pos = hall.add(dx, dy, dz);
                    if (Math.abs(dx) == 5 && Math.abs(dz) == 3) {
                        world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 5 || Math.abs(dz) == 3) {
                        if (dz == -3 && Math.abs(dx) <= 1 && dy <= 4) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(hall.add(dx, 9, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int step = 0; step <= 6; step++) {
            for (int dx = -3; dx <= 3; dx++) {
                world.setBlockState(center.add(dx, step, -17 + step), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        world.setBlockState(hall.add(-3, 2, 1), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(hall.add(3, 2, 1), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildSideBastion(
            StructureWorldAccess world,
            BlockPos center,
            BlockState polished,
            BlockState veilstone,
            BlockState frame,
            BlockState lantern,
            BlockState barrier
    ) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                world.setBlockState(center.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 7; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 3 && Math.abs(dz) == 5) {
                        world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 3 || Math.abs(dz) == 5) {
                        world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) <= 1 && dz == 0 && dy <= 3) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(center.add(0, 1, -3), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 1, 3), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 5, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildRearTower(
            StructureWorldAccess world,
            BlockPos center,
            BlockState polished,
            BlockState veilstone,
            BlockState frame,
            BlockState lantern
    ) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                world.setBlockState(center.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 15; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 3 && Math.abs(dz) == 3) {
                        world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 3 || Math.abs(dz) == 3) {
                        world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int layer = 0; layer <= 3; layer++) {
            int radius = 4 - layer;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
                        world.setBlockState(center.add(dx, 16 + layer, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(center.add(0, 20, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void carveUndercroft(
            StructureWorldAccess world,
            BlockPos center,
            BlockState polished,
            BlockState veilstone,
            BlockState lantern,
            BlockState barrier
    ) {
        BlockPos crypt = center.down(6);
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                for (int dy = -1; dy <= 5; dy++) {
                    BlockPos pos = crypt.add(dx, dy, dz);
                    if (dy == -1) {
                        world.setBlockState(pos, polished, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 5 || Math.abs(dz) == 5 || dy == 5) {
                        world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int step = 0; step <= 5; step++) {
            BlockPos stair = center.add(0, -step, 5 - step);
            world.setBlockState(stair.down(), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(stair, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(stair.up(), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }
        world.setBlockState(crypt.add(-2, 1, -2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(crypt.add(2, 1, -2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(crypt.add(0, 2, 0), barrier, HushStructureFeatureSupport.GEN_FLAGS);
    }
}

