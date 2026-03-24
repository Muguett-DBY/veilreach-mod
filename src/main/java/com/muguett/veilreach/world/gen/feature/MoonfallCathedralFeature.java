package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

        BlockState base = Blocks.POLISHED_DEEPSLATE.getDefaultState();
        BlockState wall = Blocks.CALCITE.getDefaultState();
        BlockState trim = Blocks.CHISELED_DEEPSLATE.getDefaultState();
        BlockState inlay = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState bars = Blocks.IRON_BARS.getDefaultState();
        BlockState stair = Blocks.POLISHED_DEEPSLATE_STAIRS.getDefaultState();
        BlockState slab = Blocks.POLISHED_DEEPSLATE_SLAB.getDefaultState();
        BlockState pillar = Blocks.POLISHED_ANDESITE.getDefaultState();
        BlockState mosaic = Blocks.TUFF_BRICKS.getDefaultState();
        BlockState cache = VeilreachBlocks.STARLIGHT_CACHE.getDefaultState();
        BlockState altar = VeilreachBlocks.AWAKENED_ALTAR.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 24, 30, 30);
        HushStructureFeatureSupport.supportFootprint(world, center, -10, 10, -25, 23, -1, base);
        HushStructureFeatureSupport.supportFootprint(world, center, -18, 18, -7, 8, -1, base);
        HushStructureFeatureSupport.supportFootprint(world, center, -20, -12, 12, 22, -1, base);
        HushStructureFeatureSupport.supportFootprint(world, center, 12, 20, 12, 22, -1, base);

        buildFrontCourt(world, center, base, trim, frame, lantern, stair, slab);
        buildMainNave(world, center, base, wall, trim, inlay, frame, lantern, bars, stair, slab, pillar, mosaic);
        buildTransepts(world, center, base, wall, trim, frame, lantern, bars, pillar);
        buildUpperChoir(world, center, base, inlay, wall, lantern, stair);
        buildRearApse(world, center, base, wall, trim, frame, lantern, bars, altar);
        buildTower(world, center.add(-16, 0, 17), base, wall, frame, lantern, bars, false);
        buildTower(world, center.add(16, 0, 17), base, wall, frame, lantern, bars, true);
        buildCrypt(world, center, base, wall, trim, lantern, cache);
        buildReliquaryWing(world, center, base, wall, trim, frame, lantern, true);
        buildReliquaryWing(world, center, base, wall, trim, frame, lantern, false);
        carveCathedralFlow(world, center, trim, stair, slab);
        buildRoseFacade(world, center, frame, bars, lantern, pillar);

        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, 18), Direction.SOUTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-6, 1, 10), Direction.SOUTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(6, 1, 10), Direction.SOUTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-13, 1, 0), Direction.EAST, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(13, 1, 0), Direction.WEST, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, -16), Direction.SOUTH, "chests/moonfall_cathedral_vault");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-16, 10, 17), Direction.SOUTH, "chests/moonfall_cathedral_vault");
        HushStructureFeatureSupport.placeLootChest(world, center.add(16, 10, 17), Direction.SOUTH, "chests/moonfall_cathedral_vault");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, -5, -2), Direction.NORTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-15, 1, -2), Direction.EAST, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(15, 1, -2), Direction.WEST, "chests/moonfall_cathedral");

        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-4, 1, 11), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(4, 1, 11), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-13, 1, 0), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(13, 1, 0), VeilreachEntities.WARDEN_OF_SILENCE);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(0, -5, -2), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-16, 1, 17), VeilreachEntities.LANTERNKEEPER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(16, 10, 17), VeilreachEntities.VEILBOUND_WATCHER);
        return true;
    }

    private static void buildFrontCourt(StructureWorldAccess world, BlockPos center, BlockState base, BlockState trim, BlockState frame, BlockState lantern, BlockState stair, BlockState slab) {
        for (int dz = 13; dz <= 23; dz++) {
            int halfWidth = dz >= 19 ? 8 : 6;
            for (int dx = -halfWidth; dx <= halfWidth; dx++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, dz), dx == 0 || Math.abs(dx) == 3 || dz == 18 ? trim : base, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int side = -1; side <= 1; side += 2) {
            for (int y = 1; y <= 8; y++) {
                world.setBlockState(center.add(side * 8, y, 22), frame, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(side * 6, y, 14), frame, HushStructureFeatureSupport.GEN_FLAGS);
            }
            world.setBlockState(center.add(side * 8, 9, 22), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int step = 0; step <= 7; step++) {
            for (int dx = -4; dx <= 4; dx++) {
                world.setBlockState(center.add(dx, Math.max(-1, 3 - (step / 2)), 24 + step), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, Math.max(0, 4 - (step / 2)), 24 + step), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int dx = -3; dx <= 3; dx++) {
            world.setBlockState(center.add(dx, 1, 20), stair.with(StairsBlock.FACING, Direction.NORTH), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(dx, 1, 19), slab, HushStructureFeatureSupport.GEN_FLAGS);
        }
    }

    private static void buildMainNave(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState inlay, BlockState frame, BlockState lantern, BlockState bars, BlockState stair, BlockState slab, BlockState pillar, BlockState mosaic) {
        for (int dz = -10; dz <= 16; dz++) {
            for (int dx = -9; dx <= 9; dx++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                BlockState floor = dx == 0 || Math.abs(dx) == 4 || dz == 7 || dz == -6 ? inlay : wall;
                if (Math.abs(dx) >= 6 && dz >= -7 && dz <= 12) {
                    floor = mosaic;
                }
                world.setBlockState(center.add(dx, 0, dz), floor, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int dy = 1; dy <= 12; dy++) {
            for (int dx = -9; dx <= 9; dx++) {
                for (int dz = -10; dz <= 16; dz++) {
                    boolean outer = Math.abs(dx) == 9 || dz == -10 || dz == 16;
                    boolean aisle = Math.abs(dx) == 4 || Math.abs(dx) == 5;
                    BlockPos pos = center.add(dx, dy, dz);
                    if (outer) {
                        if (dz == 16 && Math.abs(dx) <= 2 && dy <= 5) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                        } else if ((Math.abs(dx) == 9 && dy >= 3 && dy <= 8 && Math.floorMod(dz + 10, 6) <= 1) || (dz == -10 && dy >= 3 && dy <= 7 && Math.abs(dx) <= 2)) {
                            world.setBlockState(pos, bars, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, dy >= 9 && Math.abs(dx) >= 7 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else if (aisle && dy <= 8 && dz >= -8 && dz <= 13) {
                        world.setBlockState(pos, dy <= 5 && dz != 0 && dz != 7 && dz != -6 ? wall : Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    } else if (Math.abs(dx) == 2 && dz >= -7 && dz <= 12 && dy <= 6) {
                        world.setBlockState(pos, dy == 1 || dy == 6 ? trim : bars, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -9; dz <= 15; dz++) {
                world.setBlockState(center.add(dx, 13, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int side = -1; side <= 1; side += 2) {
            for (int z = -6; z <= 12; z += 6) {
                world.setBlockState(center.add(side * 7, 3, z), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            }
            for (int y = 1; y <= 9; y++) {
                world.setBlockState(center.add(side * 6, y, -7), pillar, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(side * 6, y, -1), pillar, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(side * 6, y, 5), pillar, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(side * 6, y, 11), pillar, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int dz = 8; dz <= 14; dz++) {
            world.setBlockState(center.add(-1, 0, dz), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(0, 0, dz), trim, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(1, 0, dz), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int side = -1; side <= 1; side += 2) {
            for (int dz = -8; dz <= 13; dz += 7) {
                HushStructureFeatureSupport.placeDoor(world, center.add(side * 4, 0, dz), side < 0 ? Direction.WEST : Direction.EAST);
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            world.setBlockState(center.add(dx, 1, 15), stair.with(StairsBlock.FACING, Direction.SOUTH), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(dx, 2, 14), stair.with(StairsBlock.FACING, Direction.SOUTH), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(dx, 3, 13), slab, HushStructureFeatureSupport.GEN_FLAGS);
        }
    }

    private static void buildTransepts(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState frame, BlockState lantern, BlockState bars, BlockState pillar) {
        for (int side = -1; side <= 1; side += 2) {
            BlockPos wing = center.add(side * 13, 0, 0);
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -6; dz <= 6; dz++) {
                    world.setBlockState(wing.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                    world.setBlockState(wing.add(dx, 0, dz), Math.abs(dz) == 5 ? trim : wall, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
            for (int dy = 1; dy <= 9; dy++) {
                for (int dx = -4; dx <= 4; dx++) {
                    for (int dz = -6; dz <= 6; dz++) {
                        BlockPos pos = wing.add(dx, dy, dz);
                        if (Math.abs(dx) == 4 || Math.abs(dz) == 6) {
                            if (Math.abs(dz) <= 1 && dx == -side * 4 && dy <= 4) {
                                world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                            } else if ((Math.abs(dz) == 6 && dy >= 3 && dy <= 6 && Math.abs(dx) <= 1) || (Math.abs(dx) == 4 && dy >= 3 && dy <= 6 && Math.abs(dz) <= 1)) {
                                world.setBlockState(pos, bars, HushStructureFeatureSupport.GEN_FLAGS);
                            } else {
                                world.setBlockState(pos, dy >= 7 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                            }
                        } else {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    }
                }
            }
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    world.setBlockState(wing.add(dx, 10, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
            world.setBlockState(wing.add(0, 2, 4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(wing.add(0, 2, -4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            for (int y = 1; y <= 8; y++) {
                world.setBlockState(wing.add(-2, y, -3), pillar, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(wing.add(2, y, -3), pillar, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(wing.add(-2, y, 3), pillar, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(wing.add(2, y, 3), pillar, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
    }

    private static void buildUpperChoir(StructureWorldAccess world, BlockPos center, BlockState base, BlockState inlay, BlockState wall, BlockState lantern, BlockState stair) {
        for (int side = -1; side <= 1; side += 2) {
            for (int step = 0; step <= 6; step++) {
                BlockPos tread = center.add(side * (8 - step), step, 11 - step);
                for (int width = 0; width <= 1; width++) {
                    world.setBlockState(tread.add(0, 0, width), base, HushStructureFeatureSupport.GEN_FLAGS);
                    world.setBlockState(tread.add(0, 1, width), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
            for (int dx = 0; dx <= 4; dx++) {
                for (int dz = -1; dz <= 4; dz++) {
                    BlockPos platform = center.add(side * (5 + dx), 7, dz);
                    world.setBlockState(platform, dx == 4 || dz == -1 || dz == 4 ? base : inlay, HushStructureFeatureSupport.GEN_FLAGS);
                    world.setBlockState(platform.up(), dx == 4 ? wall : Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
            world.setBlockState(center.add(side * 7, 8, 1), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            for (int rise = 0; rise <= 3; rise++) {
                world.setBlockState(center.add(side * 6, 4 + rise, -1 - rise), stair.with(StairsBlock.FACING, side < 0 ? Direction.WEST : Direction.EAST), HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
    }

    private static void carveCathedralFlow(StructureWorldAccess world, BlockPos center, BlockState trim, BlockState stair, BlockState slab) {
        for (int dz = -8; dz <= 12; dz++) {
            world.setBlockState(center.add(-1, 1, dz), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(0, 1, dz), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(1, 1, dz), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int side = -1; side <= 1; side += 2) {
            for (int dz = -5; dz <= 9; dz += 7) {
                for (int dx = 5; dx <= 8; dx++) {
                    world.setBlockState(center.add(side * dx, 0, dz), dx == 5 ? trim : slab, HushStructureFeatureSupport.GEN_FLAGS);
                    world.setBlockState(center.add(side * dx, 1, dz), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
            for (int step = 0; step <= 4; step++) {
                BlockPos tread = center.add(side * 8, 1 + step, 5 - step);
                world.setBlockState(tread, stair.with(StairsBlock.FACING, side < 0 ? Direction.WEST : Direction.EAST), HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(tread.up(), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
    }

    private static void buildRoseFacade(StructureWorldAccess world, BlockPos center, BlockState frame, BlockState bars, BlockState lantern, BlockState pillar) {
        BlockPos facade = center.add(0, 7, 16);
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                if (dx * dx + dy * dy > 26) {
                    continue;
                }
                BlockPos pos = facade.add(dx, dy, 0);
                if (Math.abs(dx) <= 1 || Math.abs(dy) <= 1 || dx * dx + dy * dy >= 18) {
                    world.setBlockState(pos, frame, HushStructureFeatureSupport.GEN_FLAGS);
                } else {
                    world.setBlockState(pos, bars, HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }
        for (int y = 1; y <= 16; y++) {
            world.setBlockState(center.add(-10, y, 16), pillar, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(10, y, 16), pillar, HushStructureFeatureSupport.GEN_FLAGS);
        }
        world.setBlockState(center.add(0, 13, 16), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildRearApse(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState frame, BlockState lantern, BlockState bars, BlockState altar) {
        BlockPos apse = center.add(0, 0, -16);
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > 7) {
                    continue;
                }
                world.setBlockState(apse.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(apse.add(dx, 0, dz), Math.abs(dx) <= 1 ? trim : wall, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int dy = 1; dy <= 9; dy++) {
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > 7) {
                        continue;
                    }
                    BlockPos pos = apse.add(dx, dy, dz);
                    if (Math.abs(dx) + Math.abs(dz) >= 6) {
                        if (dz == -4 && Math.abs(dx) <= 1 && dy >= 3 && dy <= 6) {
                            world.setBlockState(pos, bars, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, dy >= 7 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(apse.add(0, 1, 0), altar, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(apse.add(-2, 2, 1), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(apse.add(2, 2, 1), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildTower(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState frame, BlockState lantern, BlockState bars, boolean clockwise) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 17; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 4 || Math.abs(dz) == 4) {
                        if ((Math.abs(dx) == 4 && dy >= 4 && dy <= 12 && dz == 0) || (Math.abs(dz) == 4 && dy >= 7 && dy <= 11 && dx == 0)) {
                            world.setBlockState(pos, bars, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, dy >= 13 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        for (int step = 0; step <= 11; step++) {
            int x = clockwise ? 3 - Math.floorMod(step, 4) : -3 + Math.floorMod(step, 4);
            int z = switch (Math.floorDiv(step, 3)) {
                case 0 -> 2;
                case 1 -> 0;
                case 2 -> -2;
                default -> -3;
            };
            BlockPos tread = center.add(x, step, z);
            world.setBlockState(tread, base, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(tread.up(), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int layer = 0; layer <= 3; layer++) {
            int radius = 5 - layer;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
                        world.setBlockState(center.add(dx, 18 + layer, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(center.add(0, 22, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildCrypt(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState lantern, BlockState cache) {
        BlockPos crypt = center.down(6);
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -8; dz <= 3; dz++) {
                world.setBlockState(crypt.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 5; dy++) {
                    BlockPos pos = crypt.add(dx, dy, dz);
                    if (Math.abs(dx) == 6 || dz == -8 || dz == 3 || dy == 5) {
                        world.setBlockState(pos, wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        for (int step = 0; step <= 6; step++) {
            BlockPos tread = center.add(0, -1 - step, -2 + step);
            for (int width = -1; width <= 1; width++) {
                world.setBlockState(tread.add(width, 0, 0), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(tread.add(width, 1, 0), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int side = -1; side <= 1; side += 2) {
            for (int dz = -6; dz <= 1; dz++) {
                world.setBlockState(crypt.add(side * 3, 0, dz), trim, HushStructureFeatureSupport.GEN_FLAGS);
            }
            world.setBlockState(crypt.add(side * 4, 1, -6), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(crypt.add(side * 4, 1, 0), cache, HushStructureFeatureSupport.GEN_FLAGS);
        }
    }

    private static void buildReliquaryWing(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState frame, BlockState lantern, boolean west) {
        int side = west ? -1 : 1;
        BlockPos wing = center.add(side * 16, 0, -2);
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                world.setBlockState(wing.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 6; dy++) {
                    BlockPos pos = wing.add(dx, dy, dz);
                    if (Math.abs(dx) == 3 || Math.abs(dz) == 4 || dy == 6) {
                        world.setBlockState(pos, dy >= 5 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        HushStructureFeatureSupport.placeDoor(world, wing.add(-side * 3, 0, 0), west ? Direction.EAST : Direction.WEST);
        world.setBlockState(wing.add(0, 1, -2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(wing.add(0, 0, 2), trim, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        return HushStructureFeatureSupport.hasStableGround(world, center, 20, 24, 4, 36, 18);
    }
}
