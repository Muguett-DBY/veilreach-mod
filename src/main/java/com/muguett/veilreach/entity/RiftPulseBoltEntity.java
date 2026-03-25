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

public final class RiftPulseBoltEntity extends ThrownItemEntity {
    private static final float EXPLOSION_POWER = 2.35F;
    private static final int MAX_LIFETIME = 44;
    private static final int SAFETY_TICKS = 4;

    private int safetyTicks = SAFETY_TICKS;

    public RiftPulseBoltEntity(EntityType<? extends RiftPulseBoltEntity> entityType, World world) {
        super(entityType, world);
    }

    public RiftPulseBoltEntity(World world, LivingEntity owner) {
        super(VeilreachEntities.RIFT_PULSE_BOLT, owner, world, new ItemStack(VeilreachItems.RIFT_PULSE_BOLT_ITEM));
    }

    @Override
    public void tick() {
        super.tick();
        if (safetyTicks > 0) {
            safetyTicks--;
        }

        if (getEntityWorld().isClient()) {
            getEntityWorld().addParticleClient(ParticleTypes.END_ROD, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            getEntityWorld().addParticleClient(ParticleTypes.GLOW, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            return;
        }

        if (age % 2 == 0 && getEntityWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 3, 0.05D, 0.05D, 0.05D, 0.0D);
            serverWorld.spawnParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 2, 0.04D, 0.04D, 0.04D, 0.0D);
        }

        if (age > MAX_LIFETIME) {
            explode();
        }
    }

    @Override
    protected double getGravity() {
        return 0.0D;
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
        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_SKIMMER_PULSE, SoundCategory.HOSTILE, 0.8F, 0.82F);
        serverWorld.createExplosion(this, getX(), getY(), getZ(), EXPLOSION_POWER, World.ExplosionSourceType.TNT);
        serverWorld.spawnParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 14, 0.2D, 0.12D, 0.2D, 0.02D);
        serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 10, 0.18D, 0.1D, 0.18D, 0.0D);
        discard();
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.RIFT_PULSE_BOLT_ITEM;
    }
}
