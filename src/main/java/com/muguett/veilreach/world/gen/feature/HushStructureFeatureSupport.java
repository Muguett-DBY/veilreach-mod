package com.muguett.veilreach.world.gen.feature;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;

final class HushStructureFeatureSupport {
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

    static void clearBox(StructureWorldAccess world, BlockPos center, int radiusX, int height, int radiusZ) {
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                for (int dy = 0; dy <= height; dy++) {
                    world.setBlockState(center.add(dx, dy, dz), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }
    }

    static void placeLootChest(StructureWorldAccess world, BlockPos pos, Direction facing, String lootPath) {
        world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, facing), Block.NOTIFY_ALL);
        if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(VeilreachMod.MOD_ID, lootPath)));
            chest.setLootTableSeed(world.getRandom().nextLong());
        }
    }

    static <T extends MobEntity> void spawnPersistentMob(StructureWorldAccess world, BlockPos pos, EntityType<T> entityType) {
        T mob = entityType.spawn(world.toServerWorld(), pos, SpawnReason.STRUCTURE);
        if (mob != null) {
            mob.setPersistent();
        }
    }
}
