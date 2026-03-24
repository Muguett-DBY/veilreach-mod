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
    private static final int MAX_STEPS = 34;
    private static final double STEP_DRAG = 0.98D;
    private static final double STEP_GRAVITY = 0.04D;
    private static final int STEP_INTERVAL = 3;

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

        Vec3d position = player.getEyePos().add(player.getRotationVector().multiply(0.4D));
        Vec3d velocity = player.getRotationVector().multiply(grenadeItem.getThrowSpeed()).add(player.getVelocity());

        for (int step = 0; step < MAX_STEPS; step++) {
            Vec3d nextPosition = position.add(velocity);
            HitResult hitResult = client.world.raycast(new RaycastContext(position, nextPosition, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
            if (hitResult.getType() != HitResult.Type.MISS) {
                Vec3d impact = hitResult.getPos();
                for (int i = 0; i < 8; i++) {
                    double angle = (Math.PI * 2.0D * i) / 8.0D;
                    double radius = 0.28D;
                    client.world.addParticleClient(ParticleTypes.END_ROD, impact.x + Math.cos(angle) * radius, impact.y + 0.02D, impact.z + Math.sin(angle) * radius, 0.0D, 0.0D, 0.0D);
                }
                for (int i = 0; i < 6; i++) {
                    client.world.addParticleClient(ParticleTypes.GLOW, impact.x, impact.y + (i * 0.16D), impact.z, 0.0D, 0.0D, 0.0D);
                }
                client.world.addParticleClient(ParticleTypes.CRIT, impact.x, impact.y, impact.z, 0.0D, 0.0D, 0.0D);
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    Vec3d normal = Vec3d.of(blockHitResult.getSide().getVector()).multiply(0.04D);
                    client.world.addParticleClient(ParticleTypes.GLOW, impact.x + normal.x, impact.y + normal.y, impact.z + normal.z, 0.0D, 0.0D, 0.0D);
                }
                return;
            }

            if (step % STEP_INTERVAL == 0) {
                double drift = 0.003D * MathHelper.sin((player.age + step) * 0.3F);
                client.world.addParticleClient(grenadeItem.showsFuseHud() ? ParticleTypes.END_ROD : ParticleTypes.GLOW, nextPosition.x, nextPosition.y, nextPosition.z, drift, 0.0D, -drift);
            }

            position = nextPosition;
            velocity = velocity.multiply(STEP_DRAG).add(0.0D, -STEP_GRAVITY, 0.0D);
        }

        for (int i = 0; i < 5; i++) {
            client.world.addParticleClient(ParticleTypes.GLOW, position.x, position.y + (i * 0.14D), position.z, 0.0D, 0.0D, 0.0D);
        }
        for (int i = 0; i < 4; i++) {
            double angle = (Math.PI * 2.0D * i) / 4.0D;
            client.world.addParticleClient(ParticleTypes.END_ROD, position.x + Math.cos(angle) * 0.18D, position.y, position.z + Math.sin(angle) * 0.18D, 0.0D, 0.0D, 0.0D);
        }
    }
}
