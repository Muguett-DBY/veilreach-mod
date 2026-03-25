package com.muguett.veilreach.client;

import com.muguett.veilreach.item.ThrowableGrenadeItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public final class GrenadeTrajectoryPreview {
    private static final int MAX_STEPS = 32;
    private static final double STEP_DRAG = 0.98D;
    private static final double STEP_GRAVITY = 0.04D;
    private static final int STEP_INTERVAL = 4;

    private GrenadeTrajectoryPreview() {
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(GrenadeTrajectoryPreview::tick);
    }

    private static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (client.world == null || player == null || !player.isUsingItem()) {
            return;
        }

        ItemStack stack = player.getActiveItem();
        if (!(stack.getItem() instanceof ThrowableGrenadeItem grenadeItem)) {
            return;
        }
        if ((player.age & 1) != 0) {
            return;
        }

        spawnPreparationParticles(client, player, grenadeItem);

        Vec3d position = player.getEyePos().add(player.getRotationVector().multiply(0.4D));
        Vec3d velocity = player.getRotationVector().multiply(grenadeItem.getThrowSpeed()).add(player.getVelocity());

        for (int step = 0; step < MAX_STEPS; step++) {
            Vec3d nextPosition = position.add(velocity);
            HitResult hitResult = client.world.raycast(new RaycastContext(position, nextPosition, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
            if (hitResult.getType() != HitResult.Type.MISS) {
                spawnLandingMarker(client, grenadeItem, hitResult);
                return;
            }

            if (step % STEP_INTERVAL == 0) {
                spawnPathMarker(client, grenadeItem, player, nextPosition, step);
            }

            position = nextPosition;
            velocity = velocity.multiply(STEP_DRAG).add(0.0D, -STEP_GRAVITY, 0.0D);
        }

        spawnProjectedLandingMarker(client, grenadeItem, position);
    }

    private static void spawnPreparationParticles(MinecraftClient client, ClientPlayerEntity player, ThrowableGrenadeItem grenadeItem) {
        Vec3d lateral = player.getRotationVector().crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        if (lateral.lengthSquared() < 1.0E-4D) {
            lateral = new Vec3d(0.0D, 0.0D, 0.0D);
        }
        double side = player.getMainArm() == net.minecraft.util.Arm.RIGHT ? 0.18D : -0.18D;
        Vec3d source = player.getEyePos()
                .add(player.getRotationVector().multiply(0.42D))
                .add(lateral.multiply(side))
                .add(0.0D, -0.18D, 0.0D);

        switch (grenadeItem.getPreparationStyle()) {
            case SMOKE -> {
                client.world.addParticleClient(ParticleTypes.SMOKE, source.x, source.y, source.z, 0.0D, 0.01D, 0.0D);
                if ((player.age & 3) == 0) {
                    client.world.addParticleClient(ParticleTypes.CAMPFIRE_COSY_SMOKE, source.x, source.y + 0.02D, source.z, 0.0D, 0.01D, 0.0D);
                }
            }
            case SPARK -> {
                client.world.addParticleClient(ParticleTypes.GLOW, source.x, source.y, source.z, 0.0D, 0.0D, 0.0D);
                client.world.addParticleClient(ParticleTypes.CRIT, source.x, source.y + 0.02D, source.z, 0.0D, 0.0D, 0.0D);
            }
            case FIRE -> {
                client.world.addParticleClient(ParticleTypes.SMALL_FLAME, source.x, source.y, source.z, 0.0D, 0.02D, 0.0D);
                client.world.addParticleClient(ParticleTypes.SMOKE, source.x, source.y + 0.02D, source.z, 0.0D, 0.01D, 0.0D);
            }
        }
    }

    private static void spawnPathMarker(MinecraftClient client, ThrowableGrenadeItem grenadeItem, ClientPlayerEntity player, Vec3d position, int step) {
        double drift = 0.002D * MathHelper.sin((player.age + step) * 0.25F);
        switch (grenadeItem.getTrajectoryStyle()) {
            case FIRE -> client.world.addParticleClient(ParticleTypes.SMALL_FLAME, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
            case IMPACT -> client.world.addParticleClient(ParticleTypes.GLOW, position.x, position.y, position.z, drift, 0.0D, -drift);
            case DELAYED -> client.world.addParticleClient(ParticleTypes.END_ROD, position.x, position.y, position.z, drift, 0.0D, -drift);
        }
    }

    private static void spawnLandingMarker(MinecraftClient client, ThrowableGrenadeItem grenadeItem, HitResult hitResult) {
        Vec3d impact = hitResult.getPos();
        double radius = grenadeItem.getTrajectoryStyle() == ThrowableGrenadeItem.TrajectoryStyle.FIRE ? 0.36D : 0.3D;
        int ringPoints = grenadeItem.getTrajectoryStyle() == ThrowableGrenadeItem.TrajectoryStyle.DELAYED ? 4 : 6;

        for (int i = 0; i < ringPoints; i++) {
            double angle = (Math.PI * 2.0D * i) / ringPoints;
            double x = impact.x + Math.cos(angle) * radius;
            double z = impact.z + Math.sin(angle) * radius;
            switch (grenadeItem.getTrajectoryStyle()) {
                case FIRE -> client.world.addParticleClient(ParticleTypes.SMALL_FLAME, x, impact.y + 0.02D, z, 0.0D, 0.0D, 0.0D);
                case IMPACT -> client.world.addParticleClient(ParticleTypes.END_ROD, x, impact.y + 0.02D, z, 0.0D, 0.0D, 0.0D);
                case DELAYED -> client.world.addParticleClient(ParticleTypes.GLOW, x, impact.y + 0.02D, z, 0.0D, 0.0D, 0.0D);
            }
        }

        for (int i = 0; i < 4; i++) {
            double y = impact.y + (i * 0.18D);
            switch (grenadeItem.getTrajectoryStyle()) {
                case FIRE -> client.world.addParticleClient(ParticleTypes.SMALL_FLAME, impact.x, y, impact.z, 0.0D, 0.0D, 0.0D);
                case IMPACT -> client.world.addParticleClient(ParticleTypes.GLOW, impact.x, y, impact.z, 0.0D, 0.0D, 0.0D);
                case DELAYED -> client.world.addParticleClient(ParticleTypes.END_ROD, impact.x, y, impact.z, 0.0D, 0.0D, 0.0D);
            }
        }

        client.world.addParticleClient(grenadeItem.getTrajectoryStyle() == ThrowableGrenadeItem.TrajectoryStyle.FIRE ? ParticleTypes.FLAME : ParticleTypes.CRIT, impact.x, impact.y, impact.z, 0.0D, 0.0D, 0.0D);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            Vec3d normal = Vec3d.of(blockHitResult.getSide().getVector()).multiply(0.05D);
            client.world.addParticleClient(ParticleTypes.GLOW, impact.x + normal.x, impact.y + normal.y, impact.z + normal.z, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void spawnProjectedLandingMarker(MinecraftClient client, ThrowableGrenadeItem grenadeItem, Vec3d position) {
        for (int i = 0; i < 3; i++) {
            client.world.addParticleClient(ParticleTypes.GLOW, position.x, position.y + (i * 0.16D), position.z, 0.0D, 0.0D, 0.0D);
        }
        double radius = 0.22D;
        for (int i = 0; i < 4; i++) {
            double angle = (Math.PI * 2.0D * i) / 4.0D;
            client.world.addParticleClient(grenadeItem.getTrajectoryStyle() == ThrowableGrenadeItem.TrajectoryStyle.FIRE ? ParticleTypes.SMALL_FLAME : ParticleTypes.END_ROD, position.x + Math.cos(angle) * radius, position.y, position.z + Math.sin(angle) * radius, 0.0D, 0.0D, 0.0D);
        }
    }
}
