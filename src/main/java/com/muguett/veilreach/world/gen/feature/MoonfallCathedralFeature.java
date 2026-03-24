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

public class MoonfallCathedralFeature extends Feature<DefaultFeatureConfig> {
    public MoonfallCathedralFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
        if (!hasStableGround(world, center)) {
            return false;
        }

        BlockState polished = Blocks.POLISHED_DEEPSLATE.getDefaultState();
        BlockState veilstone = Blocks.CALCITE.getDefaultState();
        BlockState inlay = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState trim = Blocks.CHISELED_DEEPSLATE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState glass = Blocks.IRON_BARS.getDefaultState();
        BlockState chain = Blocks.END_ROD.getDefaultState();
        BlockState cache = VeilreachBlocks.STARLIGHT_CACHE.getDefaultState();
        BlockState altar = VeilreachBlocks.AWAKENED_ALTAR.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 16, 26, 18);
        HushStructureFeatureSupport.supportFootprint(world, center, -9, 9, -7, 7, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -11, 11, 8, 15, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -4, 4, -14, -8, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, -14, -8, 8, 14, -1, polished);
        HushStructureFeatureSupport.supportFootprint(world, center, 8, 14, 8, 14, -1, polished);

        for (int dx = -9; dx <= 9; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                int ring = Math.max(Math.abs(dx), Math.abs(dz));
                BlockState underfoot = ring >= 8 ? polished : trim;
                world.setBlockState(center.add(dx, -1, dz), underfoot, HushStructureFeatureSupport.GEN_FLAGS);
                if (Math.abs(dx) <= 8 && Math.abs(dz) <= 6) {
                    BlockState floor = (dx == 0 || dz == 0 || Math.abs(dx) == 3 || Math.abs(dz) == 3) ? inlay : veilstone;
                    world.setBlockState(center.add(dx, 0, dz), floor, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int dy = 1; dy <= 9; dy++) {
            for (int dx = -8; dx <= 8; dx++) {
                for (int dz = -6; dz <= 6; dz++) {
                    if (Math.abs(dx) != 8 && Math.abs(dz) != 6) {
                        continue;
                    }
                    BlockPos pos = center.add(dx, dy, dz);
                    if ((Math.abs(dx) == 8 && Math.abs(dz) == 6) || dx == -3 || dx == 3) {
                        world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (dy == 4 || dy == 9) {
                        world.setBlockState(pos, polished, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if ((Math.abs(dx) == 8 && dy <= 5) || (Math.abs(dz) == 6 && dy <= 3)) {
                        world.setBlockState(pos, trim, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dz) == 6 && Math.abs(dx) <= 5 && Math.abs(dx) >= 2 && dy >= 2 && dy <= 6) {
                        world.setBlockState(pos, glass, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                if (Math.abs(dx) <= 6 && Math.abs(dz) <= 4) {
                    BlockState roof = (dx == 0 || dz == 0) ? inlay : polished;
                    world.setBlockState(center.add(dx, 10, dz), roof, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int layer = 0; layer <= 3; layer++) {
            int radiusX = 8 - layer;
            int radiusZ = 6 - layer;
            for (int dx = -radiusX; dx <= radiusX; dx++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    if (Math.abs(dx) == radiusX || Math.abs(dz) == radiusZ) {
                        world.setBlockState(center.add(dx, 11 + layer, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int x = -6; x <= 6; x += 12) {
            for (int z = -4; z <= 4; z += 8) {
                for (int dy = 1; dy <= 13; dy++) {
                    world.setBlockState(center.add(x, dy, z), dy <= 4 ? trim : frame, HushStructureFeatureSupport.GEN_FLAGS);
                }
                world.setBlockState(center.add(x, 14, z), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int side = -1; side <= 1; side += 2) {
            for (int z = -4; z <= 4; z += 4) {
                for (int y = 1; y <= 7; y++) {
                    world.setBlockState(center.add(side * 9, y, z), trim, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }

        for (int x = -3; x <= 3; x += 3) {
            world.setBlockState(center.add(x, 8, 0), chain, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(x, 7, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }

        carveDoorway(world, center.south(6));
        carveCrypt(world, center, polished, veilstone, lantern, altar);
        carveSideChapels(world, center, polished, veilstone, glass, lantern);
        carveReliquary(world, center, polished, veilstone, lantern);
        carveHiddenSacristy(world, center, polished, veilstone, lantern, glass);
        buildFrontApproach(world, center, polished, veilstone, frame, lantern, glass);
        buildRearApse(world, center, polished, veilstone, frame, lantern, glass);
        buildCathedralTower(world, center.add(-11, 0, 11), polished, veilstone, frame, lantern, glass);
        buildCathedralTower(world, center.add(11, 0, 11), polished, veilstone, frame, lantern, glass);

        world.setBlockState(center.add(0, 1, -2), cache, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 1, 2), cache, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-2, 1, 1), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(2, 1, 1), VeilreachEntities.LANTERNKEEPER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(0, -4, 2), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-7, 1, 0), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(7, 1, 0), VeilreachEntities.WARDEN_OF_SILENCE);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-11, 1, 11), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(11, 9, 11), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.placeLootChest(world, center.add(-5, 1, -4), Direction.SOUTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(5, 1, -4), Direction.SOUTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, -4, 0), Direction.NORTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-7, 1, 4), Direction.WEST, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(7, 1, 4), Direction.EAST, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, -11), Direction.SOUTH, "chests/moonfall_cathedral_vault");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, 13), Direction.NORTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-11, 9, 11), Direction.SOUTH, "chests/moonfall_cathedral_vault");
        HushStructureFeatureSupport.placeLootChest(world, center.add(11, 1, 11), Direction.SOUTH, "chests/moonfall_cathedral");
        return true;
    }

    private static void carveDoorway(StructureWorldAccess world, BlockPos doorBase) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 4; dy++) {
                world.setBlockState(doorBase.add(dx, dy, 0), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
    }

    private static void carveCrypt(
            StructureWorldAccess world,
            BlockPos center,
            BlockState polished,
            BlockState veilstone,
            BlockState lantern,
            BlockState altar
    ) {
        BlockPos crypt = center.down(4);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = -1; dy <= 4; dy++) {
                    BlockPos pos = crypt.add(dx, dy, dz);
                    if (dy == -1) {
                        world.setBlockState(pos, polished, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 4 || Math.abs(dz) == 4 || dy == 4) {
                        world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int step = 0; step <= 4; step++) {
            BlockPos stair = center.add(0, -step, -1 - step);
            world.setBlockState(stair.down(), polished, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(stair, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(stair.up(), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }

        world.setBlockState(crypt, altar, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(crypt.north(2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(crypt.south(2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void carveSideChapels(StructureWorldAccess world, BlockPos center, BlockState polished, BlockState veilstone, BlockState glass, BlockState lantern) {
        for (int side = -1; side <= 1; side += 2) {
            BlockPos chapel = center.add(side * 10, 0, 0);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    for (int dy = 0; dy <= 6; dy++) {
                        BlockPos pos = chapel.add(dx, dy, dz);
                        if (Math.abs(dx) == 2 || Math.abs(dz) == 3 || dy == 6) {
                            world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    }
                    world.setBlockState(chapel.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
            world.setBlockState(chapel.add(0, 2, -3), glass, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(chapel.add(0, 3, -3), glass, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(chapel.add(0, 1, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }
    }

    private static void carveReliquary(StructureWorldAccess world, BlockPos center, BlockState polished, BlockState veilstone, BlockState lantern) {
        BlockPos reliquary = center.add(0, 0, -8);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = reliquary.add(dx, dy, dz);
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2 || dy == 4) {
                        world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
                world.setBlockState(reliquary.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        world.setBlockState(reliquary.add(0, 1, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeDoor(world, reliquary.add(0, 0, 2), Direction.SOUTH);
    }

    private static void carveHiddenSacristy(StructureWorldAccess world, BlockPos center, BlockState polished, BlockState veilstone, BlockState lantern, BlockState glass) {
        BlockPos sacristy = center.add(0, 1, -11);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = sacristy.add(dx, dy, dz);
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2 || dy == 4) {
                        world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
                world.setBlockState(sacristy.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        world.setBlockState(sacristy.add(0, 1, -2), glass, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(sacristy.add(0, 2, -2), glass, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(sacristy.add(0, 1, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeDoor(world, sacristy.add(0, 0, 2), Direction.SOUTH);
    }

    private static void buildFrontApproach(
            StructureWorldAccess world,
            BlockPos center,
            BlockState polished,
            BlockState veilstone,
            BlockState frame,
            BlockState lantern,
            BlockState glass
    ) {
        BlockPos nave = center.add(0, 0, 11);
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -3; dz <= 4; dz++) {
                world.setBlockState(nave.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(nave.add(dx, 0, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int dy = 1; dy <= 8; dy++) {
            for (int dx = -6; dx <= 6; dx++) {
                for (int dz = -3; dz <= 4; dz++) {
                    BlockPos pos = nave.add(dx, dy, dz);
                    if (Math.abs(dx) == 6 && Math.abs(dz) == 4) {
                        world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 6 || Math.abs(dz) == 4) {
                        if (dz == 4 && Math.abs(dx) <= 1 && dy <= 4) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                        } else if ((Math.abs(dx) == 4 && dz == -4) || (Math.abs(dx) == 6 && dz == 0 && dy >= 3 && dy <= 6)) {
                            world.setBlockState(pos, glass, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                world.setBlockState(nave.add(dx, 9, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        world.setBlockState(nave.add(-3, 2, 2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(nave.add(3, 2, 2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildRearApse(
            StructureWorldAccess world,
            BlockPos center,
            BlockState polished,
            BlockState veilstone,
            BlockState frame,
            BlockState lantern,
            BlockState glass
    ) {
        BlockPos apse = center.add(0, 0, -11);
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > 4) {
                    continue;
                }
                world.setBlockState(apse.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(apse.add(dx, 0, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }

        for (int dy = 1; dy <= 7; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > 4) {
                        continue;
                    }
                    BlockPos pos = apse.add(dx, dy, dz);
                    if (Math.abs(dx) + Math.abs(dz) == 4) {
                        world.setBlockState(pos, dy >= 4 ? frame : veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if ((dx == 0 && dz == -3 && dy >= 2 && dy <= 5) || (Math.abs(dx) == 3 && dz == 0 && dy >= 3 && dy <= 5)) {
                        world.setBlockState(pos, glass, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(apse.add(0, 2, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildCathedralTower(
            StructureWorldAccess world,
            BlockPos center,
            BlockState polished,
            BlockState veilstone,
            BlockState frame,
            BlockState lantern,
            BlockState glass
    ) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                world.setBlockState(center.add(dx, -1, dz), polished, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 15; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 3 && Math.abs(dz) == 3) {
                        world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 3 || Math.abs(dz) == 3) {
                        if (dy >= 5 && dy <= 9 && ((dx == 0 && Math.abs(dz) == 3) || (dz == 0 && Math.abs(dx) == 3))) {
                            world.setBlockState(pos, glass, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, veilstone, HushStructureFeatureSupport.GEN_FLAGS);
                        }
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

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        return HushStructureFeatureSupport.hasStableGround(world, center, 12, 14, 4, 24, 16);
    }
}

