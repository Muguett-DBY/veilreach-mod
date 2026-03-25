package com.muguett.veilreach.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractVehicleEntity extends Entity {
    protected static final int CONTROL_TIMEOUT_TICKS = 8;
    private static final TrackedData<Float> HEALTH = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> THROTTLE = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VISUAL_PITCH = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VISUAL_ROLL = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);

    protected float pilotForwardInput;
    protected float pilotTurnInput;
    protected boolean pilotAscendInput;
    protected boolean pilotDescendInput;
    protected float pilotAimYaw;
    protected float pilotAimPitch;
    protected int pilotControlTicks;
    protected int damageFlashTicks;

    protected AbstractVehicleEntity(EntityType<? extends AbstractVehicleEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
    }

    protected final void initVehicleTrackerBase(DataTracker.Builder builder) {
        builder.add(HEALTH, getMaxHealthAmount());
        builder.add(THROTTLE, 0.0F);
        builder.add(VISUAL_PITCH, 0.0F);
        builder.add(VISUAL_ROLL, 0.0F);
    }

    public final void applyPilotInputs(float forward, float turn, boolean ascend, boolean descend, float yaw, float pitch) {
        this.pilotForwardInput = forward;
        this.pilotTurnInput = turn;
        this.pilotAscendInput = ascend;
        this.pilotDescendInput = descend;
        this.pilotAimYaw = yaw;
        this.pilotAimPitch = pitch;
        this.pilotControlTicks = CONTROL_TIMEOUT_TICKS;
    }

    protected final void tickPilotInputWindow() {
        if (pilotControlTicks > 0) {
            pilotControlTicks--;
            return;
        }
        pilotForwardInput = 0.0F;
        pilotTurnInput = 0.0F;
        pilotAscendInput = false;
        pilotDescendInput = false;
    }

    protected final void tickDamageFlash() {
        if (damageFlashTicks > 0) {
            damageFlashTicks--;
        }
    }

    protected final void triggerDamageFlash() {
        damageFlashTicks = 8;
    }

    public float getDamageFlash(float tickDelta) {
        return damageFlashTicks > 0 ? 1.0F : 0.0F;
    }

    public boolean isDamaged() {
        return getHealthAmount() <= (getMaxHealthAmount() * 0.4F);
    }

    public boolean isCriticalDamage() {
        return getHealthAmount() <= (getMaxHealthAmount() * 0.15F);
    }

    public float getDamageFraction() {
        return Math.max(0.0F, Math.min(1.0F, getHealthAmount() / getMaxHealthAmount()));
    }

    public void repairFully() {
        setHealthAmount(getMaxHealthAmount());
        damageFlashTicks = 0;
    }

    public void repairPartial(float amount) {
        setHealthAmount(Math.min(getMaxHealthAmount(), getHealthAmount() + amount));
    }

    public float getHealthAmount() {
        return dataTracker.get(HEALTH);
    }

    protected void setHealthAmount(float value) {
        dataTracker.set(HEALTH, value);
    }

    public abstract float getMaxHealthAmount();

    public float getThrottle() {
        return dataTracker.get(THROTTLE);
    }

    protected void setThrottle(float value) {
        dataTracker.set(THROTTLE, value);
    }

    public float getVisualPitch() {
        return dataTracker.get(VISUAL_PITCH);
    }

    protected void setVisualPitch(float value) {
        dataTracker.set(VISUAL_PITCH, value);
    }

    public float getVisualRoll() {
        return dataTracker.get(VISUAL_ROLL);
    }

    protected void setVisualRoll(float value) {
        dataTracker.set(VISUAL_ROLL, value);
    }

    protected abstract float scaleIncomingDamage(DamageSource source, float amount);

    protected abstract void onVehicleDamaged(ServerWorld world, DamageSource source, float scaledDamage);

    protected abstract void destroyVehicle(ServerWorld world);

    protected @Nullable SoundEvent getBoardingSound() {
        return null;
    }

    public @Nullable SoundEvent getLoopingSound() {
        return null;
    }

    public float getLoopSoundVolume() {
        return 0.0F;
    }

    public float getLoopSoundPitch() {
        return 1.0F;
    }

    public Vec3d getPassengerAnchorOffset(Entity passenger) {
        return new Vec3d(0.0D, 0.9D + (passenger.getHeight() * 0.02D), 0.0D);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengerList().isEmpty() && passenger instanceof LivingEntity;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (!hasPassenger(passenger)) {
            return;
        }
        Vec3d seatOffset = getPassengerAnchorOffset(passenger).rotateY(-(getYaw() * 0.017453292F));
        positionUpdater.accept(passenger, getX() + seatOffset.x, getY() + seatOffset.y, getZ() + seatOffset.z);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        if (hasPassenger(player)) {
            return ActionResult.CONSUME;
        }
        if (hasPassengers()) {
            return ActionResult.PASS;
        }
        if (!getEntityWorld().isClient()) {
            player.startRiding(this, true, true);
            SoundEvent boardingSound = getBoardingSound();
            if (boardingSound != null) {
                getEntityWorld().playSound(null, getX(), getY(), getZ(), boardingSound, SoundCategory.NEUTRAL, 0.85F, 1.0F);
            }
        }
        return getEntityWorld().isClient() ? ActionResult.SUCCESS : ActionResult.CONSUME;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (isRemoved()) {
            return false;
        }

        float scaledDamage = scaleIncomingDamage(source, amount);
        if (scaledDamage <= 0.0F) {
            return false;
        }

        setHealthAmount(Math.max(0.0F, getHealthAmount() - scaledDamage));
        triggerDamageFlash();
        onVehicleDamaged(world, source, scaledDamage);
        if (getHealthAmount() <= 0.0F) {
            destroyVehicle(world);
        }
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public boolean shouldAlwaysSyncAbsolute() {
        return false;
    }

    @Override
    public boolean isFlyingVehicle() {
        return true;
    }
}
