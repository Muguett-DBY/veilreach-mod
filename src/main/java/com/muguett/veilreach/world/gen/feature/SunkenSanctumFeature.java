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
        BlockPos center = HushStructureFeatureSupport.findGenerationCenter(world, context.getOrigin());
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
        carveOuterRing(world, chamberCenter, polished, veilstone, lantern);
        carveDetentionHall(world, chamberCenter, polished, veilstone, lantern);
        carveVault(world, chamberCenter, polished, veilstone, lantern, glass);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamberCenter.add(0, 0, -2), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamberCenter.add(2, 0, 2), VeilreachEntities.LANTERNKEEPER);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamberCenter.add(-6, 0, 0), VeilreachEntities.WARDEN_OF_SILENCE);
        HushStructureFeatureSupport.spawnPersistentMob(world, chamberCenter.add(6, 0, 0), VeilreachEntities.VEILBOUND_WATCHER);

        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(-4, 0, -4), Direction.SOUTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(4, 0, -4), Direction.SOUTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(0, 0, 4), Direction.NORTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(-4, 0, 4), Direction.NORTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(0, 0, -8), Direction.SOUTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(0, 0, 8), Direction.NORTH, "chests/sunken_sanctum");
        HushStructureFeatureSupport.placeLootChest(world, chamberCenter.add(8, 0, 8), Direction.WEST, "chests/sunken_sanctum_vault");
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

    private static void carveOuterRing(StructureWorldAccess world, BlockPos chamberCenter, BlockState polished, BlockState veilstone, BlockState lantern) {
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                if (Math.abs(dx) < 7 && Math.abs(dz) < 7) {
                    continue;
                }
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = chamberCenter.add(dx, dy, dz);
                    if (Math.abs(dx) == 8 || Math.abs(dz) == 8 || dy == 4) {
                        world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
                world.setBlockState(chamberCenter.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
            }
        }
        world.setBlockState(chamberCenter.add(-8, 1, 0), lantern, Block.NOTIFY_ALL);
        world.setBlockState(chamberCenter.add(8, 1, 0), lantern, Block.NOTIFY_ALL);
        world.setBlockState(chamberCenter.add(0, 1, -8), lantern, Block.NOTIFY_ALL);
        world.setBlockState(chamberCenter.add(0, 1, 8), lantern, Block.NOTIFY_ALL);
    }

    private static void carveDetentionHall(StructureWorldAccess world, BlockPos chamberCenter, BlockState polished, BlockState veilstone, BlockState lantern) {
        for (int dz = -9; dz <= -6; dz++) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos pos = chamberCenter.add(dx, dy, dz);
                    if (Math.abs(dx) == 3 || dz == -9 || dy == 3) {
                        world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
                world.setBlockState(chamberCenter.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
            }
        }

        for (int side = -1; side <= 1; side += 2) {
            BlockPos cellBase = chamberCenter.add(side * 4, 0, -8);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = 0; dy <= 3; dy++) {
                        BlockPos pos = cellBase.add(dx, dy, dz);
                        if (Math.abs(dx) == 1 || Math.abs(dz) == 1 || dy == 3) {
                            world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                        } else {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                        }
                    }
                    world.setBlockState(cellBase.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
                }
            }
            HushStructureFeatureSupport.placeDoor(world, cellBase.add(0, 0, side == -1 ? 1 : -1), side == -1 ? Direction.SOUTH : Direction.NORTH);
            world.setBlockState(cellBase.add(0, 1, 0), lantern, Block.NOTIFY_ALL);
        }
    }

    private static void carveVault(StructureWorldAccess world, BlockPos chamberCenter, BlockState polished, BlockState veilstone, BlockState lantern, BlockState glass) {
        BlockPos vault = chamberCenter.add(8, 0, 8);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = vault.add(dx, dy, dz);
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2 || dy == 4) {
                        world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
                world.setBlockState(vault.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
            }
        }
        world.setBlockState(vault.add(0, 1, -2), glass, Block.NOTIFY_ALL);
        world.setBlockState(vault.add(0, 2, -2), glass, Block.NOTIFY_ALL);
        world.setBlockState(vault.add(0, 1, 0), lantern, Block.NOTIFY_ALL);
        HushStructureFeatureSupport.placeDoor(world, vault.add(-2, 0, 0), Direction.WEST);
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        return HushStructureFeatureSupport.hasStableGround(world, center, 6, 6, 3, 12, 12);
    }
}
