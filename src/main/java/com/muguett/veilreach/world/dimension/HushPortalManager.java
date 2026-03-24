package com.muguett.veilreach.world.dimension;

import com.muguett.veilreach.registry.VeilreachBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.Set;

public final class HushPortalManager {
    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_HEIGHT = 5;
    private static final int PORTAL_SEARCH_RADIUS = 16;
    private static final int PORTAL_COOLDOWN_TICKS = 20 * 4;

    private HushPortalManager() {
    }

    public static boolean tryActivate(ServerWorld world, BlockPos clickedPos, PlayerEntity player) {
        PortalFrame frame = findFrame(world, clickedPos);
        if (frame == null) {
            player.sendMessage(Text.literal("The sigil needs a complete Moonwoven Frame."), true);
            return false;
        }

        fillPortal(world, frame);
        world.playSound(null, clickedPos, net.minecraft.sound.SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, net.minecraft.sound.SoundCategory.BLOCKS, 1.0F, 0.8F);
        player.sendMessage(Text.literal("The air parts. A gate to the Hushlands opens."), true);
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

        BlockPos preferred = portalPos.withY(targetWorld.getSeaLevel());
        BlockPos arrival = findExistingPortal(targetWorld, preferred);
        if (arrival == null) {
            arrival = ensureArrivalPlatform(targetWorld, preferred);
        }
        player.setPortalCooldown(PORTAL_COOLDOWN_TICKS);
        player.teleport(targetWorld, arrival.getX() + 0.5D, arrival.getY(), arrival.getZ() + 0.5D, Set.of(), player.getYaw(), player.getPitch(), false);
    }

    private static BlockPos findExistingPortal(ServerWorld world, BlockPos preferred) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double bestDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;

        for (int x = preferred.getX() - PORTAL_SEARCH_RADIUS; x <= preferred.getX() + PORTAL_SEARCH_RADIUS; x++) {
            for (int z = preferred.getZ() - PORTAL_SEARCH_RADIUS; z <= preferred.getZ() + PORTAL_SEARCH_RADIUS; z++) {
                for (int y = world.getBottomY(); y <= world.getTopYInclusive(); y++) {
                    mutable.set(x, y, z);
                    if (!world.getBlockState(mutable).isOf(VeilreachBlocks.HUSH_PORTAL)) {
                        continue;
                    }

                    while (world.getBlockState(mutable.down()).isOf(VeilreachBlocks.HUSH_PORTAL)) {
                        // Walk to the base of the portal opening so players always land at the same height.
                        mutable.move(Direction.DOWN);
                    }

                    double distance = mutable.getSquaredDistance(preferred);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestPos = findSafeArrival(world, mutable.toImmutable());
                    }
                }
            }
        }

        return bestPos;
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
        BlockPos center = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, preferred);
        BlockPos base = center.up(1);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlockState(base.add(dx, -1, dz), VeilreachBlocks.VEILSTONE.getDefaultState(), Block.NOTIFY_ALL);
                for (int dy = 0; dy <= 4; dy++) {
                    world.setBlockState(base.add(dx, dy, dz), net.minecraft.block.Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        buildFrame(world, base);
        return findSafeArrival(world, base.up());
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
