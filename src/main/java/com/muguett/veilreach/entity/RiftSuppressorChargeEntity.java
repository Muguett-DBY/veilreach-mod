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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class RiftSuppressorChargeEntity extends ThrownItemEntity {
    private static final float PRIMARY_POWER = 3.2F;
    private static final float SPLASH_POWER = 1.6F;
    private static final int MAX_LIFETIME = 84;
    private static final int SAFETY_TICKS = 6;

    private int safetyTicks = SAFETY_TICKS;

    public RiftSuppressorChargeEntity(EntityType<? extends RiftSuppressorChargeEntity> entityType, World world) {
        super(entityType, world);
    }

    public RiftSuppressorChargeEntity(World world, LivingEntity owner) {
        super(VeilreachEntities.RIFT_SUPPRESSOR_CHARGE, owner, world, new ItemStack(VeilreachItems.RIFT_SUPPRESSOR_CHARGE_ITEM));
    }

    @Override
    public void tick() {
        super.tick();
        if (safetyTicks > 0) {
            safetyTicks--;
        }

        if (getEntityWorld().isClient()) {
            getEntityWorld().addParticleClient(ParticleTypes.PORTAL, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            return;
        }

        if (age % 3 == 0 && getEntityWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.PORTAL, getX(), getY(), getZ(), 4, 0.08D, 0.08D, 0.08D, 0.06D);
        }

        if (age > MAX_LIFETIME) {
            explode();
        }
    }

    @Override
    protected double getGravity() {
        return 0.02D;
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
        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_SIEGE_SUPPRESS, SoundCategory.HOSTILE, 0.92F, 0.88F);
        serverWorld.createExplosion(this, getX(), getY(), getZ(), PRIMARY_POWER, World.ExplosionSourceType.TNT);
        for (int index = 0; index < 6; index++) {
            float angle = (float) ((Math.PI * 2.0D * index) / 6.0D);
            double offsetX = MathHelper.cos(angle) * 2.1D;
            double offsetZ = MathHelper.sin(angle) * 2.1D;
            serverWorld.createExplosion(this, getX() + offsetX, getY(), getZ() + offsetZ, SPLASH_POWER, World.ExplosionSourceType.TNT);
        }
        serverWorld.spawnParticles(ParticleTypes.PORTAL, getX(), getY(), getZ(), 28, 0.5D, 0.2D, 0.5D, 0.22D);
        discard();
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.RIFT_SUPPRESSOR_CHARGE_ITEM;
    }
}
