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

public final class RiftSiegePlatformEntity extends AbstractHoverVehicleEntity {
    private static final TrackedData<Integer> MAIN_AMMO = DataTracker.registerData(RiftSiegePlatformEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MAIN_COOLDOWN = DataTracker.registerData(RiftSiegePlatformEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MAIN_RELOAD = DataTracker.registerData(RiftSiegePlatformEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SUPPRESS_AMMO = DataTracker.registerData(RiftSiegePlatformEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SUPPRESS_COOLDOWN = DataTracker.registerData(RiftSiegePlatformEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SUPPRESS_RELOAD = DataTracker.registerData(RiftSiegePlatformEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final float MAX_HEALTH = 560.0F;
    public static final int MAX_MAIN_AMMO = 6;
    public static final int MAX_SUPPRESS_AMMO = 10;
    private static final int MAIN_COOLDOWN_TICKS = 18;
    private static final int MAIN_RELOAD_TICKS = 96;
    private static final int SUPPRESS_COOLDOWN_TICKS = 10;
    private static final int SUPPRESS_RELOAD_TICKS = 84;

    public RiftSiegePlatformEntity(EntityType<? extends RiftSiegePlatformEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        initVehicleTrackerBase(builder);
        builder.add(MAIN_AMMO, MAX_MAIN_AMMO);
        builder.add(MAIN_COOLDOWN, 0);
        builder.add(MAIN_RELOAD, 0);
        builder.add(SUPPRESS_AMMO, MAX_SUPPRESS_AMMO);
        builder.add(SUPPRESS_COOLDOWN, 0);
        builder.add(SUPPRESS_RELOAD, 0);
    }

    @Override
    protected void tickVehicleSystemsServer() {
        if (getMainCooldown() > 0) {
            setMainCooldown(getMainCooldown() - 1);
        }
        if (getSuppressCooldown() > 0) {
            setSuppressCooldown(getSuppressCooldown() - 1);
        }
        if (getMainReload() > 0) {
            setMainReload(getMainReload() - 1);
            if (getMainReload() == 0) {
                setMainAmmo(MAX_MAIN_AMMO);
            }
        }
        if (getSuppressReload() > 0) {
            setSuppressReload(getSuppressReload() - 1);
            if (getSuppressReload() == 0) {
                setSuppressAmmo(MAX_SUPPRESS_AMMO);
            }
        }
    }

    @Override
    protected void tickVehicleEffectsClient() {
        if (age % 4 == 0) {
            getEntityWorld().addParticleClient(ParticleTypes.GLOW, getX(), getY() + 0.35D, getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void handleVehicleAction(ServerPlayerEntity pilot, LittleBirdActionPayload payload) {
        switch (payload.action()) {
            case LittleBirdActionPayload.UPDATE_CONTROLS ->
                    applyPilotInputs(payload.forward(), payload.turn(), payload.ascend(), payload.descend(), payload.yaw(), payload.pitch());
            case LittleBirdActionPayload.FIRE_ROCKET -> tryFireMainCannon(pilot, payload.yaw(), payload.pitch());
            case LittleBirdActionPayload.DROP_BOMB -> tryFireSuppressor(pilot, payload.yaw(), payload.pitch());
            default -> {
            }
        }
    }

    private void tryFireMainCannon(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        if (pilot != getControllingPassenger() || getMainCooldown() > 0 || getMainReload() > 0 || getMainAmmo() <= 0) {
            return;
        }
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        Vec3d aim = Vec3d.fromPolar(aimPitch, aimYaw).normalize();
        Vec3d launchPos = new Vec3d(getX(), getY() + 0.96D, getZ()).add(aim.multiply(1.6D));
        RiftSiegeShellEntity shell = new RiftSiegeShellEntity(serverWorld, pilot);
        shell.refreshPositionAndAngles(launchPos.x, launchPos.y, launchPos.z, aimYaw, aimPitch);
        shell.setVelocity(aim.multiply(1.9D).add(getVelocity().multiply(0.55D)));
        serverWorld.spawnEntity(shell);

        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_SIEGE_CANNON, SoundCategory.HOSTILE, 1.0F, 0.92F);
        serverWorld.spawnParticles(ParticleTypes.CLOUD, launchPos.x, launchPos.y, launchPos.z, 16, 0.12D, 0.08D, 0.12D, 0.02D);
        serverWorld.spawnParticles(ParticleTypes.SMOKE, launchPos.x, launchPos.y, launchPos.z, 10, 0.08D, 0.05D, 0.08D, 0.01D);

        setMainAmmo(getMainAmmo() - 1);
        setMainCooldown(MAIN_COOLDOWN_TICKS);
        if (getMainAmmo() <= 0) {
            setMainReload(MAIN_RELOAD_TICKS);
        }
    }

    private void tryFireSuppressor(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        if (pilot != getControllingPassenger() || getSuppressCooldown() > 0 || getSuppressReload() > 0 || getSuppressAmmo() <= 0) {
            return;
        }
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        Vec3d aim = Vec3d.fromPolar(aimPitch + 5.0F, aimYaw).normalize();
        Vec3d launchPos = new Vec3d(getX(), getY() + 0.78D, getZ()).add(aim.multiply(1.3D));
        RiftSuppressorChargeEntity charge = new RiftSuppressorChargeEntity(serverWorld, pilot);
        charge.refreshPositionAndAngles(launchPos.x, launchPos.y, launchPos.z, aimYaw, aimPitch);
        charge.setVelocity(aim.multiply(1.2D).add(getVelocity().multiply(0.45D)));
        serverWorld.spawnEntity(charge);

        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_SIEGE_SUPPRESS, SoundCategory.HOSTILE, 0.95F, 1.0F);
        serverWorld.spawnParticles(ParticleTypes.PORTAL, launchPos.x, launchPos.y, launchPos.z, 12, 0.12D, 0.06D, 0.12D, 0.08D);

        setSuppressAmmo(getSuppressAmmo() - 1);
        setSuppressCooldown(SUPPRESS_COOLDOWN_TICKS);
        if (getSuppressAmmo() <= 0) {
            setSuppressReload(SUPPRESS_RELOAD_TICKS);
        }
    }

    @Override
    public void rearmVehicle() {
        setMainAmmo(MAX_MAIN_AMMO);
        setMainCooldown(0);
        setMainReload(0);
        setSuppressAmmo(MAX_SUPPRESS_AMMO);
        setSuppressCooldown(0);
        setSuppressReload(0);
    }

    @Override
    public VehicleHudState createHudState(PlayerEntity viewer) {
        return new VehicleHudState(
                Text.translatable("entity.veilreach.rift_siege_platform"),
                getHealthAmount(),
                MAX_HEALTH,
                getVelocity().horizontalLength() * 20.0D,
                (int) Math.round(getY()),
                getThrottle(),
                List.of(
                        new VehicleWeaponMount(Text.translatable("hud.veilreach.rift_siege_platform.cannon"), getMainAmmo(), MAX_MAIN_AMMO, getMainCooldown(), getMainReload(), 0xFFEAC47E),
                        new VehicleWeaponMount(Text.translatable("hud.veilreach.rift_siege_platform.suppress"), getSuppressAmmo(), MAX_SUPPRESS_AMMO, getSuppressCooldown(), getSuppressReload(), 0xFFC7A2FF)
                ),
                Text.translatable("hud.veilreach.vehicle.lock_unavailable"),
                Text.translatable("hud.veilreach.rift_siege_platform.fire_hint"),
                0xFFE7C489,
                0xFFFF8C72,
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
        return Math.min(1.0F, 0.24F + (getThrottle() * 0.5F));
    }

    @Override
    public float getLoopSoundPitch() {
        return 0.78F + (getThrottle() * 0.18F);
    }

    @Override
    public Vec3d getPassengerAnchorOffset(net.minecraft.entity.Entity passenger) {
        return new Vec3d(0.0D, 1.0D + (passenger.getHeight() * 0.02D), -0.3D);
    }

    @Override
    protected SoundEvent getBoardingSound() {
        return SoundEvents.BLOCK_BEACON_POWER_SELECT;
    }

    @Override
    public SoundEvent getLoopingSound() {
        return VeilreachSoundEvents.RIFT_SIEGE_HUM;
    }

    @Override
    protected float getForwardAcceleration() {
        return 0.1F;
    }

    @Override
    protected float getStrafeAcceleration() {
        return 0.08F;
    }

    @Override
    protected float getYawFollowRate() {
        return 8.0F;
    }

    @Override
    protected double getAscentRate() {
        return 0.2D;
    }

    @Override
    protected double getDescentRate() {
        return -0.18D;
    }

    @Override
    protected double getMaxHorizontalSpeed() {
        return 1.3D;
    }

    @Override
    protected double getMaxVerticalSpeed() {
        return 0.44D;
    }

    @Override
    protected float getProjectileDamageMultiplier() {
        return 0.08F;
    }

    @Override
    protected float getExplosionDamageMultiplier() {
        return 0.28F;
    }

    @Override
    protected float getMeleeDamageMultiplier() {
        return 0.18F;
    }

    @Override
    protected float getAmbientDamageMultiplier() {
        return 0.22F;
    }

    @Override
    protected float getMinimumProjectileDamage() {
        return 0.4F;
    }

    @Override
    protected float getMinimumHeavyDamage() {
        return 0.8F;
    }

    @Override
    protected float getCrashDamageThreshold() {
        return 1.15F;
    }

    @Override
    protected float getCrashDamageFactor() {
        return 18.0F;
    }

    @Override
    protected double getHorizontalDrag() {
        return 0.94D;
    }

    @Override
    protected double getVerticalDrag() {
        return 0.91D;
    }

    @Override
    protected double getVerticalResponse() {
        return 0.24D;
    }

    @Override
    protected float getBaseThrottle() {
        return 0.28F;
    }

    @Override
    protected float getForwardThrottleGain() {
        return 0.22F;
    }

    @Override
    protected float getAscendThrottleGain() {
        return 0.12F;
    }

    @Override
    protected float getTurnThrottleGain() {
        return 0.05F;
    }

    @Override
    protected double getHoverLiftBias() {
        return 0.01D;
    }

    @Override
    protected SoundEvent getHitSound() {
        return VeilreachSoundEvents.RIFT_SIEGE_SUPPRESS;
    }

    @Override
    protected SoundEvent getDestroySound() {
        return VeilreachSoundEvents.RIFT_SIEGE_EXPLOSION;
    }

    @Override
    protected float getDestructionPower() {
        return 5.4F;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(VeilreachItems.RIFT_SIEGE_PLATFORM);
    }

    @Override
    protected void writeCustomData(WriteView writeView) {
        writeView.putInt("main_ammo", getMainAmmo());
        writeView.putInt("main_cooldown", getMainCooldown());
        writeView.putInt("main_reload", getMainReload());
        writeView.putInt("suppress_ammo", getSuppressAmmo());
        writeView.putInt("suppress_cooldown", getSuppressCooldown());
        writeView.putInt("suppress_reload", getSuppressReload());
        writeView.putFloat("health", getHealthAmount());
        writeView.putFloat("throttle", getThrottle());
        writeView.putFloat("visual_pitch", getVisualPitch());
        writeView.putFloat("visual_roll", getVisualRoll());
    }

    @Override
    protected void readCustomData(ReadView readView) {
        setMainAmmo(readView.getInt("main_ammo", MAX_MAIN_AMMO));
        setMainCooldown(readView.getInt("main_cooldown", 0));
        setMainReload(readView.getInt("main_reload", 0));
        setSuppressAmmo(readView.getInt("suppress_ammo", MAX_SUPPRESS_AMMO));
        setSuppressCooldown(readView.getInt("suppress_cooldown", 0));
        setSuppressReload(readView.getInt("suppress_reload", 0));
        setHealthAmount(readView.getFloat("health", MAX_HEALTH));
        setThrottle(readView.getFloat("throttle", 0.0F));
        setVisualPitch(readView.getFloat("visual_pitch", 0.0F));
        setVisualRoll(readView.getFloat("visual_roll", 0.0F));
    }

    private int getMainAmmo() {
        return dataTracker.get(MAIN_AMMO);
    }

    private void setMainAmmo(int value) {
        dataTracker.set(MAIN_AMMO, value);
    }

    private int getMainCooldown() {
        return dataTracker.get(MAIN_COOLDOWN);
    }

    private void setMainCooldown(int value) {
        dataTracker.set(MAIN_COOLDOWN, value);
    }

    private int getMainReload() {
        return dataTracker.get(MAIN_RELOAD);
    }

    private void setMainReload(int value) {
        dataTracker.set(MAIN_RELOAD, value);
    }

    private int getSuppressAmmo() {
        return dataTracker.get(SUPPRESS_AMMO);
    }

    private void setSuppressAmmo(int value) {
        dataTracker.set(SUPPRESS_AMMO, value);
    }

    private int getSuppressCooldown() {
        return dataTracker.get(SUPPRESS_COOLDOWN);
    }

    private void setSuppressCooldown(int value) {
        dataTracker.set(SUPPRESS_COOLDOWN, value);
    }

    private int getSuppressReload() {
        return dataTracker.get(SUPPRESS_RELOAD);
    }

    private void setSuppressReload(int value) {
        dataTracker.set(SUPPRESS_RELOAD, value);
    }
}
