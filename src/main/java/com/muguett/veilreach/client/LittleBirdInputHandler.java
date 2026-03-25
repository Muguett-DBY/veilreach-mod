package com.muguett.veilreach.client;

import com.muguett.veilreach.entity.vehicle.AbstractArmedVehicleEntity;
import com.muguett.veilreach.entity.vehicle.AbstractVehicleEntity;
import com.muguett.veilreach.network.LittleBirdActionPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public final class LittleBirdInputHandler {
    private static final KeyBinding.Category VEHICLE_CATEGORY = KeyBinding.Category.create(Identifier.of("veilreach", "vehicles"));

    private static final KeyBinding FIRE_ROCKET = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.veilreach.little_bird_fire",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            VEHICLE_CATEGORY
    ));
    private static final KeyBinding DROP_BOMB = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.veilreach.little_bird_bomb",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            VEHICLE_CATEGORY
    ));
    private static final KeyBinding LOCK_TRACKING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.veilreach.little_bird_lock",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            VEHICLE_CATEGORY
    ));
    private static final KeyBinding FIRE_TRACKING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.veilreach.little_bird_fire_tracking",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            VEHICLE_CATEGORY
    ));
    private static final KeyBinding CLEAR_TRACKING_LOCKS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.veilreach.little_bird_clear_locks",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            VEHICLE_CATEGORY
    ));
    private static final KeyBinding DESCEND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.veilreach.little_bird_descend",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            VEHICLE_CATEGORY
    ));

    private static VehicleLoopSoundInstance vehicleLoopSound;
    private static int soundingVehicleId = -1;

    private LittleBirdInputHandler() {
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(LittleBirdInputHandler::tick);
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            stopLoopSound(client);
            return;
        }

        if (!(client.player.getVehicle() instanceof AbstractArmedVehicleEntity vehicle) || vehicle.getControllingPassenger() != client.player) {
            stopLoopSound(client);
            return;
        }

        ensureLoopSound(client, vehicle);
        spawnLockIndicators(client, vehicle);

        float forward = (client.options.forwardKey.isPressed() ? 1.0F : 0.0F) - (client.options.backKey.isPressed() ? 0.65F : 0.0F);
        float turn = (client.options.rightKey.isPressed() ? 1.0F : 0.0F) - (client.options.leftKey.isPressed() ? 1.0F : 0.0F);
        boolean ascend = client.options.jumpKey.isPressed();
        boolean descend = DESCEND.isPressed();
        vehicle.applyPilotInputs(forward, turn, ascend, descend, client.player.getYaw(), client.player.getPitch());
        ClientPlayNetworking.send(LittleBirdActionPayload.controls(forward, turn, ascend, descend, client.player.getYaw(), client.player.getPitch()));

        while (FIRE_ROCKET.wasPressed()) {
            ClientPlayNetworking.send(LittleBirdActionPayload.fireRocket(client.player.getYaw(), client.player.getPitch()));
        }

        while (DROP_BOMB.wasPressed()) {
            ClientPlayNetworking.send(LittleBirdActionPayload.dropBomb(client.player.getYaw(), client.player.getPitch()));
        }

        while (LOCK_TRACKING.wasPressed()) {
            ClientPlayNetworking.send(LittleBirdActionPayload.lockTracking(client.player.getYaw(), client.player.getPitch()));
        }

        while (FIRE_TRACKING.wasPressed()) {
            ClientPlayNetworking.send(LittleBirdActionPayload.fireTracking());
        }

        while (CLEAR_TRACKING_LOCKS.wasPressed()) {
            ClientPlayNetworking.send(LittleBirdActionPayload.clearTrackingLocks());
        }
    }

    private static void spawnLockIndicators(MinecraftClient client, AbstractArmedVehicleEntity vehicle) {
        long time = client.world.getTime();
        for (Entity target : vehicle.getHighlightedTargets()) {
            double radius = Math.max(0.55D, target.getWidth() * 0.8D);
            double baseY = target.getY() + (target.getHeight() * 0.62D);
            double phase = (time * 0.18D) + (target.getId() * 0.35D);
            for (int index = 0; index < 4; index++) {
                double angle = phase + (index * (Math.PI * 0.5D));
                double px = target.getX() + (MathHelper.cos((float) angle) * radius);
                double pz = target.getZ() + (MathHelper.sin((float) angle) * radius);
                client.world.addParticleClient(ParticleTypes.GLOW, px, baseY, pz, 0.0D, 0.0D, 0.0D);
            }
            client.world.addParticleClient(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + target.getHeight() + 0.18D, target.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    private static void ensureLoopSound(MinecraftClient client, AbstractVehicleEntity vehicle) {
        SoundEvent loopSound = vehicle.getLoopingSound();
        if (loopSound == null) {
            stopLoopSound(client);
            return;
        }
        if (vehicleLoopSound != null && vehicleLoopSound.isDone()) {
            vehicleLoopSound = null;
            soundingVehicleId = -1;
        }
        if (vehicleLoopSound == null || soundingVehicleId != vehicle.getId()) {
            stopLoopSound(client);
            vehicleLoopSound = new VehicleLoopSoundInstance(vehicle, loopSound);
            soundingVehicleId = vehicle.getId();
            client.getSoundManager().play(vehicleLoopSound);
        }
    }

    private static void stopLoopSound(MinecraftClient client) {
        if (vehicleLoopSound != null) {
            client.getSoundManager().stop(vehicleLoopSound);
            vehicleLoopSound = null;
            soundingVehicleId = -1;
        }
    }
}
