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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractVehicleEntity extends Entity {
    protected static final int CONTROL_TIMEOUT_TICKS = 8;
    private static final int MAX_TRACKED_SEATS = 4;
    private static final TrackedData<Float> HEALTH = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> THROTTLE = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VISUAL_PITCH = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VISUAL_ROLL = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    @SuppressWarnings("unchecked")
    private static final TrackedData<Integer>[] SEAT_OCCUPANTS = new TrackedData[] {
            DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.INTEGER),
            DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.INTEGER)
    };

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
        for (TrackedData<Integer> seatOccupant : SEAT_OCCUPANTS) {
            builder.add(seatOccupant, -1);
        }
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

    protected Vec3d getDefaultPilotSeatOffset() {
        return new Vec3d(0.0D, 0.9D, 0.0D);
    }

    protected Vec3d getDefaultPilotDismountOffset() {
        return new Vec3d(1.4D, 0.0D, 0.0D);
    }

    protected List<VehicleSeatDefinition> getSeatDefinitions() {
        return List.of(new VehicleSeatDefinition(VehicleSeatRole.PILOT, getDefaultPilotSeatOffset(), getDefaultPilotDismountOffset(), true));
    }

    protected final void syncSeatAssignmentsServer() {
        if (getEntityWorld().isClient()) {
            return;
        }

        for (int seatIndex = 0; seatIndex < MAX_TRACKED_SEATS; seatIndex++) {
            int occupantId = getSeatOccupantId(seatIndex);
            if (occupantId < 0) {
                continue;
            }
            Entity occupant = getEntityWorld().getEntityById(occupantId);
            if (occupant == null || !hasPassenger(occupant)) {
                setSeatOccupantId(seatIndex, -1);
            }
        }

        for (Entity passenger : getPassengerList()) {
            if (getSeatIndex(passenger) >= 0) {
                continue;
            }
            int seatIndex = getFirstAvailableSeatIndex();
            if (seatIndex >= 0) {
                assignSeat(passenger, seatIndex);
            }
        }
    }

    protected final void assignSeat(Entity passenger, int seatIndex) {
        if (seatIndex < 0 || seatIndex >= Math.min(getSeatDefinitions().size(), MAX_TRACKED_SEATS)) {
            return;
        }
        setSeatOccupantId(seatIndex, passenger.getId());
    }

    public final int getSeatIndex(@Nullable Entity passenger) {
        if (passenger == null) {
            return -1;
        }
        for (int seatIndex = 0; seatIndex < Math.min(getSeatDefinitions().size(), MAX_TRACKED_SEATS); seatIndex++) {
            if (getSeatOccupantId(seatIndex) == passenger.getId()) {
                return seatIndex;
            }
        }
        return -1;
    }

    public final VehicleSeatRole getSeatRole(@Nullable Entity passenger) {
        int seatIndex = getSeatIndex(passenger);
        if (seatIndex < 0 || seatIndex >= getSeatDefinitions().size()) {
            return VehicleSeatRole.PASSENGER;
        }
        return getSeatDefinitions().get(seatIndex).role();
    }

    public final @Nullable VehicleSeatDefinition getSeatDefinition(@Nullable Entity passenger) {
        int seatIndex = getSeatIndex(passenger);
        if (seatIndex < 0 || seatIndex >= getSeatDefinitions().size()) {
            return null;
        }
        return getSeatDefinitions().get(seatIndex);
    }

    public final boolean usesFlightHud(@Nullable Entity passenger) {
        VehicleSeatDefinition definition = getSeatDefinition(passenger);
        return definition == null || definition.usesFlightHud();
    }

    public final boolean isPilot(@Nullable Entity passenger) {
        return getSeatRole(passenger) == VehicleSeatRole.PILOT;
    }

    public final @Nullable LivingEntity getPassengerForRole(VehicleSeatRole role) {
        List<VehicleSeatDefinition> seats = getSeatDefinitions();
        for (int seatIndex = 0; seatIndex < Math.min(seats.size(), MAX_TRACKED_SEATS); seatIndex++) {
            if (seats.get(seatIndex).role() != role) {
                continue;
            }
            Entity occupant = getEntityWorld().getEntityById(getSeatOccupantId(seatIndex));
            if (occupant instanceof LivingEntity living && hasPassenger(occupant)) {
                return living;
            }
        }
        return null;
    }

    public Vec3d getPassengerAnchorOffset(Entity passenger) {
        VehicleSeatDefinition definition = getSeatDefinition(passenger);
        Vec3d base = definition != null ? definition.seatOffset() : getDefaultPilotSeatOffset();
        return base.add(0.0D, passenger.getHeight() * 0.02D, 0.0D);
    }

    protected Vec3d getPassengerDismountOffset(Entity passenger) {
        VehicleSeatDefinition definition = getSeatDefinition(passenger);
        return definition != null ? definition.dismountOffset() : getDefaultPilotDismountOffset();
    }

    protected final int getFirstAvailableSeatIndex() {
        int maxSeats = Math.min(getSeatDefinitions().size(), MAX_TRACKED_SEATS);
        for (int seatIndex = 0; seatIndex < maxSeats; seatIndex++) {
            if (getSeatOccupantId(seatIndex) < 0) {
                return seatIndex;
            }
        }
        return -1;
    }

    private int getSeatOccupantId(int seatIndex) {
        return dataTracker.get(SEAT_OCCUPANTS[seatIndex]);
    }

    private void setSeatOccupantId(int seatIndex, int entityId) {
        dataTracker.set(SEAT_OCCUPANTS[seatIndex], entityId);
    }

    public boolean hasActiveCountermeasures() {
        return false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof LivingEntity && getPassengerList().size() < Math.min(getSeatDefinitions().size(), MAX_TRACKED_SEATS);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return getPassengerForRole(VehicleSeatRole.PILOT);
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
        int seatIndex = getFirstAvailableSeatIndex();
        if (seatIndex < 0) {
            return ActionResult.PASS;
        }
        if (!getEntityWorld().isClient()) {
            boolean started = player.startRiding(this, true, true);
            if (started) {
                assignSeat(player, seatIndex);
            }
            SoundEvent boardingSound = seatIndex == 0 ? getBoardingSound() : null;
            if (started && boardingSound != null) {
                getEntityWorld().playSound(null, getX(), getY(), getZ(), boardingSound, SoundCategory.NEUTRAL, 0.85F, 1.0F);
            }
        }
        return getEntityWorld().isClient() ? ActionResult.SUCCESS : ActionResult.CONSUME;
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Vec3d localOffset = getPassengerDismountOffset(passenger);
        Vec3d rotated = localOffset.rotateY(-(getYaw() * MathHelper.RADIANS_PER_DEGREE));
        return new Vec3d(getX() + rotated.x, getY() + rotated.y, getZ() + rotated.z);
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
