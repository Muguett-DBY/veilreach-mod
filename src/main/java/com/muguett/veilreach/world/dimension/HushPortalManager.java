package com.muguett.veilreach.world.dimension;

import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.world.gen.feature.ForgottenManorFeature;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HushPortalManager {
    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_HEIGHT = 5;
    private static final int PORTAL_SEARCH_RADIUS = 16;
    private static final int PORTAL_COOLDOWN_TICKS = 20 * 4;
    private static final BlockPos HUSHLANDS_ENTRY_ANCHOR = new BlockPos(0, 0, 0);
    private static final int ARRIVAL_PLATFORM_RADIUS = 3;
    private static final int ARRIVAL_CLEARANCE_HEIGHT = 6;
    private static final int ARRIVAL_MIN_SURFACE_Y = 72;
    private static final int ARRIVAL_ISLAND_RADIUS_X = 22;
    private static final int ARRIVAL_ISLAND_RADIUS_Z = 18;
    private static final int ARRIVAL_LANDMARK_OFFSET_X = 64;
    private static final int ARRIVAL_LANDMARK_OFFSET_Z = 20;
    private static final int ARRIVAL_SHRINE_RADIUS = 6;
    private static final Map<UUID, BlockPos> OVERWORLD_RETURN_PORTALS = new ConcurrentHashMap<>();

    private HushPortalManager() {
    }

    public static boolean tryActivate(ServerWorld world, BlockPos clickedPos, PlayerEntity player) {
        PortalFrame frame = findFrame(world, clickedPos);
        if (frame == null) {
            player.sendMessage(Text.translatable("message.veilreach.portal.invalid_frame"), true);
            return false;
        }

        fillPortal(world, frame);
        world.playSound(null, clickedPos, net.minecraft.sound.SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, net.minecraft.sound.SoundCategory.BLOCKS, 1.0F, 0.8F);
        player.sendMessage(Text.translatable("message.veilreach.portal.opened"), true);
        return true;
    }

    public static void teleport(ServerPlayerEntity player, BlockPos portalPos) {
        if (player.hasPortalCooldown()) {
            return;
        }
        ServerWorld currentWorld = player.getEntityWorld();
        ServerWorld targetWorld = currentWorld.getRegistryKey() == VeilreachDimensions.HUSHLANDS_WORLD
                ? currentWorld.getServer().getOverworld()
                : currentWorld.getServer().getWorld(VeilreachDimensions.HUSHLANDS_WORLD);
        if (targetWorld == null) {
            return;
        }

        boolean enteringHushlands = targetWorld.getRegistryKey() == VeilreachDimensions.HUSHLANDS_WORLD;
        if (enteringHushlands) {
            OVERWORLD_RETURN_PORTALS.put(player.getUuid(), portalPos.toImmutable());
        }

        BlockPos preferred = enteringHushlands
                ? HUSHLANDS_ENTRY_ANCHOR.withY(targetWorld.getSeaLevel())
                : OVERWORLD_RETURN_PORTALS.get(player.getUuid());
        BlockPos arrival;

        if (enteringHushlands) {
            arrival = findExistingPortal(targetWorld, preferred);
            if (arrival == null) {
                arrival = ensureArrivalPlatform(targetWorld, preferred);
            }
        } else {
            arrival = preferred != null ? findBoundReturnPortal(targetWorld, preferred) : null;
            if (arrival == null) {
                BlockPos fallbackAnchor = preferred != null ? preferred : new BlockPos(0, targetWorld.getSeaLevel(), 0);
                arrival = findSurfaceArrival(targetWorld, fallbackAnchor);
            }
        }

        player.setPortalCooldown(PORTAL_COOLDOWN_TICKS);
        player.teleport(targetWorld, arrival.getX() + 0.5D, arrival.getY(), arrival.getZ() + 0.5D, Set.of(), player.getYaw(), player.getPitch(), false);
    }

    private static BlockPos findBoundReturnPortal(ServerWorld world, BlockPos boundPortal) {
        world.getChunk(boundPortal.getX() >> 4, boundPortal.getZ() >> 4);

        BlockPos directPortal = descendPortalColumn(world, boundPortal);
        if (directPortal != null && isPortalLocationUsable(world, directPortal)) {
            return findSafeArrival(world, directPortal);
        }

        return findExistingPortal(world, boundPortal, 6, true);
    }

    private static BlockPos findExistingPortal(ServerWorld world, BlockPos preferred) {
        return findExistingPortal(world, preferred, PORTAL_SEARCH_RADIUS, false);
    }

    private static BlockPos findExistingPortal(ServerWorld world, BlockPos preferred, int searchRadius, boolean forceLoad) {
        if (preferred == null) {
            return null;
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double bestDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;

        for (int x = preferred.getX() - searchRadius; x <= preferred.getX() + searchRadius; x++) {
            for (int z = preferred.getZ() - searchRadius; z <= preferred.getZ() + searchRadius; z++) {
                long chunkPos = ChunkPos.toLong(x >> 4, z >> 4);
                if (!world.isChunkLoaded(chunkPos)) {
                    if (!forceLoad) {
                        continue;
                    }
                    world.getChunk(x >> 4, z >> 4);
                }

                for (int y = world.getBottomY(); y <= world.getTopYInclusive(); y++) {
                    mutable.set(x, y, z);
                    BlockPos portalBase = descendPortalColumn(world, mutable);
                    if (portalBase == null || !isPortalLocationUsable(world, portalBase)) {
                        continue;
                    }

                    double distance = portalBase.getSquaredDistance(preferred);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestPos = findSafeArrival(world, portalBase);
                    }
                }
            }
        }

        return bestPos;
    }

    private static BlockPos descendPortalColumn(ServerWorld world, BlockPos pos) {
        if (!world.getBlockState(pos).isOf(VeilreachBlocks.HUSH_PORTAL)) {
            return null;
        }

        BlockPos.Mutable mutable = pos.mutableCopy();
        while (world.getBlockState(mutable.down()).isOf(VeilreachBlocks.HUSH_PORTAL)) {
            // Walk to the base of the portal opening so players always land at the same height.
            mutable.move(Direction.DOWN);
        }
        return mutable.toImmutable();
    }

    private static BlockPos findSurfaceArrival(ServerWorld world, BlockPos anchor) {
        BlockPos surface = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, anchor);
        BlockPos base = new BlockPos(anchor.getX(), Math.max(surface.getY(), world.getSeaLevel() + 1), anchor.getZ());

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos candidate = base.offset(direction);
            if (isArrivalSpace(world, candidate)) {
                return candidate;
            }
        }

        for (int dy = 0; dy <= 3; dy++) {
            BlockPos candidate = base.up(dy);
            if (isArrivalSpace(world, candidate)) {
                return candidate;
            }
        }

        return base;
    }

    private static PortalFrame findFrame(ServerWorld world, BlockPos clickedPos) {
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
            for (int lateral = 0; lateral < FRAME_WIDTH; lateral++) {
                for (int vertical = 0; vertical < FRAME_HEIGHT; vertical++) {
                    BlockPos origin = axis == Direction.Axis.X
                            ? clickedPos.add(-lateral, -vertical, 0)
                            : clickedPos.add(0, -vertical, -lateral);
                    PortalFrame frame = validateFrame(world, origin, axis);
                    if (frame != null) {
                        return frame;
                    }
                }
            }
        }
        return null;
    }

    private static PortalFrame validateFrame(ServerWorld world, BlockPos origin, Direction.Axis axis) {
        for (int w = 0; w < FRAME_WIDTH; w++) {
            for (int h = 0; h < FRAME_HEIGHT; h++) {
                BlockPos pos = offset(origin, axis, w, h);
                boolean edge = w == 0 || w == FRAME_WIDTH - 1 || h == 0 || h == FRAME_HEIGHT - 1;
                Block block = world.getBlockState(pos).getBlock();
                if (edge) {
                    if (block != VeilreachBlocks.MOONWOVEN_FRAME) {
                        return null;
                    }
                } else {
                    if (!world.getBlockState(pos).isAir() && block != VeilreachBlocks.HUSH_PORTAL) {
                        return null;
                    }
                }
            }
        }
        return new PortalFrame(origin, axis);
    }

    private static void fillPortal(ServerWorld world, PortalFrame frame) {
        BlockState portalState = VeilreachBlocks.HUSH_PORTAL.getDefaultState();
        for (int w = 1; w < FRAME_WIDTH - 1; w++) {
            for (int h = 1; h < FRAME_HEIGHT - 1; h++) {
                world.setBlockState(offset(frame.origin(), frame.axis(), w, h), portalState, Block.NOTIFY_ALL);
            }
        }
    }

    private static BlockPos ensureArrivalPlatform(ServerWorld world, BlockPos preferred) {
        BlockPos base = resolveArrivalBase(world, preferred);
        buildArrivalIsland(world, base, ARRIVAL_ISLAND_RADIUS_X, ARRIVAL_ISLAND_RADIUS_Z, 7);

        for (int dx = -ARRIVAL_PLATFORM_RADIUS; dx <= ARRIVAL_PLATFORM_RADIUS; dx++) {
            for (int dz = -ARRIVAL_PLATFORM_RADIUS; dz <= ARRIVAL_PLATFORM_RADIUS; dz++) {
                world.setBlockState(base.add(dx, -1, dz), VeilreachBlocks.VEILSTONE.getDefaultState(), Block.NOTIFY_ALL);
                for (int dy = 0; dy <= ARRIVAL_CLEARANCE_HEIGHT; dy++) {
                    world.setBlockState(base.add(dx, dy, dz), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        ensureArrivalLandmark(world, base);
        buildArrivalShrine(world, base);
        buildFrame(world, base);
        return findSafeArrival(world, base.up());
    }

    private static BlockPos resolveArrivalBase(ServerWorld world, BlockPos preferred) {
        BlockPos surface = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, preferred);
        int fallbackY = Math.max(world.getSeaLevel() + 4, ARRIVAL_MIN_SURFACE_Y);
        int chosenY = surface.getY() < world.getSeaLevel() - 16 || surface.getY() <= world.getBottomY() + 8
                ? fallbackY
                : surface.getY() + 1;
        int maxSafeY = world.getTopYInclusive() - FRAME_HEIGHT - 8;
        return new BlockPos(preferred.getX(), Math.min(chosenY, maxSafeY), preferred.getZ());
    }

    private static void ensureArrivalLandmark(ServerWorld world, BlockPos base) {
        if (world.getRegistryKey() != VeilreachDimensions.HUSHLANDS_WORLD) {
            return;
        }

        BlockPos landmarkCenter = base.add(ARRIVAL_LANDMARK_OFFSET_X, 0, ARRIVAL_LANDMARK_OFFSET_Z);
        buildArrivalIsland(world, landmarkCenter, ARRIVAL_ISLAND_RADIUS_X + 8, ARRIVAL_ISLAND_RADIUS_Z + 4, 7);
        ForgottenManorFeature.generateAt(world, landmarkCenter, false);
    }

    private static void buildArrivalShrine(ServerWorld world, BlockPos base) {
        BlockState foundation = Blocks.POLISHED_DEEPSLATE.getDefaultState();
        BlockState trim = VeilreachBlocks.POLISHED_VEILSTONE.getDefaultState();
        BlockState inlay = Blocks.CALCITE.getDefaultState();
        BlockState pillar = Blocks.CHISELED_DEEPSLATE.getDefaultState();
        BlockState chain = Blocks.END_ROD.getDefaultState();
        BlockState lantern = VeilreachBlocks.STARLANTERN.getDefaultState();
        BlockState frame = VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState();

        for (int dx = -ARRIVAL_SHRINE_RADIUS; dx <= ARRIVAL_SHRINE_RADIUS; dx++) {
            for (int dz = -ARRIVAL_SHRINE_RADIUS; dz <= ARRIVAL_SHRINE_RADIUS; dz++) {
                int ring = Math.max(Math.abs(dx), Math.abs(dz));
                BlockState floor = ring == ARRIVAL_SHRINE_RADIUS
                        ? foundation
                        : ((dx == 0 || dz == 0) ? inlay : trim);
                world.setBlockState(base.add(dx, -1, dz), floor, Block.NOTIFY_ALL);

                for (int dy = 0; dy <= ARRIVAL_CLEARANCE_HEIGHT + 3; dy++) {
                    world.setBlockState(base.add(dx, dy, dz), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        for (int dx = -4; dx <= 4; dx += 8) {
            for (int dz = -4; dz <= 4; dz += 8) {
                BlockPos pillarBase = base.add(dx, 0, dz);
                for (int dy = 0; dy <= 4; dy++) {
                    world.setBlockState(pillarBase.up(dy), dy == 0 ? trim : pillar, Block.NOTIFY_ALL);
                }
                world.setBlockState(pillarBase.up(5), chain, Block.NOTIFY_ALL);
                world.setBlockState(pillarBase.up(6), lantern, Block.NOTIFY_ALL);
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            world.setBlockState(base.add(dx, 0, -2), frame, Block.NOTIFY_ALL);
            world.setBlockState(base.add(dx, 0, 2), frame, Block.NOTIFY_ALL);
        }
        for (int dz = -2; dz <= 2; dz++) {
            world.setBlockState(base.add(-3, 0, dz), foundation, Block.NOTIFY_ALL);
            world.setBlockState(base.add(3, 0, dz), foundation, Block.NOTIFY_ALL);
        }
    }

    private static void buildArrivalIsland(ServerWorld world, BlockPos center, int radiusX, int radiusZ, int depth) {
        BlockState top = VeilreachBlocks.MIRROR_GRASS.getDefaultState();
        BlockState fill = VeilreachBlocks.VEILSTONE.getDefaultState();

        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                double normalized = (dx * dx) / (double) (radiusX * radiusX) + (dz * dz) / (double) (radiusZ * radiusZ);
                if (normalized > 1.0D) {
                    continue;
                }

                int contourDrop = (int) Math.floor(normalized * depth);
                BlockPos topPos = center.add(dx, -1 - contourDrop, dz);
                world.setBlockState(topPos, top, Block.NOTIFY_ALL);

                for (int y = topPos.getY() - 1; y > world.getBottomY() + 1; y--) {
                    BlockPos current = new BlockPos(topPos.getX(), y, topPos.getZ());
                    BlockState state = world.getBlockState(current);
                    if (!state.isAir() && !state.isOf(Blocks.WATER) && !state.getFluidState().isStill()) {
                        break;
                    }
                    world.setBlockState(current, fill, Block.NOTIFY_ALL);
                }

                for (int y = topPos.getY() + 1; y <= center.getY() + ARRIVAL_CLEARANCE_HEIGHT; y++) {
                    world.setBlockState(new BlockPos(topPos.getX(), y, topPos.getZ()), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }
    }

    private static void buildFrame(ServerWorld world, BlockPos base) {
        BlockPos origin = base.add(-1, 0, 0);
        for (int w = 0; w < FRAME_WIDTH; w++) {
            for (int h = 0; h < FRAME_HEIGHT; h++) {
                BlockPos pos = offset(origin, Direction.Axis.X, w, h);
                boolean edge = w == 0 || w == FRAME_WIDTH - 1 || h == 0 || h == FRAME_HEIGHT - 1;
                world.setBlockState(pos, edge ? VeilreachBlocks.MOONWOVEN_FRAME.getDefaultState() : VeilreachBlocks.HUSH_PORTAL.getDefaultState(), Block.NOTIFY_ALL);
            }
        }
    }

    private static BlockPos findSafeArrival(ServerWorld world, BlockPos portalBase) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos candidate = portalBase.offset(direction);
            if (isArrivalSpace(world, candidate)) {
                return candidate;
            }
        }

        return portalBase;
    }

    private static boolean isPortalLocationUsable(ServerWorld world, BlockPos portalBase) {
        if (portalBase.getY() <= world.getBottomY() + 12) {
            return false;
        }
        if (world.getRegistryKey() == VeilreachDimensions.HUSHLANDS_WORLD && portalBase.getY() < world.getSeaLevel() - 16) {
            return false;
        }
        BlockPos safeArrival = findSafeArrival(world, portalBase);
        return isArrivalSpace(world, safeArrival);
    }

    private static boolean isArrivalSpace(ServerWorld world, BlockPos pos) {
        BlockPos headPos = pos.up();
        BlockPos floorPos = pos.down();
        return world.getBlockState(pos).isAir()
                && world.getBlockState(headPos).isAir()
                && world.getBlockState(floorPos).isSideSolidFullSquare(world, floorPos, Direction.UP);
    }

    private static BlockPos offset(BlockPos origin, Direction.Axis axis, int lateral, int vertical) {
        return axis == Direction.Axis.X ? origin.add(lateral, vertical, 0) : origin.add(0, vertical, lateral);
    }

    private record PortalFrame(BlockPos origin, Direction.Axis axis) {
    }
}
