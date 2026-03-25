package com.muguett.veilreach.entity;

import com.muguett.veilreach.entity.vehicle.AbstractHoverVehicleEntity;
import com.muguett.veilreach.entity.vehicle.VehicleHudState;
import com.muguett.veilreach.entity.vehicle.VehicleSeatDefinition;
import com.muguett.veilreach.entity.vehicle.VehicleSeatRole;
import com.muguett.veilreach.entity.vehicle.VehicleWeaponMount;
import com.muguett.veilreach.network.LittleBirdActionPayload;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public final class RiftGunshipEntity extends AbstractHoverVehicleEntity {
    private static final TrackedData<Integer> ROCKET_AMMO = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ROCKET_COOLDOWN = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ROCKET_RELOAD = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> COUNTERMEASURE_AMMO = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> COUNTERMEASURE_COOLDOWN = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> COUNTERMEASURE_RELOAD = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> COUNTERMEASURE_ACTIVE = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> GUN_HEAT = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> GUN_COOLDOWN = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> GUN_VENT = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> TURRET_YAW = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> TURRET_PITCH = DataTracker.registerData(RiftGunshipEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public static final float MAX_HEALTH = 420.0F;
    public static final int MAX_ROCKET_AMMO = 6;
    public static final int MAX_COUNTERMEASURE_AMMO = 4;
    public static final int MAX_GUN_HEAT = 500;
    private static final int ROCKET_COOLDOWN_TICKS = 2;
    private static final int ROCKET_RELOAD_TICKS = 88;
    private static final int COUNTERMEASURE_COOLDOWN_TICKS = 34;
    private static final int COUNTERMEASURE_RELOAD_TICKS = 112;
    private static final int COUNTERMEASURE_ACTIVE_TICKS = 44;
    private static final int GUN_HEAT_PER_SHOT = 8;
    private static final int GUN_COOLDOWN_TICKS = 1;
    private static final int GUN_VENT_TICKS = 30;
    private static final List<VehicleSeatDefinition> SEAT_DEFINITIONS = List.of(
            new VehicleSeatDefinition(VehicleSeatRole.PILOT, new Vec3d(0.0D, 1.02D, -0.86D), new Vec3d(1.9D, 0.0D, 0.8D), true),
            new VehicleSeatDefinition(VehicleSeatRole.GUNNER, new Vec3d(0.0D, 0.98D, 0.26D), new Vec3d(-1.9D, 0.0D, 0.8D), false)
    );

    private int rocketRippleRemaining;
    private int rocketLaunchIndex;
    private float rocketAimYaw;
    private float rocketAimPitch;

    public RiftGunshipEntity(EntityType<? extends RiftGunshipEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        initVehicleTrackerBase(builder);
        builder.add(ROCKET_AMMO, MAX_ROCKET_AMMO);
        builder.add(ROCKET_COOLDOWN, 0);
        builder.add(ROCKET_RELOAD, 0);
        builder.add(COUNTERMEASURE_AMMO, MAX_COUNTERMEASURE_AMMO);
        builder.add(COUNTERMEASURE_COOLDOWN, 0);
        builder.add(COUNTERMEASURE_RELOAD, 0);
        builder.add(COUNTERMEASURE_ACTIVE, 0);
        builder.add(GUN_HEAT, 0);
        builder.add(GUN_COOLDOWN, 0);
        builder.add(GUN_VENT, 0);
        builder.add(TURRET_YAW, 0.0F);
        builder.add(TURRET_PITCH, -4.0F);
    }

    @Override
    protected void tickVehicleSystemsServer() {
        if (getRocketCooldown() > 0) {
            setRocketCooldown(getRocketCooldown() - 1);
        }
        if (getCountermeasureCooldown() > 0) {
            setCountermeasureCooldown(getCountermeasureCooldown() - 1);
        }
        if (getGunCooldown() > 0) {
            setGunCooldown(getGunCooldown() - 1);
        }
        if (getCountermeasureActiveTicks() > 0) {
            setCountermeasureActiveTicks(getCountermeasureActiveTicks() - 1);
        }
        if (getRocketReload() > 0) {
            setRocketReload(getRocketReload() - 1);
            if (getRocketReload() == 0) {
                setRocketAmmo(MAX_ROCKET_AMMO);
            }
        }
        if (getCountermeasureReload() > 0) {
            setCountermeasureReload(getCountermeasureReload() - 1);
            if (getCountermeasureReload() == 0) {
                setCountermeasureAmmo(MAX_COUNTERMEASURE_AMMO);
            }
        }
        if (getGunVentTicks() > 0) {
            setGunVentTicks(getGunVentTicks() - 1);
            coolGunHeat(4);
        } else {
            coolGunHeat(getPassengerForRole(VehicleSeatRole.GUNNER) != null ? 2 : 3);
        }

        tickTurretAlignment();

        if (rocketRippleRemaining > 0 && getRocketCooldown() <= 0 && getRocketReload() <= 0) {
            LivingEntity pilot = getControllingPassenger();
            if (pilot instanceof ServerPlayerEntity serverPilot) {
                if (fireRocketShot(serverPilot, rocketAimYaw, rocketAimPitch)) {
                    rocketRippleRemaining--;
                } else {
                    rocketRippleRemaining = 0;
                }
            } else {
                rocketRippleRemaining = 0;
            }
        }
    }

    @Override
    protected void tickVehicleEffectsServer(ServerWorld serverWorld) {
        super.tickVehicleEffectsServer(serverWorld);
        if (getCountermeasureActiveTicks() > 0 && age % 2 == 0) {
            Vec3d aft = Vec3d.fromPolar(0.0F, getYaw()).multiply(-1.35D);
            Vec3d right = new Vec3d(-aft.z, 0.0D, aft.x).normalize();
            Vec3d base = new Vec3d(getX(), getY() + 1.02D, getZ()).add(aft);
            for (int side = -1; side <= 1; side += 2) {
                Vec3d pos = base.add(right.multiply(side * 0.85D));
                serverWorld.spawnParticles(ParticleTypes.GLOW, pos.x, pos.y, pos.z, 5, 0.08D, 0.08D, 0.08D, 0.0D);
                serverWorld.spawnParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 3, 0.06D, 0.06D, 0.06D, 0.02D);
            }
        }
    }

    @Override
    protected void tickVehicleEffectsClient() {
        if (getCountermeasureActiveTicks() > 0 && age % 2 == 0) {
            Vec3d aft = Vec3d.fromPolar(0.0F, getYaw()).multiply(-1.3D);
            Vec3d base = new Vec3d(getX(), getY() + 0.98D, getZ()).add(aft);
            getEntityWorld().addParticleClient(ParticleTypes.GLOW, base.x, base.y, base.z, 0.0D, 0.0D, 0.0D);
            getEntityWorld().addParticleClient(ParticleTypes.END_ROD, base.x, base.y, base.z, 0.0D, 0.01D, 0.0D);
        }
    }

    private void tickTurretAlignment() {
        LivingEntity gunner = getPassengerForRole(VehicleSeatRole.GUNNER);
        if (gunner == null) {
            float yawDelta = MathHelper.wrapDegrees(getYaw() - getTurretYaw());
            setTurretYaw(getTurretYaw() + MathHelper.clamp(yawDelta, -5.0F, 5.0F));
            setTurretPitch(MathHelper.lerp(0.18F, getTurretPitch(), -4.0F));
        }
    }

    private void coolGunHeat(int amount) {
        if (getGunHeat() > 0) {
            setGunHeat(Math.max(0, getGunHeat() - amount));
        }
    }

    @Override
    public void handleVehicleAction(ServerPlayerEntity player, LittleBirdActionPayload payload) {
        VehicleSeatRole seatRole = getSeatRole(player);
        switch (payload.action()) {
            case LittleBirdActionPayload.UPDATE_CONTROLS -> {
                if (seatRole == VehicleSeatRole.PILOT) {
                    applyPilotInputs(payload.forward(), payload.turn(), payload.ascend(), payload.descend(), payload.yaw(), payload.pitch());
                } else if (seatRole == VehicleSeatRole.GUNNER) {
                    updateTurretAim(payload.yaw(), payload.pitch());
                }
            }
            case LittleBirdActionPayload.FIRE_ROCKET -> {
                if (seatRole == VehicleSeatRole.PILOT) {
                    startRocketRipple(player, payload.yaw(), payload.pitch());
                } else if (seatRole == VehicleSeatRole.GUNNER) {
                    fireAutocannon(player, payload.yaw(), payload.pitch());
                }
            }
            case LittleBirdActionPayload.DROP_BOMB -> {
                if (seatRole == VehicleSeatRole.PILOT) {
                    deployCountermeasures(player);
                }
            }
            case LittleBirdActionPayload.SWITCH_SEAT -> {
                if (hasPassenger(player)) {
                    rocketRippleRemaining = 0;
                    switchSeat(player);
                }
            }
            default -> {
            }
        }
    }

    private void startRocketRipple(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        if (pilot != getControllingPassenger() || rocketRippleRemaining > 0 || getRocketCooldown() > 0 || getRocketReload() > 0 || getRocketAmmo() <= 0) {
            return;
        }
        rocketAimYaw = aimYaw;
        rocketAimPitch = aimPitch;
        rocketRippleRemaining = Math.min(MAX_ROCKET_AMMO, getRocketAmmo());
    }

    private boolean fireRocketShot(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        if (!(getEntityWorld() instanceof ServerWorld serverWorld) || getRocketAmmo() <= 0) {
            return false;
        }

        int tubeIndex = rocketLaunchIndex++ % MAX_ROCKET_AMMO;
        int row = tubeIndex % 3;
        double sideOffset = tubeIndex < 3 ? 0.96D : -0.96D;
        double verticalOffset = 0.26D - (row * 0.08D);
        double forwardOffset = 1.26D - (row * 0.06D);
        float yawOffset = tubeIndex < 3 ? -0.6F : 0.6F;
        float pitchOffset = (1 - row) * 0.35F;

        Vec3d aim = Vec3d.fromPolar(aimPitch + pitchOffset, aimYaw + yawOffset).normalize();
        Vec3d right = aim.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
        if (right.lengthSquared() < 1.0E-4D) {
            right = Vec3d.fromPolar(0.0F, getYaw() - 90.0F);
        } else {
            right = right.normalize();
        }

        Vec3d launchPos = new Vec3d(getX(), getY() + 0.96D + verticalOffset, getZ())
                .add(right.multiply(sideOffset))
                .add(aim.multiply(forwardOffset));
        RiftGunshipRocketEntity rocket = new RiftGunshipRocketEntity(serverWorld, pilot);
        rocket.refreshPositionAndAngles(launchPos.x, launchPos.y, launchPos.z, aimYaw, aimPitch);
        rocket.setVelocity(aim.multiply(1.95D).add(getVelocity().multiply(0.72D)));
        serverWorld.spawnEntity(rocket);

        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_GUNSHIP_ROCKET, SoundCategory.HOSTILE, 0.92F, 0.94F + (serverWorld.random.nextFloat() * 0.08F));
        serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, launchPos.x, launchPos.y, launchPos.z, 8, 0.06D, 0.06D, 0.06D, 0.018D);
        serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, launchPos.x, launchPos.y, launchPos.z, 6, 0.04D, 0.04D, 0.04D, 0.005D);

        setRocketAmmo(getRocketAmmo() - 1);
        setRocketCooldown(ROCKET_COOLDOWN_TICKS);
        if (getRocketAmmo() <= 0) {
            setRocketReload(ROCKET_RELOAD_TICKS);
            rocketRippleRemaining = 0;
        }
        return true;
    }

    private void deployCountermeasures(ServerPlayerEntity pilot) {
        if (pilot != getControllingPassenger() || getCountermeasureCooldown() > 0 || getCountermeasureReload() > 0 || getCountermeasureAmmo() <= 0) {
            return;
        }
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        Vec3d aft = Vec3d.fromPolar(0.0F, getYaw()).multiply(-1.35D);
        Vec3d right = new Vec3d(-aft.z, 0.0D, aft.x).normalize();
        Vec3d base = new Vec3d(getX(), getY() + 1.0D, getZ()).add(aft);
        for (int side = -1; side <= 1; side += 2) {
            Vec3d pos = base.add(right.multiply(side * 0.88D));
            serverWorld.spawnParticles(ParticleTypes.GLOW, pos.x, pos.y, pos.z, 10, 0.12D, 0.08D, 0.12D, 0.0D);
            serverWorld.spawnParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 8, 0.08D, 0.08D, 0.08D, 0.03D);
            serverWorld.spawnParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 4, 0.08D, 0.08D, 0.08D, 0.01D);
        }
        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_GUNSHIP_COUNTERMEASURE, SoundCategory.HOSTILE, 0.9F, 1.05F);

        setCountermeasureAmmo(getCountermeasureAmmo() - 1);
        setCountermeasureCooldown(COUNTERMEASURE_COOLDOWN_TICKS);
        setCountermeasureActiveTicks(COUNTERMEASURE_ACTIVE_TICKS);
        if (getCountermeasureAmmo() <= 0) {
            setCountermeasureReload(COUNTERMEASURE_RELOAD_TICKS);
        }
    }

    private void fireAutocannon(ServerPlayerEntity gunner, float aimYaw, float aimPitch) {
        if (getSeatRole(gunner) != VehicleSeatRole.GUNNER || getGunCooldown() > 0 || getGunVentTicks() > 0) {
            return;
        }
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        updateTurretAim(aimYaw, aimPitch);
        if (getGunHeat() + GUN_HEAT_PER_SHOT > MAX_GUN_HEAT) {
            beginGunVent(serverWorld);
            return;
        }

        Vec3d aim = Vec3d.fromPolar(aimPitch, aimYaw).normalize();
        Vec3d muzzle = new Vec3d(getX(), getY() + 0.92D, getZ()).add(aim.multiply(1.72D));
        RiftGunshipRoundEntity round = new RiftGunshipRoundEntity(serverWorld, gunner);
        round.refreshPositionAndAngles(muzzle.x, muzzle.y, muzzle.z, aimYaw, aimPitch);
        round.setVelocity(aim.multiply(3.35D).add(getVelocity().multiply(0.58D)));
        serverWorld.spawnEntity(round);

        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_GUNSHIP_CANNON, SoundCategory.HOSTILE, 0.72F, 1.08F + (serverWorld.random.nextFloat() * 0.06F));
        serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, muzzle.x, muzzle.y, muzzle.z, 5, 0.04D, 0.04D, 0.04D, 0.003D);
        serverWorld.spawnParticles(ParticleTypes.GLOW, muzzle.x, muzzle.y, muzzle.z, 6, 0.05D, 0.05D, 0.05D, 0.0D);

        setGunHeat(Math.min(MAX_GUN_HEAT, getGunHeat() + GUN_HEAT_PER_SHOT));
        setGunCooldown(isCriticalDamage() ? GUN_COOLDOWN_TICKS + 1 : GUN_COOLDOWN_TICKS);
        if (getGunHeat() >= MAX_GUN_HEAT) {
            beginGunVent(serverWorld);
        }
    }

    private void beginGunVent(ServerWorld serverWorld) {
        setGunVentTicks(GUN_VENT_TICKS);
        setGunCooldown(GUN_VENT_TICKS);
        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_GUNSHIP_CANNON, SoundCategory.HOSTILE, 0.55F, 0.62F);
        serverWorld.spawnParticles(ParticleTypes.SMOKE, getX(), getY() + 0.96D, getZ(), 8, 0.18D, 0.08D, 0.18D, 0.015D);
    }

    private void updateTurretAim(float yaw, float pitch) {
        setTurretYaw(MathHelper.wrapDegrees(yaw));
        setTurretPitch(MathHelper.clamp(pitch, -40.0F, 26.0F));
    }

    @Override
    public VehicleHudState createHudState(PlayerEntity viewer) {
        VehicleSeatRole seatRole = getSeatRole(viewer);
        Text seatLabel = Text.translatable(seatRole.translationKey());
        if (seatRole == VehicleSeatRole.GUNNER) {
            return createGunnerHudState(seatLabel);
        }
        return createPilotHudState(seatLabel, viewer);
    }

    private VehicleHudState createPilotHudState(Text seatLabel, PlayerEntity viewer) {
        Text status = getCountermeasureActiveTicks() > 0
                ? Text.translatable("hud.veilreach.rift_gunship.countermeasure_active")
                : getPassengerForRole(VehicleSeatRole.GUNNER) != null
                ? Text.translatable("hud.veilreach.rift_gunship.gunner_online")
                : Text.translatable("hud.veilreach.rift_gunship.gunner_open");
        return new VehicleHudState(
                Text.translatable("entity.veilreach.rift_gunship"),
                seatLabel,
                getHealthAmount(),
                MAX_HEALTH,
                getVelocity().horizontalLength() * 20.0D,
                (int) Math.round(getY()),
                getThrottle(),
                List.of(
                        new VehicleWeaponMount(Text.translatable("hud.veilreach.rift_gunship.rockets"), getRocketAmmo(), MAX_ROCKET_AMMO, getRocketCooldown(), getRocketReload(), 0xFFE1F6FF),
                        new VehicleWeaponMount(Text.translatable("hud.veilreach.rift_gunship.countermeasure"), getCountermeasureAmmo(), MAX_COUNTERMEASURE_AMMO, getCountermeasureCooldown(), getCountermeasureReload(), 0xFFC7F0FF)
                ),
                status,
                Text.translatable("hud.veilreach.rift_gunship.pilot_hint"),
                0xFFD7F0FF,
                0xFFFF8F73,
                false,
                isDamaged(),
                isCriticalDamage(),
                usesFlightHud(viewer)
        );
    }

    private VehicleHudState createGunnerHudState(Text seatLabel) {
        int thermalMargin = Math.max(0, MAX_GUN_HEAT - getGunHeat());
        Text status = getGunVentTicks() > 0
                ? Text.translatable("hud.veilreach.rift_gunship.gun_venting")
                : Text.translatable("hud.veilreach.rift_gunship.gun_heat", getGunHeat(), MAX_GUN_HEAT);
        return new VehicleHudState(
                Text.translatable("entity.veilreach.rift_gunship"),
                seatLabel,
                getHealthAmount(),
                MAX_HEALTH,
                getVelocity().horizontalLength() * 20.0D,
                (int) Math.round(getY()),
                getThrottle(),
                List.of(new VehicleWeaponMount(Text.translatable("hud.veilreach.rift_gunship.autocannon"), thermalMargin, MAX_GUN_HEAT, getGunCooldown(), getGunVentTicks(), 0xFFFFC28B)),
                status,
                Text.translatable("hud.veilreach.rift_gunship.gunner_hint"),
                0xFFFFD3A7,
                0xFFFF8669,
                false,
                isDamaged(),
                isCriticalDamage(),
                false
        );
    }

    @Override
    public void rearmVehicle() {
        setRocketAmmo(MAX_ROCKET_AMMO);
        setRocketCooldown(0);
        setRocketReload(0);
        setCountermeasureAmmo(MAX_COUNTERMEASURE_AMMO);
        setCountermeasureCooldown(0);
        setCountermeasureReload(0);
        setCountermeasureActiveTicks(0);
        setGunHeat(0);
        setGunCooldown(0);
        setGunVentTicks(0);
        rocketRippleRemaining = 0;
    }

    @Override
    public boolean hasActiveCountermeasures() {
        return getCountermeasureActiveTicks() > 0;
    }

    @Override
    protected List<VehicleSeatDefinition> getSeatDefinitions() {
        return SEAT_DEFINITIONS;
    }

    @Override
    public float getMaxHealthAmount() {
        return MAX_HEALTH;
    }

    @Override
    public float getLoopSoundVolume() {
        return Math.min(1.0F, 0.24F + (getThrottle() * 0.58F));
    }

    @Override
    public float getLoopSoundPitch() {
        float pitch = 0.82F + (getThrottle() * 0.28F);
        return isCriticalDamage() ? pitch * 0.92F : pitch;
    }

    @Override
    protected SoundEvent getBoardingSound() {
        return VeilreachSoundEvents.RIFT_GUNSHIP_STARTUP;
    }

    @Override
    public SoundEvent getLoopingSound() {
        return VeilreachSoundEvents.RIFT_GUNSHIP_LOOP;
    }

    @Override
    protected float getForwardAcceleration() {
        return isCriticalDamage() ? 0.1F : 0.125F;
    }

    @Override
    protected float getStrafeAcceleration() {
        return isCriticalDamage() ? 0.08F : 0.11F;
    }

    @Override
    protected float getYawFollowRate() {
        return isCriticalDamage() ? 8.0F : 10.5F;
    }

    @Override
    protected double getAscentRate() {
        return isCriticalDamage() ? 0.22D : 0.28D;
    }

    @Override
    protected double getDescentRate() {
        return -0.24D;
    }

    @Override
    protected double getMaxHorizontalSpeed() {
        return isCriticalDamage() ? 1.35D : 1.7D;
    }

    @Override
    protected double getMaxVerticalSpeed() {
        return isCriticalDamage() ? 0.42D : 0.58D;
    }

    @Override
    protected float getProjectileDamageMultiplier() {
        return 0.08F;
    }

    @Override
    protected float getExplosionDamageMultiplier() {
        return 0.3F;
    }

    @Override
    protected float getMeleeDamageMultiplier() {
        return 0.22F;
    }

    @Override
    protected float getAmbientDamageMultiplier() {
        return 0.24F;
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
        return 1.05F;
    }

    @Override
    protected float getCrashDamageFactor() {
        return 14.5F;
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
        return 0.26D;
    }

    @Override
    protected float getPitchSpeedFactor() {
        return 14.0F;
    }

    @Override
    protected float getRollResponseFactor() {
        return 18.0F;
    }

    @Override
    protected float getBaseThrottle() {
        return 0.34F;
    }

    @Override
    protected float getForwardThrottleGain() {
        return 0.26F;
    }

    @Override
    protected float getAscendThrottleGain() {
        return 0.14F;
    }

    @Override
    protected float getTurnThrottleGain() {
        return 0.06F;
    }

    @Override
    protected double getHoverLiftBias() {
        return 0.02D;
    }

    @Override
    protected SoundEvent getHitSound() {
        return VeilreachSoundEvents.RIFT_GUNSHIP_HIT;
    }

    @Override
    protected SoundEvent getDestroySound() {
        return VeilreachSoundEvents.RIFT_GUNSHIP_EXPLOSION;
    }

    @Override
    protected float getDestructionPower() {
        return 5.2F;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(VeilreachItems.RIFT_GUNSHIP);
    }

    @Override
    protected void writeCustomData(WriteView writeView) {
        writeView.putInt("rocket_ammo", getRocketAmmo());
        writeView.putInt("rocket_cooldown", getRocketCooldown());
        writeView.putInt("rocket_reload", getRocketReload());
        writeView.putInt("countermeasure_ammo", getCountermeasureAmmo());
        writeView.putInt("countermeasure_cooldown", getCountermeasureCooldown());
        writeView.putInt("countermeasure_reload", getCountermeasureReload());
        writeView.putInt("countermeasure_active", getCountermeasureActiveTicks());
        writeView.putInt("gun_heat", getGunHeat());
        writeView.putInt("gun_cooldown", getGunCooldown());
        writeView.putInt("gun_vent", getGunVentTicks());
        writeView.putFloat("turret_yaw", getTurretYaw());
        writeView.putFloat("turret_pitch", getTurretPitch());
        writeView.putFloat("health", getHealthAmount());
        writeView.putFloat("throttle", getThrottle());
        writeView.putFloat("visual_pitch", getVisualPitch());
        writeView.putFloat("visual_roll", getVisualRoll());
        writeView.putInt("rocket_launch_index", rocketLaunchIndex);
    }

    @Override
    protected void readCustomData(ReadView readView) {
        setRocketAmmo(readView.getInt("rocket_ammo", MAX_ROCKET_AMMO));
        setRocketCooldown(readView.getInt("rocket_cooldown", 0));
        setRocketReload(readView.getInt("rocket_reload", 0));
        setCountermeasureAmmo(readView.getInt("countermeasure_ammo", MAX_COUNTERMEASURE_AMMO));
        setCountermeasureCooldown(readView.getInt("countermeasure_cooldown", 0));
        setCountermeasureReload(readView.getInt("countermeasure_reload", 0));
        setCountermeasureActiveTicks(readView.getInt("countermeasure_active", 0));
        setGunHeat(readView.getInt("gun_heat", 0));
        setGunCooldown(readView.getInt("gun_cooldown", 0));
        setGunVentTicks(readView.getInt("gun_vent", 0));
        setTurretYaw(readView.getFloat("turret_yaw", 0.0F));
        setTurretPitch(readView.getFloat("turret_pitch", -4.0F));
        setHealthAmount(readView.getFloat("health", MAX_HEALTH));
        setThrottle(readView.getFloat("throttle", 0.0F));
        setVisualPitch(readView.getFloat("visual_pitch", 0.0F));
        setVisualRoll(readView.getFloat("visual_roll", 0.0F));
        rocketLaunchIndex = readView.getInt("rocket_launch_index", 0);
    }

    public float getTurretYaw() {
        return getDataTracker().get(TURRET_YAW);
    }

    private void setTurretYaw(float value) {
        getDataTracker().set(TURRET_YAW, value);
    }

    public float getTurretPitch() {
        return getDataTracker().get(TURRET_PITCH);
    }

    private void setTurretPitch(float value) {
        getDataTracker().set(TURRET_PITCH, value);
    }

    public int getRocketAmmo() {
        return getDataTracker().get(ROCKET_AMMO);
    }

    private void setRocketAmmo(int value) {
        getDataTracker().set(ROCKET_AMMO, value);
    }

    public int getRocketCooldown() {
        return getDataTracker().get(ROCKET_COOLDOWN);
    }

    private void setRocketCooldown(int value) {
        getDataTracker().set(ROCKET_COOLDOWN, value);
    }

    public int getRocketReload() {
        return getDataTracker().get(ROCKET_RELOAD);
    }

    private void setRocketReload(int value) {
        getDataTracker().set(ROCKET_RELOAD, value);
    }

    public int getCountermeasureAmmo() {
        return getDataTracker().get(COUNTERMEASURE_AMMO);
    }

    private void setCountermeasureAmmo(int value) {
        getDataTracker().set(COUNTERMEASURE_AMMO, value);
    }

    public int getCountermeasureCooldown() {
        return getDataTracker().get(COUNTERMEASURE_COOLDOWN);
    }

    private void setCountermeasureCooldown(int value) {
        getDataTracker().set(COUNTERMEASURE_COOLDOWN, value);
    }

    public int getCountermeasureReload() {
        return getDataTracker().get(COUNTERMEASURE_RELOAD);
    }

    private void setCountermeasureReload(int value) {
        getDataTracker().set(COUNTERMEASURE_RELOAD, value);
    }

    public int getCountermeasureActiveTicks() {
        return getDataTracker().get(COUNTERMEASURE_ACTIVE);
    }

    private void setCountermeasureActiveTicks(int value) {
        getDataTracker().set(COUNTERMEASURE_ACTIVE, value);
    }

    public int getGunHeat() {
        return getDataTracker().get(GUN_HEAT);
    }

    private void setGunHeat(int value) {
        getDataTracker().set(GUN_HEAT, value);
    }

    public int getGunCooldown() {
        return getDataTracker().get(GUN_COOLDOWN);
    }

    private void setGunCooldown(int value) {
        getDataTracker().set(GUN_COOLDOWN, value);
    }

    public int getGunVentTicks() {
        return getDataTracker().get(GUN_VENT);
    }

    private void setGunVentTicks(int value) {
        getDataTracker().set(GUN_VENT, value);
    }
}
