package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import com.muguett.veilreach.entity.vehicle.AbstractVehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;

public final class LittleBirdTrackingMissileEntity extends ThrownItemEntity {
    private static final float EXPLOSION_POWER = 6.4F;
    private static final int MAX_LIFETIME = 220;
    private static final int SAFETY_TICKS = 7;
    private static final double CRUISE_SPEED = 2.15D;
    private static final double TURN_BLEND = 0.2D;

    private int targetId = -1;
    private int safetyTicks = SAFETY_TICKS;

    public LittleBirdTrackingMissileEntity(EntityType<? extends LittleBirdTrackingMissileEntity> entityType, World world) {
        super(entityType, world);
    }

    public LittleBirdTrackingMissileEntity(World world, LivingEntity owner, Entity target) {
        super(VeilreachEntities.LITTLE_BIRD_TRACKING_MISSILE, owner, world, new ItemStack(VeilreachItems.LITTLE_BIRD_ROCKET_ITEM));
        if (target != null) {
            this.targetId = target.getId();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (safetyTicks > 0) {
            safetyTicks--;
        }

        Entity target = getTarget();
        if (target != null && target.isAlive()) {
            if (target instanceof AbstractVehicleEntity vehicle && vehicle.hasActiveCountermeasures()) {
                targetId = -1;
            } else {
                homeTowards(target);
            }
        }

        if (getEntityWorld().isClient()) {
            getEntityWorld().addParticleClient(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0.0D, 0.006D, 0.0D);
            getEntityWorld().addParticleClient(ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            getEntityWorld().addParticleClient(ParticleTypes.GLOW, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            if ((age & 1) == 0) {
                getEntityWorld().addParticleClient(ParticleTypes.CLOUD, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
            return;
        }

        if (age % 2 == 0 && getEntityWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 8, 0.12D, 0.06D, 0.12D, 0.014D);
            serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 5, 0.05D, 0.05D, 0.05D, 0.005D);
            serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 2, 0.02D, 0.02D, 0.02D, 0.0D);
        }

        if (age > MAX_LIFETIME) {
            explode();
        }
    }

    private void homeTowards(Entity target) {
        double targetY = target.getY() + (target.getHeight() * 0.45D);
        Vec3d desired = new Vec3d(target.getX() - getX(), targetY - getY(), target.getZ() - getZ());
        if (desired.lengthSquared() < 1.0E-5D) {
            return;
        }
        desired = desired.normalize().multiply(CRUISE_SPEED);
        Vec3d velocity = getVelocity();
        if (velocity.lengthSquared() < 1.0E-5D) {
            setVelocity(desired);
            return;
        }
        Vec3d blended = velocity.multiply(1.0D - TURN_BLEND).add(desired.multiply(TURN_BLEND));
        double speed = Math.max(CRUISE_SPEED, blended.length());
        setVelocity(blended.normalize().multiply(speed));
        velocityDirty = true;
    }

    private Entity getTarget() {
        return targetId >= 0 ? getEntityWorld().getEntityById(targetId) : null;
    }

    @Override
    protected double getGravity() {
        return 0.0006D;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        Entity owner = getOwner();
        if (safetyTicks > 0 && (hitEntity == owner || (owner != null && hitEntity == owner.getVehicle()))) {
            return;
        }
        explode();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        explode();
    }

    private void explode() {
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }
        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.LITTLE_BIRD_ROCKET_IMPACT, SoundCategory.HOSTILE, 1.0F, 0.82F);
        serverWorld.createExplosion(this, getX(), getY(), getZ(), EXPLOSION_POWER, World.ExplosionSourceType.TNT);
        serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 4, 0.08D, 0.08D, 0.08D, 0.0D);
        serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, getX(), getY(), getZ(), 24, 0.38D, 0.26D, 0.38D, 0.035D);
        serverWorld.spawnParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 18, 0.3D, 0.2D, 0.3D, 0.024D);
        discard();
    }

    @Override
    protected void writeCustomData(WriteView writeView) {
        super.writeCustomData(writeView);
        writeView.putInt("target_id", targetId);
        writeView.putInt("safety_ticks", safetyTicks);
    }

    @Override
    protected void readCustomData(ReadView readView) {
        super.readCustomData(readView);
        targetId = readView.getInt("target_id", -1);
        safetyTicks = readView.getInt("safety_ticks", SAFETY_TICKS);
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.LITTLE_BIRD_ROCKET_ITEM;
    }
}
