package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public final class RiftGunshipRocketEntity extends ThrownItemEntity {
    private static final float EXPLOSION_POWER = 4.9F;
    private static final int MAX_LIFETIME = 84;
    private static final int SAFETY_TICKS = 5;

    private int safetyTicks = SAFETY_TICKS;

    public RiftGunshipRocketEntity(EntityType<? extends RiftGunshipRocketEntity> entityType, World world) {
        super(entityType, world);
    }

    public RiftGunshipRocketEntity(World world, LivingEntity owner) {
        super(VeilreachEntities.RIFT_GUNSHIP_ROCKET, owner, world, new ItemStack(VeilreachItems.RIFT_GUNSHIP_ROCKET_ITEM));
    }

    @Override
    public void tick() {
        super.tick();
        if (safetyTicks > 0) {
            safetyTicks--;
        }

        if (getEntityWorld().isClient()) {
            getEntityWorld().addParticleClient(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0.0D, 0.004D, 0.0D);
            getEntityWorld().addParticleClient(ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            getEntityWorld().addParticleClient(ParticleTypes.GLOW, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            return;
        }

        if (age % 2 == 0 && getEntityWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 6, 0.1D, 0.04D, 0.1D, 0.012D);
            serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 4, 0.04D, 0.04D, 0.04D, 0.004D);
            serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 2, 0.02D, 0.02D, 0.02D, 0.0D);
        }

        if (age > MAX_LIFETIME) {
            explode();
        }
    }

    @Override
    protected double getGravity() {
        return 0.0012D;
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
        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_GUNSHIP_ROCKET, SoundCategory.HOSTILE, 0.94F, 0.72F);
        serverWorld.createExplosion(this, getX(), getY(), getZ(), EXPLOSION_POWER, World.ExplosionSourceType.TNT);
        serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 3, 0.06D, 0.06D, 0.06D, 0.0D);
        serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, getX(), getY(), getZ(), 16, 0.26D, 0.2D, 0.26D, 0.028D);
        serverWorld.spawnParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 16, 0.24D, 0.16D, 0.24D, 0.02D);
        discard();
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.RIFT_GUNSHIP_ROCKET_ITEM;
    }
}
