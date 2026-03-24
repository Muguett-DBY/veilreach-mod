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
        BlockPos center = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin());
        if (!hasStableGround(world, center)) {
            return false;
        }

        BlockState polished = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState veilstone = VeilreachBlocks.VEILSTONE.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState glass = VeilreachBlocks.RIFTGLASS.getDefaultState();
        BlockState cache = VeilreachBlocks.STARLIGHT_CACHE.getDefaultState();
        BlockState altar = VeilreachBlocks.AWAKENED_ALTAR.getDefaultState();

        HushStructureFeatureSupport.clearBox(world, center, 12, 18, 10);

        for (int dx = -9; dx <= 9; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                world.setBlockState(center.add(dx, -1, dz), Math.abs(dx) == 9 || Math.abs(dz) == 7 ? polished : veilstone, Block.NOTIFY_ALL);
                if (Math.abs(dx) <= 8 && Math.abs(dz) <= 6) {
                    world.setBlockState(center.add(dx, 0, dz), polished, Block.NOTIFY_ALL);
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
                        world.setBlockState(pos, frame, Block.NOTIFY_ALL);
                    } else if (dy == 4 || dy == 9) {
                        world.setBlockState(pos, polished, Block.NOTIFY_ALL);
                    } else if (Math.abs(dz) == 6 && Math.abs(dx) <= 5 && Math.abs(dx) >= 2 && dy >= 2 && dy <= 6) {
                        world.setBlockState(pos, glass, Block.NOTIFY_ALL);
                    } else {
                        world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                    }
                }
            }
        }

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                if (Math.abs(dx) <= 6 && Math.abs(dz) <= 4) {
                    world.setBlockState(center.add(dx, 10, dz), polished, Block.NOTIFY_ALL);
                }
            }
        }

        for (int layer = 0; layer <= 3; layer++) {
            int radiusX = 8 - layer;
            int radiusZ = 6 - layer;
            for (int dx = -radiusX; dx <= radiusX; dx++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    if (Math.abs(dx) == radiusX || Math.abs(dz) == radiusZ) {
                        world.setBlockState(center.add(dx, 11 + layer, dz), polished, Block.NOTIFY_ALL);
                    }
                }
            }
        }

        for (int x = -6; x <= 6; x += 12) {
            for (int z = -4; z <= 4; z += 8) {
                for (int dy = 1; dy <= 13; dy++) {
                    world.setBlockState(center.add(x, dy, z), frame, Block.NOTIFY_ALL);
                }
                world.setBlockState(center.add(x, 14, z), lantern, Block.NOTIFY_ALL);
            }
        }

        carveDoorway(world, center.south(6));
        carveCrypt(world, center, polished, veilstone, lantern, altar);
        carveSideChapels(world, center, polished, veilstone, glass, lantern);
        carveReliquary(world, center, polished, veilstone, lantern);
        carveHiddenSacristy(world, center, polished, veilstone, lantern, glass);

        world.setBlockState(center.add(0, 1, -2), cache, Block.NOTIFY_ALL);
        world.setBlockState(center.add(0, 1, 2), cache, Block.NOTIFY_ALL);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-2, 1, 1), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(2, 1, 1), VeilreachEntities.LANTERNKEEPER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(0, -4, 2), VeilreachEntities.MOONFALLEN_KNIGHT);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(-7, 1, 0), VeilreachEntities.VEILBOUND_WATCHER);
        HushStructureFeatureSupport.spawnPersistentMob(world, center.add(7, 1, 0), VeilreachEntities.WARDEN_OF_SILENCE);
        HushStructureFeatureSupport.placeLootChest(world, center.add(-5, 1, -4), Direction.SOUTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(5, 1, -4), Direction.SOUTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, -4, 0), Direction.NORTH, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(-7, 1, 4), Direction.WEST, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(7, 1, 4), Direction.EAST, "chests/moonfall_cathedral");
        HushStructureFeatureSupport.placeLootChest(world, center.add(0, 1, -11), Direction.SOUTH, "chests/moonfall_cathedral_vault");
        return true;
    }

    private static void carveDoorway(StructureWorldAccess world, BlockPos doorBase) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 4; dy++) {
                world.setBlockState(doorBase.add(dx, dy, 0), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
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
                        world.setBlockState(pos, polished, Block.NOTIFY_ALL);
                    } else if (Math.abs(dx) == 4 || Math.abs(dz) == 4 || dy == 4) {
                        world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
            }
        }

        for (int step = 0; step <= 4; step++) {
            BlockPos stair = center.add(0, -step, -1 - step);
            world.setBlockState(stair.down(), polished, Block.NOTIFY_ALL);
            world.setBlockState(stair, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            world.setBlockState(stair.up(), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }

        world.setBlockState(crypt, altar, Block.NOTIFY_ALL);
        world.setBlockState(crypt.north(2), lantern, Block.NOTIFY_ALL);
        world.setBlockState(crypt.south(2), lantern, Block.NOTIFY_ALL);
    }

    private static void carveSideChapels(StructureWorldAccess world, BlockPos center, BlockState polished, BlockState veilstone, BlockState glass, BlockState lantern) {
        for (int side = -1; side <= 1; side += 2) {
            BlockPos chapel = center.add(side * 10, 0, 0);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    for (int dy = 0; dy <= 6; dy++) {
                        BlockPos pos = chapel.add(dx, dy, dz);
                        if (Math.abs(dx) == 2 || Math.abs(dz) == 3 || dy == 6) {
                            world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                        } else {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                        }
                    }
                    world.setBlockState(chapel.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
                }
            }
            world.setBlockState(chapel.add(0, 2, -3), glass, Block.NOTIFY_ALL);
            world.setBlockState(chapel.add(0, 3, -3), glass, Block.NOTIFY_ALL);
            world.setBlockState(chapel.add(0, 1, 0), lantern, Block.NOTIFY_ALL);
        }
    }

    private static void carveReliquary(StructureWorldAccess world, BlockPos center, BlockState polished, BlockState veilstone, BlockState lantern) {
        BlockPos reliquary = center.add(0, 0, -8);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = reliquary.add(dx, dy, dz);
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2 || dy == 4) {
                        world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
                world.setBlockState(reliquary.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
            }
        }
        world.setBlockState(reliquary.add(0, 1, 0), lantern, Block.NOTIFY_ALL);
        HushStructureFeatureSupport.placeDoor(world, reliquary.add(0, 0, 2), Direction.SOUTH);
    }

    private static void carveHiddenSacristy(StructureWorldAccess world, BlockPos center, BlockState polished, BlockState veilstone, BlockState lantern, BlockState glass) {
        BlockPos sacristy = center.add(0, 1, -11);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    BlockPos pos = sacristy.add(dx, dy, dz);
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2 || dy == 4) {
                        world.setBlockState(pos, veilstone, Block.NOTIFY_ALL);
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
                world.setBlockState(sacristy.add(dx, -1, dz), polished, Block.NOTIFY_ALL);
            }
        }
        world.setBlockState(sacristy.add(0, 1, -2), glass, Block.NOTIFY_ALL);
        world.setBlockState(sacristy.add(0, 2, -2), glass, Block.NOTIFY_ALL);
        world.setBlockState(sacristy.add(0, 1, 0), lantern, Block.NOTIFY_ALL);
        HushStructureFeatureSupport.placeDoor(world, sacristy.add(0, 0, 2), Direction.SOUTH);
    }

    private static boolean hasStableGround(StructureWorldAccess world, BlockPos center) {
        for (int dx = -9; dx <= 9; dx += 3) {
            for (int dz = -7; dz <= 7; dz += 3) {
                if (!HushStructureFeatureSupport.isHushGround(world, center.add(dx, -1, dz))) {
                    return false;
                }
            }
        }
        return true;
    }
}
