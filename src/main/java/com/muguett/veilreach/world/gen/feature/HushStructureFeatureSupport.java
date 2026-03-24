package com.muguett.veilreach.world.gen.feature;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.fluid.Fluids;

final class HushStructureFeatureSupport {
    static final int GEN_FLAGS = Block.NOTIFY_LISTENERS | Block.FORCE_STATE;

    private HushStructureFeatureSupport() {
    }

    static boolean isHushGround(StructureWorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isOf(VeilreachBlocks.MIRROR_GRASS)
                || state.isOf(VeilreachBlocks.VEILSTONE)
                || state.isOf(VeilreachBlocks.POLISHED_VEILSTONE)
                || state.isOf(Blocks.GRASS_BLOCK)
                || state.isOf(Blocks.DIRT)
                || state.isOf(Blocks.STONE)
                || state.isOf(Blocks.DEEPSLATE)
                || state.isOf(Blocks.COARSE_DIRT);
    }

    static BlockPos findGenerationCenter(StructureWorldAccess world, BlockPos origin) {
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, origin);
    }

    static boolean hasStableGround(
            StructureWorldAccess world,
            BlockPos center,
            int radiusX,
            int radiusZ,
            int step,
            int minValidSamples,
            int maxHeightVariance
    ) {
        int validSamples = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int dx = -radiusX; dx <= radiusX; dx += step) {
            for (int dz = -radiusZ; dz <= radiusZ; dz += step) {
                BlockPos surface = findGenerationCenter(world, center.add(dx, 0, dz));
                minY = Math.min(minY, surface.getY());
                maxY = Math.max(maxY, surface.getY());
                if (isHushGround(world, surface.down()) && !world.getFluidState(surface.down()).isOf(Fluids.WATER)) {
                    validSamples++;
                }
            }
        }

        return validSamples >= minValidSamples && maxY - minY <= maxHeightVariance;
    }

    static void supportFootprint(
            StructureWorldAccess world,
            BlockPos center,
            int minX,
            int maxX,
            int minZ,
            int maxZ,
            int floorOffsetY,
            BlockState fillState
    ) {
        for (int dx = minX; dx <= maxX; dx++) {
            for (int dz = minZ; dz <= maxZ; dz++) {
                fillDown(world, center.add(dx, floorOffsetY, dz), fillState);
            }
        }
    }

    static void fillDown(StructureWorldAccess world, BlockPos start, BlockState fillState) {
        for (int y = start.getY(); y > world.getBottomY() + 1; y--) {
            BlockPos current = new BlockPos(start.getX(), y, start.getZ());
            BlockState existing = world.getBlockState(current);
            if (!needsFoundation(existing)) {
                break;
            }
            world.setBlockState(current, fillState, GEN_FLAGS);
        }
    }

    private static boolean needsFoundation(BlockState state) {
        return state.isAir()
                || state.isOf(Blocks.WATER)
                || state.isOf(Blocks.SEAGRASS)
                || state.isOf(Blocks.TALL_SEAGRASS)
                || state.isOf(Blocks.KELP)
                || state.isOf(Blocks.KELP_PLANT)
                || state.getFluidState().isOf(Fluids.WATER)
                || state.isReplaceable();
    }

    static void clearBox(StructureWorldAccess world, BlockPos center, int radiusX, int height, int radiusZ) {
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                for (int dy = 0; dy <= height; dy++) {
                    world.setBlockState(center.add(dx, dy, dz), Blocks.AIR.getDefaultState(), GEN_FLAGS);
                }
            }
        }
    }

    static void placeLootChest(StructureWorldAccess world, BlockPos pos, Direction facing, String lootPath) {
        world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, facing), GEN_FLAGS);
        if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(VeilreachMod.MOD_ID, lootPath)));
            chest.setLootTableSeed(world.getRandom().nextLong());
        }
    }

    static void placeDoor(StructureWorldAccess world, BlockPos basePos, Direction facing) {
        BlockState lower = Blocks.DARK_OAK_DOOR.getDefaultState()
                .with(DoorBlock.FACING, facing)
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState upper = lower.with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
        world.setBlockState(basePos, lower, GEN_FLAGS);
        world.setBlockState(basePos.up(), upper, GEN_FLAGS);
    }

    static <T extends MobEntity> void spawnPersistentMob(StructureWorldAccess world, BlockPos pos, EntityType<T> entityType) {
        T mob = entityType.spawn(world.toServerWorld(), pos, SpawnReason.STRUCTURE);
        if (mob != null) {
            mob.setPersistent();
        }
    }
}
