package com.muguett.veilreach.entity.vehicle;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractHoverVehicleEntity extends AbstractArmedVehicleEntity {
    protected AbstractHoverVehicleEntity(EntityType<? extends AbstractHoverVehicleEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
        tickPilotInputWindow();

        if (!getEntityWorld().isClient()) {
            syncSeatAssignmentsServer();
            tickVehicleSystemsServer();
        }

        if (isLogicalSideForUpdatingMovement()) {
            LivingEntity pilot = getControllingPassenger();
            if (pilot != null) {
                tickPilotedMovement(pilot);
            } else {
                tickIdleMovement();
            }

            Vec3d velocity = clampVehicleVelocity(getVelocity());
            setVelocity(velocity);
            move(MovementType.SELF, velocity);
            Vec3d postMove = getVelocity().multiply(getHorizontalDrag(), getVerticalDrag(), getHorizontalDrag());
            if (!pilotAscendInput && !pilotDescendInput && Math.abs(postMove.y) < 0.01D) {
                postMove = new Vec3d(postMove.x, 0.0D, postMove.z);
            }
            setVelocity(postMove);
            updateTiltState();

            if (!getEntityWorld().isClient() && getEntityWorld() instanceof ServerWorld serverWorld) {
                tickCollisionDamage(serverWorld);
                tickVehicleEffectsServer(serverWorld);
            }
        }

        if (getEntityWorld().isClient()) {
            tickVehicleEffectsClient();
        }

        tickDamageFlash();
        for (var passenger : getPassengerList()) {
            updatePassengerPosition(passenger);
        }
    }

    protected void tickPilotedMovement(LivingEntity pilot) {
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
            turnInput = pilot.sidewaysSpeed;
            ascend = pilot.isJumping();
            descend = pilot.isSneaking();
            targetYaw = pilot.getYaw();
        }

        float yawDelta = MathHelper.wrapDegrees(targetYaw - getYaw());
        setYaw(getYaw() + MathHelper.clamp(yawDelta, -getYawFollowRate(), getYawFollowRate()));

        Vec3d velocity = getVelocity();
        Vec3d forwardVector = Vec3d.fromPolar(0.0F, getYaw());
        Vec3d rightVector = new Vec3d(-forwardVector.z, 0.0D, forwardVector.x);
        velocity = velocity
                .add(forwardVector.multiply(getForwardAcceleration() * (forwardInput - (backwardInput * getReversePenalty()))))
                .add(rightVector.multiply(getStrafeAcceleration() * turnInput));

        double targetVertical = 0.0D;
        if (ascend) {
            targetVertical = getAscentRate();
        } else if (descend) {
            targetVertical = getDescentRate();
        } else {
            targetVertical = getHoverLiftBias();
        }
        double newVertical = MathHelper.lerp(getVerticalResponse(), velocity.y, targetVertical);
        setVelocity(velocity.x, newVertical, velocity.z);

        float desiredThrottle = getBaseThrottle()
                + (forwardInput * getForwardThrottleGain())
                + (ascend ? getAscendThrottleGain() : 0.0F)
                + (descend ? getDescendThrottleGain() : 0.0F)
                + (Math.abs(turnInput) * getTurnThrottleGain());
        setThrottle(MathHelper.lerp(0.18F, getThrottle(), MathHelper.clamp(desiredThrottle, getMinThrottle(), 1.0F)));
    }

    protected void tickIdleMovement() {
        setThrottle(MathHelper.lerp(0.18F, getThrottle(), getMinThrottle()));
        setVelocity(getVelocity().multiply(0.92D, 0.82D, 0.92D));
    }

    protected Vec3d clampVehicleVelocity(Vec3d velocity) {
        double horizontal = velocity.horizontalLength();
        if (horizontal > getMaxHorizontalSpeed()) {
            double scale = getMaxHorizontalSpeed() / horizontal;
            velocity = new Vec3d(velocity.x * scale, velocity.y, velocity.z * scale);
        }
        double clampedY = MathHelper.clamp(velocity.y, -getMaxVerticalSpeed(), getMaxVerticalSpeed());
        return new Vec3d(velocity.x, clampedY, velocity.z);
    }

    protected void updateTiltState() {
        double horizontalSpeed = getVelocity().horizontalLength();
        float pitchTarget = MathHelper.clamp((float) (-(horizontalSpeed * getPitchSpeedFactor()) - (getVelocity().y * getPitchLiftFactor())), -18.0F, 18.0F);
        float rollTarget = MathHelper.clamp((float) (pilotTurnInput * -getRollResponseFactor()), -32.0F, 32.0F);
        setVisualPitch(MathHelper.lerp(0.18F, getVisualPitch(), pitchTarget));
        setVisualRoll(MathHelper.lerp(0.24F, getVisualRoll(), rollTarget));
    }

    protected void tickCollisionDamage(ServerWorld serverWorld) {
        float impactStrength = (float) getVelocity().length();
        if ((horizontalCollision || verticalCollision) && impactStrength > getCrashDamageThreshold()) {
            float damage = impactStrength * getCrashDamageFactor();
            damage(serverWorld, getDamageSources().flyIntoWall(), damage);
        }
    }

    protected void tickVehicleEffectsServer(ServerWorld serverWorld) {
        if (isCriticalDamage() && age % 5 == 0) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, getX(), getY() + 0.8D, getZ(), 4, 0.18D, 0.08D, 0.18D, 0.008D);
        } else if (isDamaged() && age % 6 == 0) {
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 0.8D, getZ(), 2, 0.24D, 0.12D, 0.24D, 0.01D);
        }
    }

    protected void tickVehicleEffectsClient() {
    }

    @Override
    protected float scaleIncomingDamage(DamageSource source, float amount) {
        if (source.isIn(net.minecraft.registry.tag.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return amount;
        }
        if (source.isIn(net.minecraft.registry.tag.DamageTypeTags.IS_PROJECTILE)) {
            return Math.max(getMinimumProjectileDamage(), amount * getProjectileDamageMultiplier());
        }
        if (source.isIn(net.minecraft.registry.tag.DamageTypeTags.IS_EXPLOSION)) {
            return Math.max(getMinimumHeavyDamage(), amount * getExplosionDamageMultiplier());
        }
        if (source.getAttacker() instanceof LivingEntity) {
            return Math.max(getMinimumHeavyDamage(), amount * getMeleeDamageMultiplier());
        }
        return Math.max(getMinimumHeavyDamage(), amount * getAmbientDamageMultiplier());
    }

    @Override
    protected void onVehicleDamaged(ServerWorld world, DamageSource source, float scaledDamage) {
        world.playSound(null, getX(), getY(), getZ(), getHitSound(), SoundCategory.HOSTILE, 0.8F, 0.92F + (world.random.nextFloat() * 0.08F));
    }

    @Override
    protected void destroyVehicle(ServerWorld world) {
        if (isRemoved()) {
            return;
        }
        removeAllPassengers();
        world.playSound(null, getX(), getY(), getZ(), getDestroySound(), SoundCategory.HOSTILE, 1.0F, 0.94F);
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.25D, getZ(), 2, 0.15D, 0.15D, 0.15D, 0.0D);
        world.spawnParticles(ParticleTypes.POOF, getX(), getY() + 0.35D, getZ(), 20, 0.4D, 0.2D, 0.4D, 0.02D);
        world.spawnParticles(ParticleTypes.SMOKE, getX(), getY() + 0.4D, getZ(), 28, 0.35D, 0.16D, 0.35D, 0.03D);
        world.createExplosion(this, getX(), getY() + 0.3D, getZ(), getDestructionPower(), World.ExplosionSourceType.TNT);
        discard();
    }

    protected float getReversePenalty() {
        return 0.45F;
    }

    protected float getBaseThrottle() {
        return 0.32F;
    }

    protected float getForwardThrottleGain() {
        return 0.34F;
    }

    protected float getAscendThrottleGain() {
        return 0.16F;
    }

    protected float getDescendThrottleGain() {
        return 0.06F;
    }

    protected float getTurnThrottleGain() {
        return 0.08F;
    }

    protected float getMinThrottle() {
        return 0.2F;
    }

    protected double getHoverLiftBias() {
        return 0.0D;
    }

    protected double getHorizontalDrag() {
        return 0.93D;
    }

    protected double getVerticalDrag() {
        return 0.9D;
    }

    protected double getVerticalResponse() {
        return 0.3D;
    }

    protected float getPitchSpeedFactor() {
        return 18.0F;
    }

    protected float getPitchLiftFactor() {
        return 12.0F;
    }

    protected float getRollResponseFactor() {
        return 20.0F;
    }

    protected float getCrashDamageThreshold() {
        return 0.95F;
    }

    protected float getCrashDamageFactor() {
        return 14.0F;
    }

    protected float getProjectileDamageMultiplier() {
        return 0.12F;
    }

    protected float getExplosionDamageMultiplier() {
        return 0.44F;
    }

    protected float getMeleeDamageMultiplier() {
        return 0.3F;
    }

    protected float getAmbientDamageMultiplier() {
        return 0.28F;
    }

    protected float getMinimumProjectileDamage() {
        return 0.5F;
    }

    protected float getMinimumHeavyDamage() {
        return 1.0F;
    }

    protected abstract void tickVehicleSystemsServer();

    protected abstract float getForwardAcceleration();

    protected abstract float getStrafeAcceleration();

    protected abstract float getYawFollowRate();

    protected abstract double getAscentRate();

    protected abstract double getDescentRate();

    protected abstract double getMaxHorizontalSpeed();

    protected abstract double getMaxVerticalSpeed();

    protected abstract SoundEvent getHitSound();

    protected abstract SoundEvent getDestroySound();

    protected abstract float getDestructionPower();

    @Override
    public abstract ItemStack getPickBlockStack();
}
