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

public final class RiftGunshipRoundEntity extends ThrownItemEntity {
    private static final float EXPLOSION_POWER = 1.8F;
    private static final int MAX_LIFETIME = 90;
    private static final int SAFETY_TICKS = 3;

    private int safetyTicks = SAFETY_TICKS;

    public RiftGunshipRoundEntity(EntityType<? extends RiftGunshipRoundEntity> entityType, World world) {
        super(entityType, world);
    }

    public RiftGunshipRoundEntity(World world, LivingEntity owner) {
        super(VeilreachEntities.RIFT_GUNSHIP_ROUND, owner, world, new ItemStack(VeilreachItems.RIFT_GUNSHIP_ROUND_ITEM));
    }

    @Override
    public void tick() {
        super.tick();
        if (safetyTicks > 0) {
            safetyTicks--;
        }

        if (getEntityWorld().isClient()) {
            getEntityWorld().addParticleClient(ParticleTypes.GLOW, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            return;
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
        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.RIFT_GUNSHIP_CANNON, SoundCategory.HOSTILE, 0.56F, 1.2F);
        serverWorld.createExplosion(this, getX(), getY(), getZ(), EXPLOSION_POWER, World.ExplosionSourceType.TNT);
        serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 10, 0.12D, 0.08D, 0.12D, 0.0D);
        serverWorld.spawnParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 8, 0.16D, 0.1D, 0.16D, 0.012D);
        discard();
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.RIFT_GUNSHIP_ROUND_ITEM;
    }
}
