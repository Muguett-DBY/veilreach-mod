package com.muguett.veilreach.world.gen.feature;

import com.mojang.serialization.Codec;
import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

        BlockState base = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState wall = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState altar = VeilreachBlocks.SEALED_ALTAR.getDefaultState();
        BlockState dark = Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 26, 28, 28);
        HushStructureFeatureSupport.supportFootprint(world, center, -10, 10, -26, 14, -1, base);
        HushStructureFeatureSupport.supportFootprint(world, center, -17, -9, -3, 15, -1, base);
        HushStructureFeatureSupport.supportFootprint(world, center, 9, 17, -3, 15, -1, base);
        HushStructureFeatureSupport.supportFootprint(world, center, -16, 16, 15, 24, -1, base);

        buildApproach(world, center, base, dark, frame, lantern);
        buildGateHall(world, center, base, wall, dark, frame, lantern);
        buildThroneDais(world, center, base, wall, dark, frame, lantern, barrier, altar);
        buildSideFort(world, center.add(-13, 0, 4), base, wall, dark, frame, lantern, barrier, true);
        buildSideFort(world, center.add(13, 0, 4), base, wall, dark, frame, lantern, barrier, false);
        buildRearKeep(world, center, base, wall, dark, frame, lantern);
        buildUndercroft(world, center, base, wall, dark, lantern, barrier);

        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, -18), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-13, 1, 8), Direction.EAST, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(13, 1, 8), Direction.WEST, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-6, 7, 1), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(6, 7, 1), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 7, -4), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-12, 11, 18), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(12, 11, 18), Direction.SOUTH, "chests/silent_throne");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, -5, 6), Direction.NORTH, "chests/silent_throne");

        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-13, 1, 8), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(13, 1, 8), VeilreachEntities.WARDEN_OF_SILENCE);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-7, 1, -7), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(7, 1, -7), VeilreachEntities.LANTERNKEEPER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(0, -5, 6), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-12, 1, 18), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(12, 11, 18), VeilreachEntities.LANTERNKEEPER);
        return true;
    }

    private static void buildApproach(StructureWorldAccess world, BlockPos center, BlockState base, BlockState dark, BlockState frame, BlockState lantern) {
        for (int dz = -26; dz <= -11; dz++) {
            int halfWidth = dz <= -18 ? 7 : 5;
            for (int dx = -halfWidth; dx <= halfWidth; dx++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, dz), dx == 0 || Math.abs(dx) == 3 ? dark : base, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int step = 0; step <= 8; step++) {
            for (int dx = -4; dx <= 4; dx++) {
                world.setBlockState(center.add(dx, Math.max(-1, 3 - (step / 2)), -27 - step), base, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int side = -1; side <= 1; side += 2) {
            for (int y = 1; y <= 8; y++) {
                world.setBlockState(center.add(side * 7, y, -22), frame, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(side * 5, y, -13), frame, HushStructureFeatureSupport.GEN_FLAGS);
            }
            world.setBlockState(center.add(side * 7, 9, -22), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }
    }

    private static void buildGateHall(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState dark, BlockState frame, BlockState lantern) {
        for (int dz = -12; dz <= 6; dz++) {
            for (int dx = -8; dx <= 8; dx++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, dz), dx == 0 || dz == 0 ? dark : wall, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int dy = 1; dy <= 11; dy++) {
            for (int dx = -8; dx <= 8; dx++) {
                for (int dz = -12; dz <= 6; dz++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 8 || dz == -12 || dz == 6) {
                        if (dz == -12 && Math.abs(dx) <= 2 && dy <= 5) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, dy >= 8 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else if (Math.abs(dx) == 3 && dz >= -8 && dz <= 3 && dy <= 7) {
                        world.setBlockState(pos, wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -11; dz <= 5; dz++) {
                world.setBlockState(center.add(dx, 12, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int side = -1; side <= 1; side += 2) {
            world.setBlockState(center.add(side * 6, 3, -6), lantern, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(side * 6, 3, 2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        }
    }

    private static void buildThroneDais(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState dark, BlockState frame, BlockState lantern, BlockState barrier, BlockState altar) {
        for (int dz = -3; dz <= 8; dz++) {
            for (int dx = -7; dx <= 7; dx++) {
                world.setBlockState(center.add(dx, 0, dz), Math.abs(dx) <= 2 && dz <= 1 ? dark : wall, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int step = 0; step <= 5; step++) {
            for (int dx = -4; dx <= 4; dx++) {
                world.setBlockState(center.add(dx, 1 + step, -2 + step), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 2 + step, -2 + step), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -5; dz <= 1; dz++) {
                world.setBlockState(center.add(dx, 7, dz), Math.abs(dx) == 6 || dz == -5 || dz == 1 ? base : dark, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int side = -1; side <= 1; side += 2) {
            for (int y = 1; y <= 11; y++) {
                world.setBlockState(center.add(side * 7, y, -4), frame, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(side * 7, y, 8), frame, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        world.setBlockState(center.add(0, 8, -3), altar, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(-2, 8, -4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(2, 8, -4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        for (int y = 8; y <= 10; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                world.setBlockState(center.add(dx, y, -1), barrier, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
    }

    private static void buildSideFort(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState dark, BlockState frame, BlockState lantern, BlockState barrier, boolean west) {
        int side = west ? -1 : 1;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, dz), Math.abs(dz) <= 1 ? dark : wall, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 1; dy <= 8; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 4 || Math.abs(dz) == 7) {
                        world.setBlockState(pos, dy >= 6 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        for (int step = 0; step <= 5; step++) {
            BlockPos tread = center.add(side * (3 - step / 2), step, -5 + step);
            world.setBlockState(tread, base, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(tread.up(), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
        }
        HushStructureFeatureSupport.placeDoor(world, center.add(-side * 4, 0, 0), west ? Direction.EAST : Direction.WEST);
        world.setBlockState(center.add(0, 1, 4), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 5, -4), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildRearKeep(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState dark, BlockState frame, BlockState lantern) {
        for (int dz = 15; dz <= 24; dz++) {
            for (int dx = -16; dx <= 16; dx++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, dz), Math.abs(dx) >= 13 ? wall : dark, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int side = -1; side <= 1; side += 2) {
            buildKeepTower(world, center.add(side * 12, 0, 18), base, wall, frame, lantern);
        }
    }

    private static void buildKeepTower(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState frame, BlockState lantern) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 18; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 4 || Math.abs(dz) == 4) {
                        world.setBlockState(pos, dy >= 14 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        for (int step = 0; step <= 12; step++) {
            int x = 3 - Math.floorMod(step, 4);
            int z = switch (Math.floorDiv(step, 3)) {
                case 0 -> 2;
                case 1 -> 0;
                case 2 -> -2;
                default -> -3;
            };
            world.setBlockState(center.add(x, step, z), base, HushStructureFeatureSupport.GEN_FLAGS);
        }
        world.setBlockState(center.add(0, 19, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildUndercroft(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState dark, BlockState lantern, BlockState barrier) {
        BlockPos crypt = center.down(6).add(0, 0, 6);
        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                world.setBlockState(crypt.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 5; dy++) {
                    BlockPos pos = crypt.add(dx, dy, dz);
                    if (Math.abs(dx) == 7 || Math.abs(dz) == 6 || dy == 5) {
                        world.setBlockState(pos, wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        for (int step = 0; step <= 6; step++) {
            BlockPos tread = center.add(0, -1 - step, 7 - step);
            for (int width = -1; width <= 1; width++) {
                world.setBlockState(tread.add(width, 0, 0), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(tread.add(width, 1, 0), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        world.setBlockState(crypt.add(-3, 1, -3), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(crypt.add(3, 1, -3), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(crypt.add(0, 2, 0), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(crypt.add(0, 0, 0), dark, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        return HushStructureFeatureSupport.hasStableGround(world, center, 18, 20, 4, 30, 18);
    }
}
