package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ForgottenManorFeature extends Feature<DefaultFeatureConfig> {
    public ForgottenManorFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        return generateAt(world, center, true);
    }

    public static boolean generateAt(StructureWorldAccess world, BlockPos center, boolean requireStableGround) {
        if (requireStableGround && !hasStableGround(world, center)) {
            return false;
        }

        BlockState planks = VeilreachBlocks.GLOAMWOOD_PLANKS.getDefaultState();
        BlockState log = VeilreachBlocks.GLOAMWOOD_LOG.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState glass = VeilreachBlocks.RIFTGLASS.getDefaultState();
        BlockState leaves = VeilreachBlocks.SHIMMERLEAF_LEAVES.getDefaultState();
        BlockState cache = VeilreachBlocks.STARLIGHT_CACHE.getDefaultState();
        BlockState ladderNorth = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.NORTH);

        HushStructureFeatureSupport.clearBox(world, center, 14, 17, 11);
        HushStructureFeatureSupport.supportFootprint(world, center, -8, 8, -6, 6, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -13, -9, -8, -4, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, 9, 13, -8, -4, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -13, -9, 4, 8, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, 9, 13, 4, 8, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -4, 4, 7, 11, -1, polished);

        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                world.setBlockState(center.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                if (Math.abs(dx) <= 7 && Math.abs(dz) <= 5) {
                    world.setBlockState(center.add(dx, 0, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
                    world.setBlockState(center.add(dx, 5, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        buildWalls(world, center, 0, 4, log, planks, frame, glass);
        buildWalls(world, center, 6, 9, log, planks, frame, glass);
        buildInteriorRooms(world, center, planks, polished, lantern);
        buildManorBasement(world, center, polished, planks, lantern);
        buildUpperGallery(world, center, planks, polished, lantern, frame);
        buildSouthWing(world, center, planks, log, frame, polished, glass, lantern);
        buildCornerTower(world, center.add(-11, 0, -6), planks, log, frame, lantern, glass);
        buildCornerTower(world, center.add(11, 0, -6), planks, log, frame, lantern, glass);
        buildCornerTower(world, center.add(-11, 0, 6), planks, log, frame, lantern, glass);
        buildCornerTower(world, center.add(11, 0, 6), planks, log, frame, lantern, glass);

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                if (Math.abs(dx) == 7 || Math.abs(dz) == 5) {
                    continue;
                }
                world.setBlockState(center.add(dx, 10, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int layer = 0; layer <= 2; layer++) {
            int radiusX = 8 - layer;
            int radiusZ = 6 - layer;
            for (int dx = -radiusX; dx <= radiusX; dx++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    if (Math.abs(dx) == radiusX || Math.abs(dz) == radiusZ) {
                        world.setBlockState(center.add(dx, 10 + layer, dz), leaves, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int dy = 1; dy <= 9; dy++) {
            world.setBlockState(center.add(-5, dy, 4), ladderNorth, HushStructureFeatureSupport.GEN_FLAGS);
        }

        carveDoor(world, center.south(5));
        placeWindow(world, center.add(-4, 2, -5), glass);
        placeWindow(world, center.add(4, 2, -5), glass);
        placeWindow(world, center.add(-7, 2, -2), glass);
        placeWindow(world, center.add(-7, 2, 2), glass);
        placeWindow(world, center.add(7, 2, -2), glass);
        placeWindow(world, center.add(7, 2, 2), glass);
        placeWindow(world, center.add(-4, 7, -5), glass);
        placeWindow(world, center.add(4, 7, -5), glass);
        placeWindow(world, center.add(-7, 7, -2), glass);
        placeWindow(world, center.add(-7, 7, 2), glass);
        placeWindow(world, center.add(7, 7, -2), glass);
        placeWindow(world, center.add(7, 7, 2), glass);

        world.setBlockState(center.add(0, 1, 0), cache, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 6, 0), cache, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, -3, 0), cache, HushStructureFeatureSupport.GEN_FLAGS);

        for (int dz = 2; dz <= 4; dz++) {
            world.setBlockState(center.add(-2, 1, dz), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(2, 1, dz), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(-2, 6, dz), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(2, 6, dz), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        HushStructureFeatureSupport.placeLootChest(world, center.add(-5, 1, -3), Direction.EAST, "chests/forgotten_manor");
        HushStructureFeatureSupport.placeLootChest(world, center.add(5, 1, -3), Direction.WEST, "chests/forgotten_manor");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 6, -4), Direction.SOUTH, "chests/forgotten_manor");
        HushStructureFeatureSupport.placeLootChest(world, center.add(5, 6, 3), Direction.WEST, "chests/forgotten_manor");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, -3, -4), Direction.SOUTH, "chests/forgotten_manor");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, 9), Direction.NORTH, "chests/forgotten_manor");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-11, 1, -6), Direction.SOUTH, "chests/forgotten_manor");
        HushStructureFeatureSupport.placeLootChest(world, center.add(11, 7, -6), Direction.SOUTH, "chests/forgotten_manor");
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-4, -3, 0), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(4, 6, 0), VeilreachEntities.LANTERNKEEPER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(0, 1, 8), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-11, 1, 6), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(11, 1, 6), VeilreachEntities.WARDEN_OF_SILENCE);
        return true;
    }

    private static void buildWalls(
            StructureWorldAccess world,
            BlockPos center,
            int minY,
            int maxY,
            BlockState log,
            BlockState planks,
            BlockState frame,
            BlockState glass
    ) {
        for (int dy = minY; dy <= maxY; dy++) {
            for (int dx = -7; dx <= 7; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    if (Math.abs(dx) != 7 && Math.abs(dz) != 5) {
                        continue;
                    }
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 7 && Math.abs(dz) == 5) {
                        world.setBlockState(pos, log, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (dy == minY || dy == maxY) {
                        world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if ((Math.abs(dx) == 7 && dz == 0) || (Math.abs(dz) == 5 && Math.abs(dx) == 3)) {
                        world.setBlockState(pos, glass, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, planks, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
    }

    private static void carveDoor(StructureWorldAccess world, BlockPos doorwayBase) {
        for (int dy = 1; dy <= 3; dy++) {
            world.setBlockState(doorwayBase.up(dy - 1), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(doorwayBase.west().up(dy - 1), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(doorwayBase.east().up(dy - 1), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }
    }

    private static void placeWindow(StructureWorldAccess world, BlockPos start, BlockState glass) {
        world.setBlockState(start, glass, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(start.up(), glass, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildInteriorRooms(StructureWorldAccess world, BlockPos center, BlockState planks, BlockState polished, BlockState lantern) {
        for (int dy = 1; dy <= 9; dy++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (dz == 0 || dz == 1) {
                    continue;
                }
                world.setBlockState(center.add(0, dy, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int dx = -6; dx <= 6; dx++) {
            world.setBlockState(center.add(dx, 1, -1), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(dx, 6, 1), polished, HushStructureFeatureSupport.GEN_FLAGS);
        }

        for (int dy = 1; dy <= 3; dy++) {
            world.setBlockState(center.add(0, dy, -3), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(0, dy, 3), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(-3, dy, 0), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(3, dy, 0), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(-3, dy + 5, 0), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(3, dy + 5, 0), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }

        world.setBlockState(center.add(-5, 1, 4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(5, 1, 4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(-5, 6, -4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(5, 6, -4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(5, 6, 4), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(5, 7, 4), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildManorBasement(StructureWorldAccess world, BlockPos center, BlockState polished, BlockState planks, BlockState lantern) {
        BlockPos base = center.down(4);
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = base.add(dx, dy, dz);
                    if (Math.abs(dx) == 6 || Math.abs(dz) == 4 || dy == 4) {
                        world.setBlockState(pos, polished, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
                world.setBlockState(base.add(dx, -1, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int step = 0; step <= 4; step++) {
            BlockPos stair = center.add(-5 + step, -step, 4);
            world.setBlockState(stair, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(stair.up(), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(stair.down(), polished, HushStructureFeatureSupport.GEN_FLAGS);
        }
        world.setBlockState(base.add(-4, 1, -2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(base.add(4, 1, -2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeDoor(world, base.add(0, 0, -3), Direction.SOUTH);
    }

    private static void buildUpperGallery(StructureWorldAccess world, BlockPos center, BlockState planks, BlockState polished, BlockState lantern, BlockState frame) {
        for (int dx = -5; dx <= 5; dx++) {
            world.setBlockState(center.add(dx, 7, -2), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(dx, 7, 2), polished, HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int dz = -2; dz <= 2; dz++) {
            world.setBlockState(center.add(-5, 7, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(5, 7, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int dx = -4; dx <= 4; dx += 8) {
            for (int dz = -1; dz <= 1; dz += 2) {
                for (int dy = 7; dy <= 9; dy++) {
                    world.setBlockState(center.add(dx, dy, dz), frame, HushStructureFeatureSupport.GEN_FLAGS);
                }
                world.setBlockState(center.add(dx, 9, dz), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
    }

    private static void buildSouthWing(
            StructureWorldAccess world,
            BlockPos center,
            BlockState planks,
            BlockState log,
            BlockState frame,
            BlockState polished,
            BlockState glass,
            BlockState lantern
    ) {
        BlockPos wing = center.add(0, 0, 9);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(wing.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(wing.add(dx, 0, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(wing.add(dx, 5, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int dy = 1; dy <= 4; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos pos = wing.add(dx, dy, dz);
                    if (Math.abs(dx) == 4 && Math.abs(dz) == 2) {
                        world.setBlockState(pos, log, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 4 || Math.abs(dz) == 2) {
                        if (dz == 2 && Math.abs(dx) <= 1 && dy <= 3) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                        } else if (Math.abs(dx) == 2 && dz == -2 && dy >= 2) {
                            world.setBlockState(pos, glass, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(wing.add(-3, 1, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(wing.add(3, 1, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildCornerTower(
            StructureWorldAccess world,
            BlockPos center,
            BlockState planks,
            BlockState log,
            BlockState frame,
            BlockState lantern,
            BlockState glass
    ) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, -1, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 11; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                        world.setBlockState(pos, log, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                        if (dy >= 4 && dy <= 6 && ((dx == 0 && Math.abs(dz) == 2) || (dz == 0 && Math.abs(dx) == 2))) {
                            world.setBlockState(pos, glass, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int layer = 0; layer <= 2; layer++) {
            int radius = 3 - layer;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
                        world.setBlockState(center.add(dx, 12 + layer, dz), planks, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(center.add(0, 15, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        return HushStructureFeatureSupport.hasStableGround(world, center, 8, 6, 4, 10, 12);
    }
}

