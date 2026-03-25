package com.muguett.veilreach.entity;

import com.muguett.veilreach.entity.vehicle.AbstractHoverVehicleEntity;
import com.muguett.veilreach.entity.vehicle.VehicleHudState;
import com.muguett.veilreach.entity.vehicle.VehicleWeaponMount;
import com.muguett.veilreach.network.LittleBirdActionPayload;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public final class RiftSkimmerEntity extends AbstractHoverVehicleEntity {
    private static final TrackedData<Integer> PRIMARY_AMMO = DataTracker.registerData(RiftSkimmerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PRIMARY_COOLDOWN = DataTracker.registerData(RiftSkimmerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PRIMARY_RELOAD = DataTracker.registerData(RiftSkimmerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final float MAX_HEALTH = 150.0F;
    public static final int MAX_PULSE_AMMO = 40;
    private static final int PRIMARY_COOLDOWN_TICKS = 3;
    private static final int PRIMARY_RELOAD_TICKS = 68;

    public RiftSkimmerEntity(EntityType<? extends RiftSkimmerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        initVehicleTrackerBase(builder);
        builder.add(PRIMARY_AMMO, MAX_PULSE_AMMO);
        builder.add(PRIMARY_COOLDOWN, 0);
        builder.add(PRIMARY_RELOAD, 0);
    }

    @Override
    protected void tickVehicleSystemsServer() {
        if (getPrimaryCooldown() > 0) {
            setPrimaryCooldown(getPrimaryCooldown() - 1);
        }
        if (getPrimaryReload() > 0) {
            setPrimaryReload(getPrimaryReload() - 1);
            if (getPrimaryReload() == 0) {
                setPrimaryAmmo(MAX_PULSE_AMMO);
            }
        }
    }

    @Override
    protected void tickVehicleEffectsClient() {
        if (age % 3 == 0) {
            getEntityWorld().addParticleClient(ParticleTypes.END_ROD, getX(), getY() + 0.28D, getZ(), 0.0D, -0.01D, 0.0D);
        }
    }

    @Override
    public void handleVehicleAction(ServerPlayerEntity pilot, LittleBirdActionPayload payload) {
        switch (payload.action()) {
            case LittleBirdActionPayload.UPDATE_CONTROLS ->
                    applyPilotInputs(payload.forward(), payload.turn(), payload.ascend(), payload.descend(), payload.yaw(), payload.pitch());
            case LittleBirdActionPayload.FIRE_ROCKET -> tryFirePulse(pilot, payload.yaw(), payload.pitch());
            default -> {
            }
        }
    }

    private void tryFirePulse(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        if (pilot != getControllingPassenger() || getPrimaryCooldown() > 0 || getPrimaryReload() > 0 || getPrimaryAmmo() <= 0) {
            return;
        }
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        Vec3d aim = Vec3d.fromPolar(aimPitch, aimYaw).normalize();
        Vec3d forward = Vec3d.fromPolar(0.0F, getYaw());
        Vec3d right = new Vec3d(-forward.z, 0.0D, forward.x);
        Vec3d launchPos = new Vec3d(getX(), getY() + 0.72D, getZ()).add(aim.multiply(1.24D)).add(right.multiply(0.14D));
        RiftPulseBoltEntity bolt = new RiftPulseBoltEntity(serverWorld, pilot);
        bolt.refreshPositionAndAngles(launchPos.x, launchPos.y, launchPos.z, aimYaw, aimPitch);
        bolt.setVelocity(aim.multiply(2.55D).add(getVelocity().multiply(0.75D)));
        serverWorld.spawnEntity(bolt);

        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_SKIMMER_PULSE, SoundCategory.HOSTILE, 0.92F, 1.18F);
        serverWorld.spawnParticles(ParticleTypes.END_ROD, launchPos.x, launchPos.y, launchPos.z, 10, 0.08D, 0.05D, 0.08D, 0.02D);
        serverWorld.spawnParticles(ParticleTypes.GLOW, launchPos.x, launchPos.y, launchPos.z, 6, 0.06D, 0.04D, 0.06D, 0.0D);

        setPrimaryAmmo(getPrimaryAmmo() - 1);
        setPrimaryCooldown(PRIMARY_COOLDOWN_TICKS);
        if (getPrimaryAmmo() <= 0) {
            setPrimaryReload(PRIMARY_RELOAD_TICKS);
        }
    }

    @Override
    public void rearmVehicle() {
        setPrimaryAmmo(MAX_PULSE_AMMO);
        setPrimaryCooldown(0);
        setPrimaryReload(0);
    }

    @Override
    public VehicleHudState createHudState(PlayerEntity viewer) {
        return new VehicleHudState(
                Text.translatable("entity.veilreach.rift_skimmer"),
                getHealthAmount(),
                MAX_HEALTH,
                getVelocity().horizontalLength() * 20.0D,
                (int) Math.round(getY()),
                getThrottle(),
                List.of(new VehicleWeaponMount(Text.translatable("hud.veilreach.rift_skimmer.pulse"), getPrimaryAmmo(), MAX_PULSE_AMMO, getPrimaryCooldown(), getPrimaryReload(), 0xFF93F4FF)),
                Text.translatable("hud.veilreach.vehicle.lock_unavailable"),
                Text.translatable("hud.veilreach.rift_skimmer.fire_hint"),
                0xFF8BE6FF,
                0xFFFFB37C,
                false,
                isDamaged(),
                isCriticalDamage()
        );
    }

    @Override
    public float getMaxHealthAmount() {
        return MAX_HEALTH;
    }

    @Override
    public float getLoopSoundVolume() {
        return Math.min(1.0F, 0.18F + (getThrottle() * 0.58F));
    }

    @Override
    public float getLoopSoundPitch() {
        return 0.92F + (getThrottle() * 0.34F);
    }

    @Override
    public Vec3d getPassengerAnchorOffset(net.minecraft.entity.Entity passenger) {
        return new Vec3d(0.0D, 0.9D + (passenger.getHeight() * 0.02D), -0.12D);
    }

    @Override
    protected SoundEvent getBoardingSound() {
        return SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE;
    }

    @Override
    public SoundEvent getLoopingSound() {
        return VeilreachSoundEvents.RIFT_SKIMMER_LOOP;
    }

    @Override
    protected float getForwardAcceleration() {
        return 0.19F;
    }

    @Override
    protected float getStrafeAcceleration() {
        return 0.18F;
    }

    @Override
    protected float getYawFollowRate() {
        return 13.5F;
    }

    @Override
    protected double getAscentRate() {
        return 0.28D;
    }

    @Override
    protected double getDescentRate() {
        return -0.26D;
    }

    @Override
    protected double getMaxHorizontalSpeed() {
        return 2.35D;
    }

    @Override
    protected double getMaxVerticalSpeed() {
        return 0.54D;
    }

    @Override
    protected float getCrashDamageThreshold() {
        return 0.85F;
    }

    @Override
    protected float getCrashDamageFactor() {
        return 11.0F;
    }

    @Override
    protected float getProjectileDamageMultiplier() {
        return 0.14F;
    }

    @Override
    protected float getExplosionDamageMultiplier() {
        return 0.48F;
    }

    @Override
    protected float getMeleeDamageMultiplier() {
        return 0.34F;
    }

    @Override
    protected float getAmbientDamageMultiplier() {
        return 0.32F;
    }

    @Override
    protected double getHorizontalDrag() {
        return 0.95D;
    }

    @Override
    protected double getVerticalDrag() {
        return 0.92D;
    }

    @Override
    protected double getVerticalResponse() {
        return 0.28D;
    }

    @Override
    protected float getPitchSpeedFactor() {
        return 15.0F;
    }

    @Override
    protected float getRollResponseFactor() {
        return 24.0F;
    }

    @Override
    protected float getBaseThrottle() {
        return 0.34F;
    }

    @Override
    protected float getForwardThrottleGain() {
        return 0.28F;
    }

    @Override
    protected double getHoverLiftBias() {
        return 0.015D;
    }

    @Override
    protected SoundEvent getHitSound() {
        return VeilreachSoundEvents.RIFT_SKIMMER_PULSE;
    }

    @Override
    protected SoundEvent getDestroySound() {
        return VeilreachSoundEvents.RIFT_SKIMMER_EXPLOSION;
    }

    @Override
    protected float getDestructionPower() {
        return 3.6F;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(VeilreachItems.RIFT_SKIMMER);
    }

    @Override
    protected void writeCustomData(WriteView writeView) {
        writeView.putInt("primary_ammo", getPrimaryAmmo());
        writeView.putInt("primary_cooldown", getPrimaryCooldown());
        writeView.putInt("primary_reload", getPrimaryReload());
        writeView.putFloat("health", getHealthAmount());
        writeView.putFloat("throttle", getThrottle());
        writeView.putFloat("visual_pitch", getVisualPitch());
        writeView.putFloat("visual_roll", getVisualRoll());
    }

    @Override
    protected void readCustomData(ReadView readView) {
        setPrimaryAmmo(readView.getInt("primary_ammo", MAX_PULSE_AMMO));
        setPrimaryCooldown(readView.getInt("primary_cooldown", 0));
        setPrimaryReload(readView.getInt("primary_reload", 0));
        setHealthAmount(readView.getFloat("health", MAX_HEALTH));
        setThrottle(readView.getFloat("throttle", 0.0F));
        setVisualPitch(readView.getFloat("visual_pitch", 0.0F));
        setVisualRoll(readView.getFloat("visual_roll", 0.0F));
    }

    private int getPrimaryAmmo() {
        return dataTracker.get(PRIMARY_AMMO);
    }

    private void setPrimaryAmmo(int value) {
        dataTracker.set(PRIMARY_AMMO, value);
    }

    private int getPrimaryCooldown() {
        return dataTracker.get(PRIMARY_COOLDOWN);
    }

    private void setPrimaryCooldown(int value) {
        dataTracker.set(PRIMARY_COOLDOWN, value);
    }

    private int getPrimaryReload() {
        return dataTracker.get(PRIMARY_RELOAD);
    }

    private void setPrimaryReload(int value) {
        dataTracker.set(PRIMARY_RELOAD, value);
    }
}
