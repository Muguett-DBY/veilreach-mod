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

public class SunkenSanctumFeature extends Feature<DefaultFeatureConfig> {
    public SunkenSanctumFeature(Codec<DefaultFeatureConfig> codec) {
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
        BlockState altar = VeilreachBlocks.AWAKENED_ALTAR.getDefaultState();
        BlockState barrier = VeilreachBlocks.FOGBOUND_BARRIER.getDefaultState();
        BlockState glass = VeilreachBlocks.RIFTGLASS.getDefaultState();
        BlockState trim = Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 16, 20, 18);
        HushStructureFeatureSupport.supportFootprint(world, center, -8, 8, -16, 10, -1, base);

        buildEntranceCourt(world, center, base, wall, frame, lantern);
        buildDescendingHall(world, center, base, trim);

        BlockPos chamber = center.down(10).add(0, 0, 3);
        buildLowerSanctum(world, chamber, base, wall, trim, frame, lantern, altar, barrier, glass);
        buildCellWing(world, chamber.add(-9, 0, -1), base, wall, trim, lantern, true);
        buildCellWing(world, chamber.add(9, 0, -1), base, wall, trim, lantern, false);
        buildRitualWing(world, chamber, base, wall, trim, frame, lantern, barrier);
        buildVaultBranch(world, chamber.add(10, 0, 10), base, wall, trim, lantern, glass);

        HushStructureFeatureSupport.placeLootChest(world, chamber.add(-4, 0, -3), Direction.SOUTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamber.add(4, 0, -3), Direction.SOUTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamber.add(0, 0, 5), Direction.NORTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamber.add(-9, 0, -1), Direction.EAST, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamber.add(9, 0, -1), Direction.WEST, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamber.add(0, 0, -9), Direction.SOUTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamber.add(10, 0, 10), Direction.WEST, "chests/sunken_sanctum_vault");
        HushStructureFeatureSupport.placeLootChest(world, chamber.add(12, 0, 10), Direction.WEST, "chests/sunken_sanctum_vault");

        HushStructureFeatureSupport.spawnPersistentMob(world, chamber.add(0, 0, -2), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamber.add(-9, 0, -1), VeilreachEntities.WARDEN_OF_SILENCE);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamber.add(9, 0, -1), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamber.add(3, 0, 7), VeilreachEntities.LANTERNKEEPER);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamber.add(10, 0, 10), VeilreachEntities.MOONFALLEN_KNIGHT);
        return true;
    }

    private static void buildEntranceCourt(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState frame, BlockState lantern) {
        for (int dz = -16; dz <= -3; dz++) {
            int halfWidth = dz <= -10 ? 6 : 4;
            for (int dx = -halfWidth; dx <= halfWidth; dx++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, dz), wall, HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        for (int dy = 0; dy <= 5; dy++) {
            world.setBlockState(center.add(-4, dy, -5), frame, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(4, dy, -5), frame, HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int dx = -4; dx <= 4; dx++) {
            world.setBlockState(center.add(dx, 5, -5), frame, HushStructureFeatureSupport.GEN_FLAGS);
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 1; dy <= 4; dy++) {
                world.setBlockState(center.add(dx, dy, -5), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            }
        }
        world.setBlockState(center.add(-4, 6, -5), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(4, 6, -5), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildDescendingHall(StructureWorldAccess world, BlockPos center, BlockState base, BlockState trim) {
        for (int step = 0; step <= 9; step++) {
            int y = -step;
            int z = -4 + step;
            for (int dx = -2; dx <= 2; dx++) {
                world.setBlockState(center.add(dx, y - 1, z), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, y, z), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, y + 1, z), Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
            }
            world.setBlockState(center.add(-3, y, z), trim, HushStructureFeatureSupport.GEN_FLAGS);
            world.setBlockState(center.add(3, y, z), trim, HushStructureFeatureSupport.GEN_FLAGS);
        }
    }

    private static void buildLowerSanctum(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState frame, BlockState lantern, BlockState altar, BlockState barrier, BlockState glass) {
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                world.setBlockState(center.add(dx, 0, dz), dx == 0 || dz == 0 ? trim : wall, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 1; dy <= 7; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 8 || Math.abs(dz) == 10 || dy == 7) {
                        if ((Math.abs(dx) == 8 && dy >= 2 && dy <= 4 && Math.abs(dz) <= 2) || (Math.abs(dz) == 10 && dy >= 2 && dy <= 4 && Math.abs(dx) <= 2)) {
                            world.setBlockState(pos, glass, HushStructureFeatureSupport.GEN_FLAGS);
                        } else {
                            world.setBlockState(pos, dy >= 6 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                        }
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(center.add(0, 1, 0), altar, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 2, -2), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(-4, 2, -7), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(4, 2, -7), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildCellWing(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState lantern, boolean west) {
        int side = west ? -1 : 1;
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 5; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (Math.abs(dx) == 3 || Math.abs(dz) == 5 || dy == 5) {
                        world.setBlockState(pos, wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        HushStructureFeatureSupport.placeDoor(world, center.add(-side * 3, 0, 0), west ? Direction.EAST : Direction.WEST);
        for (int dz = -4; dz <= 4; dz += 4) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos cell = center.add(dx, dy, dz);
                    world.setBlockState(cell, Math.abs(dx) == 1 || Math.abs(dz) == 4 || dy == 3 ? trim : Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                }
            }
        }
        world.setBlockState(center.add(0, 1, 3), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildRitualWing(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState frame, BlockState lantern, BlockState barrier) {
        BlockPos ritual = center.add(0, 0, -11);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                world.setBlockState(ritual.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 6; dy++) {
                    BlockPos pos = ritual.add(dx, dy, dz);
                    if (Math.abs(dx) == 4 || Math.abs(dz) == 4 || dy == 6) {
                        world.setBlockState(pos, dy >= 5 ? frame : wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        HushStructureFeatureSupport.placeDoor(world, ritual.add(0, 0, 4), Direction.SOUTH);
        world.setBlockState(ritual.add(0, 0, 0), trim, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(ritual.add(0, 2, 0), barrier, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(ritual.add(-2, 2, -2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(ritual.add(2, 2, -2), lantern, HushStructureFeatureSupport.GEN_FLAGS);
    }

    private static void buildVaultBranch(StructureWorldAccess world, BlockPos center, BlockState base, BlockState wall, BlockState trim, BlockState lantern, BlockState glass) {
        for (int dx = -2; dx <= 4; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(center.add(dx, -1, dz), base, HushStructureFeatureSupport.GEN_FLAGS);
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (dx == -2 || dx == 4 || Math.abs(dz) == 2 || dy == 4) {
                        world.setBlockState(pos, wall, HushStructureFeatureSupport.GEN_FLAGS);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), HushStructureFeatureSupport.GEN_FLAGS);
                    }
                }
            }
        }
        world.setBlockState(center.add(0, 1, -2), glass, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(0, 2, -2), glass, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(2, 1, 0), lantern, HushStructureFeatureSupport.GEN_FLAGS);
        world.setBlockState(center.add(1, 0, 0), trim, HushStructureFeatureSupport.GEN_FLAGS);
        HushStructureFeatureSupport.placeDoor(world, center.add(-2, 0, 0), Direction.WEST);
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        return HushStructureFeatureSupport.hasStableGround(world, center, 10, 16, 3, 18, 14);
    }
}
