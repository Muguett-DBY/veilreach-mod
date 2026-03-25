package com.muguett.veilreach.entity;

import com.muguett.veilreach.entity.vehicle.AbstractArmedVehicleEntity;
import com.muguett.veilreach.entity.vehicle.VehicleHudState;
import com.muguett.veilreach.entity.vehicle.VehicleSeatDefinition;
import com.muguett.veilreach.entity.vehicle.VehicleSeatRole;
import com.muguett.veilreach.entity.vehicle.VehicleWeaponMount;
import com.muguett.veilreach.network.LittleBirdActionPayload;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext;

import java.util.List;

public final class LittleBirdEntity extends AbstractArmedVehicleEntity {
    private static final TrackedData<Integer> ROCKET_AMMO = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ROCKET_COOLDOWN = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ROCKET_RELOAD = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> BOMB_AMMO = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> BOMB_COOLDOWN = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> BOMB_RELOAD = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TRACKING_MISSILE_AMMO = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TRACKING_MISSILE_COOLDOWN = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TRACKING_MISSILE_RELOAD = DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @SuppressWarnings("unchecked")
    private static final TrackedData<Integer>[] LOCKED_TARGET_IDS = new TrackedData[] {
            DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(LittleBirdEntity.class, TrackedDataHandlerRegistry.INTEGER)
    };
    public static final float MAX_HEALTH = 320.0F;
    public static final int MAX_ROCKET_AMMO = 12;
    public static final int MAX_BOMB_AMMO = 3;
    public static final int MAX_TRACKING_MISSILES = 8;
    private static final int ROCKET_COOLDOWN_TICKS = 1;
    private static final int ROCKET_RELOAD_TICKS = 112;
    private static final int BOMB_COOLDOWN_TICKS = 22;
    private static final int BOMB_RELOAD_TICKS = 150;
    private static final int TRACKING_MISSILE_COOLDOWN_TICKS = 1;
    private static final int TRACKING_MISSILE_RELOAD_TICKS = 120;
    private static final float FORWARD_ACCELERATION = 0.135F;
    private static final float STRAFE_ACCELERATION = 0.11F;
    private static final float YAW_FOLLOW_RATE = 10.5F;
    private static final double ASCENT_RATE = 0.4D;
    private static final double DESCENT_RATE = -0.32D;
    private static final double HORIZONTAL_DRAG = 0.96D;
    private static final double VERTICAL_DRAG = 0.94D;
    private static final double MAX_HORIZONTAL_SPEED = 1.9D;
    private static final double MAX_VERTICAL_SPEED = 0.82D;
    private static final float CRASH_DAMAGE_THRESHOLD = 1.05F;
    private static final int CONTROL_TIMEOUT_TICKS = 8;
    private static final float PROJECTILE_DAMAGE_MULTIPLIER = 0.06F;
    private static final float EXPLOSION_DAMAGE_MULTIPLIER = 0.34F;
    private static final float MELEE_DAMAGE_MULTIPLIER = 0.18F;
    private static final float AMBIENT_DAMAGE_MULTIPLIER = 0.24F;
    private static final float MIN_PROJECTILE_DAMAGE = 0.35F;
    private static final float MIN_HEAVY_DAMAGE = 0.9F;
    private static final double LOCK_RANGE = 112.0D;
    private static final double LOCK_KEEP_RANGE = 148.0D;
    private static final double LOCK_MIN_ALIGNMENT = 0.84D;
    private static final List<VehicleSeatDefinition> SEAT_DEFINITIONS = List.of(
            new VehicleSeatDefinition(VehicleSeatRole.PILOT, new Vec3d(0.0D, 0.98D, -1.18D), new Vec3d(1.8D, 0.0D, 0.7D), true)
    );

    private float lastRotorAngle;
    private float rotorAngle;
    private float lastTailRotorAngle;
    private float tailRotorAngle;
    private int launchSide;
    private int pendingRocketSalvo;
    private float rocketSalvoYaw;
    private float rocketSalvoPitch;
    private final int[] pendingTrackingVolleyIds = new int[LOCKED_TARGET_IDS.length];
    private int pendingTrackingVolleySize;
    private int pendingTrackingVolleyIndex;

    public LittleBirdEntity(EntityType<? extends LittleBirdEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        initVehicleTrackerBase(builder);
        builder.add(ROCKET_AMMO, MAX_ROCKET_AMMO);
        builder.add(ROCKET_COOLDOWN, 0);
        builder.add(ROCKET_RELOAD, 0);
        builder.add(BOMB_AMMO, MAX_BOMB_AMMO);
        builder.add(BOMB_COOLDOWN, 0);
        builder.add(BOMB_RELOAD, 0);
        builder.add(TRACKING_MISSILE_AMMO, MAX_TRACKING_MISSILES);
        builder.add(TRACKING_MISSILE_COOLDOWN, 0);
        builder.add(TRACKING_MISSILE_RELOAD, 0);
        for (TrackedData<Integer> trackedData : LOCKED_TARGET_IDS) {
            builder.add(trackedData, -1);
        }
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);

        lastRotorAngle = rotorAngle;
        lastTailRotorAngle = tailRotorAngle;
        tickRotorAngles();

        tickPilotInputWindow();

        if (!getEntityWorld().isClient()) {
            syncSeatAssignmentsServer();
            tickWeaponState();
        }

        if (isLogicalSideForUpdatingMovement()) {
            if (getControllingPassenger() instanceof LivingEntity pilot) {
                tickPilotedMovement(pilot);
            } else {
                tickIdleDrift();
            }

            Vec3d velocity = clampVelocity(getVelocity());
            setVelocity(velocity);
            move(MovementType.SELF, velocity);
            setVelocity(getVelocity().multiply(HORIZONTAL_DRAG, VERTICAL_DRAG, HORIZONTAL_DRAG));
            updateTiltState();

            if (!getEntityWorld().isClient()) {
                tickCollisionDamage();
                tickDamageSmoke();
            }
        }

        if (getEntityWorld().isClient()) {
            tickRotorWashClient();
        }

        tickDamageFlash();
        for (Entity passenger : getPassengerList()) {
            updatePassengerPosition(passenger);
        }
    }

    private void tickRotorAngles() {
        float throttle = getThrottle();
        float rotorSpeed = 18.0F + (throttle * 46.0F);
        rotorAngle = (rotorAngle + rotorSpeed) % 360.0F;
        tailRotorAngle = (tailRotorAngle + (rotorSpeed * 1.85F)) % 360.0F;
    }

    private void tickWeaponState() {
        tickTargetLock();

        if (getRocketCooldown() > 0) {
            setRocketCooldown(getRocketCooldown() - 1);
        }
        if (getBombCooldown() > 0) {
            setBombCooldown(getBombCooldown() - 1);
        }
        if (getTrackingMissileCooldown() > 0) {
            setTrackingMissileCooldown(getTrackingMissileCooldown() - 1);
        }

        if (getRocketReload() > 0) {
            setRocketReload(getRocketReload() - 1);
            if (getRocketReload() == 0) {
                setRocketAmmo(MAX_ROCKET_AMMO);
            }
        }

        if (getBombReload() > 0) {
            setBombReload(getBombReload() - 1);
            if (getBombReload() == 0) {
                setBombAmmo(MAX_BOMB_AMMO);
            }
        }
        if (getTrackingMissileReload() > 0) {
            setTrackingMissileReload(getTrackingMissileReload() - 1);
            if (getTrackingMissileReload() == 0) {
                setTrackingMissileAmmo(MAX_TRACKING_MISSILES);
            }
        }

        if (pendingRocketSalvo > 0 && getRocketCooldown() <= 0 && getRocketReload() <= 0) {
            if (getControllingPassenger() instanceof ServerPlayerEntity pilot) {
                if (fireRocketBurstShot(pilot, rocketSalvoYaw, rocketSalvoPitch)) {
                    pendingRocketSalvo--;
                } else {
                    pendingRocketSalvo = 0;
                }
            } else {
                pendingRocketSalvo = 0;
            }
        }

        if (pendingTrackingVolleyIndex < pendingTrackingVolleySize && getTrackingMissileCooldown() <= 0 && getTrackingMissileReload() <= 0) {
            if (getControllingPassenger() instanceof ServerPlayerEntity pilot) {
                fireNextTrackingVolleyShot(pilot);
            } else {
                pendingTrackingVolleyIndex = 0;
                pendingTrackingVolleySize = 0;
            }
        }
    }

    private void tickTargetLock() {
        boolean changed = false;
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length; slot++) {
            Entity lockedTarget = getLockedTarget(slot);
            if (lockedTarget == null || !lockedTarget.isAlive() || squaredDistanceTo(lockedTarget) > (LOCK_KEEP_RANGE * LOCK_KEEP_RANGE)) {
                setLockedTargetId(slot, -1);
                changed = true;
            }
        }
        if (changed) {
            compactLockedTargets();
        }
    }

    private void tickPilotedMovement(LivingEntity pilot) {
        float forwardInput;
        float backwardInput;
        float turnInput;
        boolean ascend;
        boolean descend;
        float targetYaw;

        if (pilotControlTicks > 0) {
            forwardInput = Math.max(0.0F, pilotForwardInput);
            backwardInput = Math.max(0.0F, -pilotForwardInput);
            turnInput = pilotTurnInput;
            ascend = pilotAscendInput;
            descend = pilotDescendInput;
            targetYaw = pilotAimYaw;
        } else {
            forwardInput = Math.max(0.0F, pilot.forwardSpeed);
            backwardInput = Math.max(0.0F, -pilot.forwardSpeed);
            turnInput = -pilot.sidewaysSpeed;
            ascend = pilot.isJumping();
            descend = pilot.isSneaking();
            targetYaw = pilot instanceof PlayerEntity player ? player.getYaw() : pilot.getYaw();
        }

        float yawDelta = MathHelper.wrapDegrees(targetYaw - getYaw());
        setYaw(getYaw() + MathHelper.clamp(yawDelta, -YAW_FOLLOW_RATE, YAW_FOLLOW_RATE));

        Vec3d velocity = getVelocity();
        Vec3d forwardVector = Vec3d.fromPolar(0.0F, getYaw());
        Vec3d rightVector = new Vec3d(-forwardVector.z, 0.0D, forwardVector.x);
        velocity = velocity
                .add(forwardVector.multiply(FORWARD_ACCELERATION * (forwardInput - (backwardInput * 0.55F))))
                .add(rightVector.multiply(STRAFE_ACCELERATION * turnInput));

        double targetVertical = 0.0D;
        if (ascend) {
            targetVertical = ASCENT_RATE;
        } else if (descend) {
            targetVertical = DESCENT_RATE;
        }

        double newVertical = MathHelper.lerp(0.34D, velocity.y, targetVertical);
        if (!ascend && !descend && Math.abs(newVertical) < 0.008D) {
            newVertical = 0.0D;
        }
        setVelocity(velocity.x, newVertical, velocity.z);

        float desiredThrottle = 0.38F + (forwardInput * 0.42F) + (ascend ? 0.24F : 0.0F) + (descend ? 0.1F : 0.0F) + (Math.abs(turnInput) * 0.08F);
        setThrottle(MathHelper.lerp(0.18F, getThrottle(), MathHelper.clamp(desiredThrottle, 0.22F, 1.0F)));
    }

    private void tickIdleDrift() {
        setThrottle(MathHelper.lerp(0.18F, getThrottle(), 0.18F));
        if (Math.abs(getVelocity().y) < 0.01D) {
            setVelocity(getVelocity().x, 0.0D, getVelocity().z);
        }
    }

    private void tickRotorWashClient() {
    }

    private Vec3d clampVelocity(Vec3d velocity) {
        double horizontal = velocity.horizontalLength();
        if (horizontal > MAX_HORIZONTAL_SPEED) {
            double scale = MAX_HORIZONTAL_SPEED / horizontal;
            velocity = new Vec3d(velocity.x * scale, velocity.y, velocity.z * scale);
        }
        double clampedY = MathHelper.clamp(velocity.y, -MAX_VERTICAL_SPEED, MAX_VERTICAL_SPEED);
        return new Vec3d(velocity.x, clampedY, velocity.z);
    }

    private void updateTiltState() {
        double horizontalSpeed = getVelocity().horizontalLength();
        float pitchTarget = MathHelper.clamp((float) (-(horizontalSpeed * 22.0D) - (getVelocity().y * 12.0D)), -18.0F, 16.0F);
        float rollTarget = MathHelper.clamp((float) (pilotTurnInput * -24.0F), -28.0F, 28.0F);
        setVisualPitch(MathHelper.lerp(0.2F, getVisualPitch(), pitchTarget));
        setVisualRoll(MathHelper.lerp(0.26F, getVisualRoll(), rollTarget));
    }

    private void tickCollisionDamage() {
        float impactStrength = (float) getVelocity().length();
        if ((horizontalCollision || verticalCollision) && impactStrength > CRASH_DAMAGE_THRESHOLD && getEntityWorld() instanceof ServerWorld serverWorld) {
            float damage = impactStrength * 18.0F;
            damage(serverWorld, getDamageSources().flyIntoWall(), damage);
        }
    }

    private void tickDamageSmoke() {
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }
        Vec3d smokeOrigin = new Vec3d(getX(), getY(), getZ()).add(Vec3d.fromPolar(0.0F, getYaw()).multiply(-1.35D)).add(0.0D, 1.12D, 0.0D);
        if (isCriticalDamage() && age % 5 == 0) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, smokeOrigin.x, smokeOrigin.y, smokeOrigin.z, 4, 0.16D, 0.1D, 0.16D, 0.01D);
            serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, smokeOrigin.x, smokeOrigin.y, smokeOrigin.z, 2, 0.04D, 0.03D, 0.04D, 0.003D);
        } else if (isDamaged() && age % 6 == 0) {
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK, smokeOrigin.x, smokeOrigin.y, smokeOrigin.z, 3, 0.14D, 0.08D, 0.14D, 0.01D);
        }
    }

    public void tryFireRocket(ServerPlayerEntity pilot) {
        tryFireRocket(pilot, pilot.getYaw(), pilot.getPitch());
    }

    public void tryFireRocket(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        if (pendingRocketSalvo > 0 || getRocketCooldown() > 0 || getRocketReload() > 0 || getRocketAmmo() <= 0 || pilot != getControllingPassenger()) {
            return;
        }
        rocketSalvoYaw = aimYaw;
        rocketSalvoPitch = aimPitch;
        pendingRocketSalvo = Math.min(MAX_ROCKET_AMMO, getRocketAmmo());
    }

    private boolean fireRocketBurstShot(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        if (getRocketAmmo() <= 0 || !(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return false;
        }

        int tubeIndex = launchSide++ % MAX_ROCKET_AMMO;
        int rackIndex = tubeIndex / 2;
        float yawOffset = ((tubeIndex & 1) == 0 ? -1.3F : 1.3F) + ((rackIndex - 2.5F) * 0.22F);
        float pitchOffset = (2.5F - rackIndex) * 0.12F;
        Vec3d aim = Vec3d.fromPolar(aimPitch + pitchOffset, aimYaw + yawOffset).normalize();
        Vec3d right = aim.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
        if (right.lengthSquared() < 1.0E-4D) {
            right = Vec3d.fromPolar(0.0F, getYaw() - 90.0F);
        } else {
            right = right.normalize();
        }

        double sideOffset = (tubeIndex & 1) == 0 ? 0.95D : -0.95D;
        double verticalOffset = 0.18D - (rackIndex * 0.06D);
        double forwardOffset = 1.28D - (rackIndex * 0.03D);
        Vec3d launchPos = new Vec3d(getX(), getY(), getZ())
                .add(0.0D, 0.82D + verticalOffset, 0.0D)
                .add(right.multiply(sideOffset))
                .add(aim.multiply(forwardOffset));
        LittleBirdRocketEntity rocket = new LittleBirdRocketEntity(serverWorld, pilot);
        rocket.refreshPositionAndAngles(launchPos.x, launchPos.y, launchPos.z, pilot.getYaw(), pilot.getPitch());
        rocket.setVelocity(aim.multiply(2.1D).add(getVelocity().multiply(0.65D)));
        serverWorld.spawnEntity(rocket);

        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.LITTLE_BIRD_ROCKET_LAUNCH, SoundCategory.HOSTILE, 0.98F, 0.94F + (serverWorld.random.nextFloat() * 0.08F));
        serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, launchPos.x, launchPos.y, launchPos.z, 10, 0.08D, 0.08D, 0.08D, 0.024D);
        serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, launchPos.x, launchPos.y, launchPos.z, 7, 0.04D, 0.04D, 0.04D, 0.005D);
        serverWorld.spawnParticles(ParticleTypes.CLOUD, launchPos.x, launchPos.y, launchPos.z, 4, 0.04D, 0.04D, 0.04D, 0.008D);

        setRocketAmmo(getRocketAmmo() - 1);
        setRocketCooldown(ROCKET_COOLDOWN_TICKS);
        if (getRocketAmmo() <= 0) {
            setRocketReload(ROCKET_RELOAD_TICKS);
            pendingRocketSalvo = 0;
        }
        return true;
    }

    public void tryDropBomb(ServerPlayerEntity pilot) {
        if (getBombCooldown() > 0 || getBombReload() > 0 || getBombAmmo() <= 0 || pilot != getControllingPassenger()) {
            return;
        }
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        LittleBirdBombEntity bomb = new LittleBirdBombEntity(serverWorld, pilot, 0, 0.0F);
        Vec3d dropPos = new Vec3d(getX(), getY(), getZ()).add(0.0D, 0.15D, 0.0D);
        bomb.refreshPositionAndAngles(dropPos.x, dropPos.y, dropPos.z, getYaw(), 0.0F);
        bomb.setVelocity(getVelocity().multiply(0.72D).add(0.0D, -0.18D, 0.0D));
        serverWorld.spawnEntity(bomb);

        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.LITTLE_BIRD_BOMB_DROP, SoundCategory.HOSTILE, 0.9F, 0.9F + (serverWorld.random.nextFloat() * 0.1F));
        serverWorld.spawnParticles(ParticleTypes.SMOKE, dropPos.x, dropPos.y, dropPos.z, 6, 0.08D, 0.03D, 0.08D, 0.015D);

        setBombAmmo(getBombAmmo() - 1);
        setBombCooldown(BOMB_COOLDOWN_TICKS);
        if (getBombAmmo() <= 0) {
            setBombReload(BOMB_RELOAD_TICKS);
        }
    }

    public void tryToggleTrackingLock(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        if (pilot != getControllingPassenger()) {
            return;
        }

        Entity candidate = findLockCandidate(pilot, aimYaw, aimPitch);
        if (candidate == null) {
            pilot.getEntityWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), SoundCategory.PLAYERS, 0.55F, 0.75F);
            return;
        }

        if (findLockedTargetSlot(candidate) >= 0) {
            pilot.getEntityWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.PLAYERS, 0.35F, 1.0F);
            return;
        }

        if (getLockedTargetCount() >= LOCKED_TARGET_IDS.length) {
            pilot.getEntityWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), SoundCategory.PLAYERS, 0.6F, 0.65F);
            return;
        }

        addLockedTarget(candidate);
        pilot.getEntityWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.PLAYERS, 0.8F, 1.55F);
    }

    public void tryFireTrackingVolley(ServerPlayerEntity pilot) {
        if (pilot != getControllingPassenger() || getTrackingMissileCooldown() > 0 || getTrackingMissileReload() > 0 || getTrackingMissileAmmo() <= 0) {
            return;
        }

        int lockedCount = copyLockedTargetsToVolley();
        if (lockedCount <= 0) {
            pilot.getEntityWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), SoundCategory.PLAYERS, 0.55F, 0.75F);
            return;
        }

        clearLockedTargets();
        fireNextTrackingVolleyShot(pilot);
    }

    public void clearTrackingLocks(ServerPlayerEntity pilot) {
        if (pilot != getControllingPassenger()) {
            return;
        }
        if (getLockedTargetCount() <= 0) {
            return;
        }
        clearLockedTargets();
        pilot.getEntityWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.PLAYERS, 0.45F, 0.55F);
    }

    private int copyLockedTargetsToVolley() {
        pendingTrackingVolleyIndex = 0;
        pendingTrackingVolleySize = 0;
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length && pendingTrackingVolleySize < getTrackingMissileAmmo(); slot++) {
            int targetId = getLockedTargetId(slot);
            if (targetId >= 0) {
                pendingTrackingVolleyIds[pendingTrackingVolleySize++] = targetId;
            }
        }
        return pendingTrackingVolleySize;
    }

    private void fireNextTrackingVolleyShot(ServerPlayerEntity pilot) {
        while (pendingTrackingVolleyIndex < pendingTrackingVolleySize) {
            Entity target = getEntityWorld().getEntityById(pendingTrackingVolleyIds[pendingTrackingVolleyIndex++]);
            if (target == null || !target.isAlive()) {
                continue;
            }
            if (fireTrackingMissileShot(pilot, target)) {
                return;
            }
            break;
        }
        if (pendingTrackingVolleyIndex >= pendingTrackingVolleySize) {
            pendingTrackingVolleyIndex = 0;
            pendingTrackingVolleySize = 0;
        }
    }

    private boolean fireTrackingMissileShot(ServerPlayerEntity pilot, Entity target) {
        if (getTrackingMissileCooldown() > 0 || getTrackingMissileReload() > 0 || getTrackingMissileAmmo() <= 0 || !(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return false;
        }

        Vec3d targetPos = new Vec3d(target.getX(), target.getY() + (target.getHeight() * 0.4D), target.getZ());
        Vec3d craftPos = new Vec3d(getX(), getY() + 0.88D, getZ());
        Vec3d aim = targetPos.subtract(craftPos).normalize();
        Vec3d right = aim.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
        if (right.lengthSquared() < 1.0E-4D) {
            right = Vec3d.fromPolar(0.0F, getYaw() - 90.0F);
        } else {
            right = right.normalize();
        }

        double sideOffset = ((launchSide++ & 1) == 0) ? 1.08D : -1.08D;
        Vec3d launchPos = craftPos.add(right.multiply(sideOffset)).add(aim.multiply(1.18D));
        LittleBirdTrackingMissileEntity missile = new LittleBirdTrackingMissileEntity(serverWorld, pilot, target);
        float missileYaw = (float) Math.toDegrees(Math.atan2(aim.x, aim.z));
        float missilePitch = (float) -Math.toDegrees(Math.atan2(aim.y, aim.horizontalLength()));
        missile.refreshPositionAndAngles(launchPos.x, launchPos.y, launchPos.z, missileYaw, missilePitch);
        missile.setVelocity(aim.multiply(1.85D).add(getVelocity().multiply(0.78D)));
        serverWorld.spawnEntity(missile);

        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.LITTLE_BIRD_ROCKET_LAUNCH, SoundCategory.HOSTILE, 1.0F, 0.78F);
        serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, launchPos.x, launchPos.y, launchPos.z, 8, 0.06D, 0.05D, 0.06D, 0.018D);
        serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, launchPos.x, launchPos.y, launchPos.z, 9, 0.04D, 0.04D, 0.04D, 0.005D);
        serverWorld.spawnParticles(ParticleTypes.GLOW, launchPos.x, launchPos.y, launchPos.z, 4, 0.03D, 0.03D, 0.03D, 0.0D);

        setTrackingMissileAmmo(getTrackingMissileAmmo() - 1);
        setTrackingMissileCooldown(TRACKING_MISSILE_COOLDOWN_TICKS);
        if (getTrackingMissileAmmo() <= 0) {
            setTrackingMissileReload(TRACKING_MISSILE_RELOAD_TICKS);
            pendingTrackingVolleyIndex = 0;
            pendingTrackingVolleySize = 0;
        }
        return true;
    }

    private Entity findLockCandidate(ServerPlayerEntity pilot, float aimYaw, float aimPitch) {
        Vec3d origin = pilot.getEyePos();
        Vec3d look = Vec3d.fromPolar(aimPitch, aimYaw).normalize();
        Vec3d end = origin.add(look.multiply(LOCK_RANGE));
        Box searchBox = getBoundingBox().stretch(look.multiply(LOCK_RANGE)).expand(2.0D);
        Entity bestTarget = null;
        double bestDistance = LOCK_RANGE * LOCK_RANGE;
        for (Entity candidate : getEntityWorld().getOtherEntities(this, searchBox, this::isValidLockTarget)) {
            java.util.Optional<Vec3d> hit = candidate.getBoundingBox().expand(0.18D).raycast(origin, end);
            if (hit.isEmpty()) {
                continue;
            }
            Vec3d candidatePos = new Vec3d(candidate.getX(), candidate.getY() + (candidate.getHeight() * 0.45D), candidate.getZ());
            double distance = origin.squaredDistanceTo(hit.get());
            if (distance < 36.0D || distance > bestDistance) {
                continue;
            }
            if (!hasLineOfSight(origin, candidatePos)) {
                continue;
            }
            double alignment = candidatePos.subtract(origin).normalize().dotProduct(look);
            if (alignment < LOCK_MIN_ALIGNMENT) {
                continue;
            }
            if (distance < bestDistance) {
                bestDistance = distance;
                bestTarget = candidate;
            }
        }

        return bestTarget;
    }

    private boolean isValidLockTarget(Entity candidate) {
        if (candidate == this || candidate == getControllingPassenger() || hasPassenger(candidate)) {
            return false;
        }
        if (!candidate.isAlive() || !candidate.canHit()) {
            return false;
        }
        return candidate instanceof LivingEntity || candidate instanceof LittleBirdEntity;
    }

    private boolean hasLineOfSight(Vec3d origin, Vec3d targetPos) {
        BlockHitResult hitResult = getEntityWorld().raycast(new RaycastContext(origin, targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (hitResult.getType() == HitResult.Type.MISS) {
            return true;
        }
        double hitDistance = origin.squaredDistanceTo(hitResult.getPos());
        double targetDistance = origin.squaredDistanceTo(targetPos);
        return hitDistance + 0.25D >= targetDistance;
    }

    public Entity getLockedTarget() {
        return getLockedTarget(0);
    }

    public java.util.List<Entity> getLockedTargets() {
        java.util.List<Entity> targets = new java.util.ArrayList<>();
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length; slot++) {
            Entity target = getLockedTarget(slot);
            if (target != null && target.isAlive()) {
                targets.add(target);
            }
        }
        return targets;
    }

    public int getLockedTargetCount() {
        int count = 0;
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length; slot++) {
            if (getLockedTargetId(slot) >= 0) {
                count++;
            }
        }
        return count;
    }

    private Entity getLockedTarget(int slot) {
        int id = getLockedTargetId(slot);
        return id >= 0 ? getEntityWorld().getEntityById(id) : null;
    }

    private boolean addLockedTarget(Entity target) {
        if (findLockedTargetSlot(target) >= 0) {
            return false;
        }
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length; slot++) {
            if (getLockedTargetId(slot) < 0) {
                setLockedTargetId(slot, target.getId());
                return true;
            }
        }
        return false;
    }

    private boolean removeLockedTarget(Entity target) {
        int slot = findLockedTargetSlot(target);
        if (slot < 0) {
            return false;
        }
        setLockedTargetId(slot, -1);
        compactLockedTargets();
        return true;
    }

    private int findLockedTargetSlot(Entity target) {
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length; slot++) {
            if (getLockedTargetId(slot) == target.getId()) {
                return slot;
            }
        }
        return -1;
    }

    private void clearLockedTargets() {
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length; slot++) {
            setLockedTargetId(slot, -1);
        }
    }

    private void compactLockedTargets() {
        int writeSlot = 0;
        for (int readSlot = 0; readSlot < LOCKED_TARGET_IDS.length; readSlot++) {
            int id = getLockedTargetId(readSlot);
            if (id >= 0) {
                if (writeSlot != readSlot) {
                    setLockedTargetId(writeSlot, id);
                    setLockedTargetId(readSlot, -1);
                }
                writeSlot++;
            }
        }
        while (writeSlot < LOCKED_TARGET_IDS.length) {
            setLockedTargetId(writeSlot++, -1);
        }
    }

    @Override
    protected float scaleIncomingDamage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return amount;
        }
        if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            return Math.max(MIN_PROJECTILE_DAMAGE, amount * PROJECTILE_DAMAGE_MULTIPLIER);
        }
        if (source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            return Math.max(MIN_HEAVY_DAMAGE, amount * EXPLOSION_DAMAGE_MULTIPLIER);
        }
        if (source.getAttacker() instanceof LivingEntity) {
            return Math.max(MIN_HEAVY_DAMAGE, amount * MELEE_DAMAGE_MULTIPLIER);
        }
        return Math.max(MIN_HEAVY_DAMAGE, amount * AMBIENT_DAMAGE_MULTIPLIER);
    }

    @Override
    protected void onVehicleDamaged(ServerWorld world, DamageSource source, float scaledDamage) {
        world.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.LITTLE_BIRD_HIT, SoundCategory.HOSTILE, 0.75F, 0.9F + (world.random.nextFloat() * 0.1F));
    }

    @Override
    protected void destroyVehicle(ServerWorld world) {
        if (isRemoved()) {
            return;
        }

        removeAllPassengers();
        world.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.LITTLE_BIRD_EXPLOSION, SoundCategory.HOSTILE, 1.0F, 0.92F);
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.4D, getZ(), 3, 0.15D, 0.15D, 0.15D, 0.0D);
        world.spawnParticles(ParticleTypes.SMOKE, getX(), getY() + 0.4D, getZ(), 24, 0.45D, 0.24D, 0.45D, 0.03D);
        world.spawnParticles(ParticleTypes.POOF, getX(), getY() + 0.4D, getZ(), 22, 0.55D, 0.25D, 0.55D, 0.02D);
        world.createExplosion(this, getX(), getY() + 0.3D, getZ(), 4.2F, World.ExplosionSourceType.TNT);
        discard();
    }

    @Override
    protected SoundEvent getBoardingSound() {
        return VeilreachSoundEvents.LITTLE_BIRD_STARTUP;
    }

    @Override
    public SoundEvent getLoopingSound() {
        return VeilreachSoundEvents.LITTLE_BIRD_ROTOR_LOOP;
    }

    @Override
    public float getLoopSoundVolume() {
        return MathHelper.clamp(0.2F + (getThrottle() * 0.65F), 0.0F, 1.0F);
    }

    @Override
    public float getLoopSoundPitch() {
        float pitch = 0.78F + (getThrottle() * 0.4F);
        if (isCriticalDamage()) {
            pitch *= 0.9F;
        }
        return pitch;
    }

    @Override
    public float getMaxHealthAmount() {
        return MAX_HEALTH;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(VeilreachItems.LITTLE_BIRD);
    }

    @Override
    public VehicleHudState createHudState(PlayerEntity viewer) {
        Text seatLabel = Text.translatable(getSeatRole(viewer).translationKey());
        Entity lockedTarget = getLockedTarget();
        int lockCount = getLockedTargetCount();
        Text targetText = lockCount <= 0 || lockedTarget == null
                ? Text.translatable("hud.veilreach.little_bird.lock_none")
                : Text.translatable("hud.veilreach.little_bird.lock_primary", lockedTarget.getName(), Math.round(distanceTo(lockedTarget)))
                .copy().append("  ").append(Text.translatable("hud.veilreach.little_bird.locked_count", lockCount));

        return new VehicleHudState(
                Text.translatable("entity.veilreach.little_bird"),
                seatLabel,
                getHealthAmount(),
                MAX_HEALTH,
                getVelocity().horizontalLength() * 20.0D,
                (int) Math.round(getY()),
                getThrottle(),
                List.of(
                        new VehicleWeaponMount(Text.translatable("hud.veilreach.little_bird.rockets"), getRocketAmmo(), MAX_ROCKET_AMMO, getRocketCooldown(), getRocketReload(), 0xFFE7F6FF),
                        new VehicleWeaponMount(Text.translatable("hud.veilreach.little_bird.bombs"), getBombAmmo(), MAX_BOMB_AMMO, getBombCooldown(), getBombReload(), 0xFFFFD7A6),
                        new VehicleWeaponMount(Text.translatable("hud.veilreach.little_bird.tracking"), getTrackingMissileAmmo(), MAX_TRACKING_MISSILES, getTrackingMissileCooldown(), getTrackingMissileReload(), 0xFFFFB88F)
                ),
                targetText,
                Text.translatable("hud.veilreach.little_bird.fire_hint"),
                0xFFD8F1FF,
                0xFFFF8C72,
                lockCount > 0,
                isDamaged(),
                isCriticalDamage(),
                usesFlightHud(viewer)
        );
    }

    @Override
    protected List<VehicleSeatDefinition> getSeatDefinitions() {
        return SEAT_DEFINITIONS;
    }

    @Override
    public void handleVehicleAction(ServerPlayerEntity pilot, LittleBirdActionPayload payload) {
        switch (payload.action()) {
            case LittleBirdActionPayload.FIRE_ROCKET -> tryFireRocket(pilot, payload.yaw(), payload.pitch());
            case LittleBirdActionPayload.DROP_BOMB -> tryDropBomb(pilot);
            case LittleBirdActionPayload.LOCK_TRACKING -> tryToggleTrackingLock(pilot, payload.yaw(), payload.pitch());
            case LittleBirdActionPayload.FIRE_TRACKING -> tryFireTrackingVolley(pilot);
            case LittleBirdActionPayload.CLEAR_TRACKING_LOCKS -> clearTrackingLocks(pilot);
            case LittleBirdActionPayload.UPDATE_CONTROLS ->
                    applyPilotInputs(payload.forward(), payload.turn(), payload.ascend(), payload.descend(), payload.yaw(), payload.pitch());
            default -> {
            }
        }
    }

    @Override
    public void rearmVehicle() {
        setRocketAmmo(MAX_ROCKET_AMMO);
        setRocketCooldown(0);
        setRocketReload(0);
        setBombAmmo(MAX_BOMB_AMMO);
        setBombCooldown(0);
        setBombReload(0);
        setTrackingMissileAmmo(MAX_TRACKING_MISSILES);
        setTrackingMissileCooldown(0);
        setTrackingMissileReload(0);
    }

    @Override
    public List<Entity> getHighlightedTargets() {
        return getLockedTargets();
    }

    @Override
    protected void writeCustomData(WriteView writeView) {
        writeView.putFloat("health", getHealthAmount());
        writeView.putInt("rocket_ammo", getRocketAmmo());
        writeView.putInt("rocket_cooldown", getRocketCooldown());
        writeView.putInt("rocket_reload", getRocketReload());
        writeView.putInt("bomb_ammo", getBombAmmo());
        writeView.putInt("bomb_cooldown", getBombCooldown());
        writeView.putInt("bomb_reload", getBombReload());
        writeView.putInt("tracking_missile_ammo", getTrackingMissileAmmo());
        writeView.putInt("tracking_missile_cooldown", getTrackingMissileCooldown());
        writeView.putInt("tracking_missile_reload", getTrackingMissileReload());
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length; slot++) {
            writeView.putInt("locked_target_" + slot, getLockedTargetId(slot));
        }
        writeView.putFloat("throttle", getThrottle());
        writeView.putFloat("visual_pitch", getVisualPitch());
        writeView.putFloat("visual_roll", getVisualRoll());
        writeView.putInt("launch_side", launchSide);
    }

    @Override
    protected void readCustomData(ReadView readView) {
        setHealthAmount(readView.getFloat("health", MAX_HEALTH));
        setRocketAmmo(readView.getInt("rocket_ammo", MAX_ROCKET_AMMO));
        setRocketCooldown(readView.getInt("rocket_cooldown", 0));
        setRocketReload(readView.getInt("rocket_reload", 0));
        setBombAmmo(readView.getInt("bomb_ammo", MAX_BOMB_AMMO));
        setBombCooldown(readView.getInt("bomb_cooldown", 0));
        setBombReload(readView.getInt("bomb_reload", 0));
        setTrackingMissileAmmo(readView.getInt("tracking_missile_ammo", MAX_TRACKING_MISSILES));
        setTrackingMissileCooldown(readView.getInt("tracking_missile_cooldown", 0));
        setTrackingMissileReload(readView.getInt("tracking_missile_reload", 0));
        for (int slot = 0; slot < LOCKED_TARGET_IDS.length; slot++) {
            setLockedTargetId(slot, readView.getInt("locked_target_" + slot, -1));
        }
        setThrottle(readView.getFloat("throttle", 0.0F));
        setVisualPitch(readView.getFloat("visual_pitch", 0.0F));
        setVisualRoll(readView.getFloat("visual_roll", 0.0F));
        launchSide = readView.getInt("launch_side", 0);
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

    public int getBombAmmo() {
        return getDataTracker().get(BOMB_AMMO);
    }

    private void setBombAmmo(int value) {
        getDataTracker().set(BOMB_AMMO, value);
    }

    public int getBombCooldown() {
        return getDataTracker().get(BOMB_COOLDOWN);
    }

    private void setBombCooldown(int value) {
        getDataTracker().set(BOMB_COOLDOWN, value);
    }

    public int getBombReload() {
        return getDataTracker().get(BOMB_RELOAD);
    }

    private void setBombReload(int value) {
        getDataTracker().set(BOMB_RELOAD, value);
    }

    public int getTrackingMissileAmmo() {
        return getDataTracker().get(TRACKING_MISSILE_AMMO);
    }

    private void setTrackingMissileAmmo(int value) {
        getDataTracker().set(TRACKING_MISSILE_AMMO, value);
    }

    public int getTrackingMissileCooldown() {
        return getDataTracker().get(TRACKING_MISSILE_COOLDOWN);
    }

    private void setTrackingMissileCooldown(int value) {
        getDataTracker().set(TRACKING_MISSILE_COOLDOWN, value);
    }

    public int getTrackingMissileReload() {
        return getDataTracker().get(TRACKING_MISSILE_RELOAD);
    }

    private void setTrackingMissileReload(int value) {
        getDataTracker().set(TRACKING_MISSILE_RELOAD, value);
    }

    public int getLockedTargetId(int slot) {
        return getDataTracker().get(LOCKED_TARGET_IDS[slot]);
    }

    private void setLockedTargetId(int slot, int value) {
        getDataTracker().set(LOCKED_TARGET_IDS[slot], value);
    }

    public float getRotorAngle(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastRotorAngle, rotorAngle);
    }

    public float getTailRotorAngle(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastTailRotorAngle, tailRotorAngle);
    }
}
