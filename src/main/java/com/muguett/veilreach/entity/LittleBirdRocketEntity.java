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

public final class LittleBirdRocketEntity extends ThrownItemEntity {
    private static final float EXPLOSION_POWER = 4.6F;
    private static final int MAX_LIFETIME = 96;
    private static final int SAFETY_TICKS = 5;

    private int safetyTicks = SAFETY_TICKS;

    public LittleBirdRocketEntity(EntityType<? extends LittleBirdRocketEntity> entityType, World world) {
        super(entityType, world);
    }

    public LittleBirdRocketEntity(World world, LivingEntity owner) {
        super(VeilreachEntities.LITTLE_BIRD_ROCKET, owner, world, new ItemStack(VeilreachItems.LITTLE_BIRD_ROCKET_ITEM));
    }

    @Override
    public void tick() {
        super.tick();
        if (safetyTicks > 0) {
            safetyTicks--;
        }

        if (getEntityWorld().isClient()) {
            getEntityWorld().addParticleClient(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0.0D, 0.005D, 0.0D);
            getEntityWorld().addParticleClient(ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            if ((age & 1) == 0) {
                getEntityWorld().addParticleClient(ParticleTypes.CLOUD, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
            return;
        }

        if (age % 2 == 0 && getEntityWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 6, 0.11D, 0.05D, 0.11D, 0.012D);
            serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 3, 0.04D, 0.04D, 0.04D, 0.004D);
            serverWorld.spawnParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(), 2, 0.07D, 0.03D, 0.07D, 0.008D);
        }

        if (age > MAX_LIFETIME) {
            explode();
        }
    }

    @Override
    protected double getGravity() {
        return 0.0015D;
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
        serverWorld.playSound(null, getX(), getY(), getZ(), VeilreachSoundEvents.LITTLE_BIRD_ROCKET_IMPACT, SoundCategory.HOSTILE, 0.9F, 0.95F);
        serverWorld.createExplosion(this, getX(), getY(), getZ(), EXPLOSION_POWER, World.ExplosionSourceType.TNT);
        serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 3, 0.04D, 0.04D, 0.04D, 0.0D);
        serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, getX(), getY(), getZ(), 18, 0.32D, 0.22D, 0.32D, 0.03D);
        serverWorld.spawnParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 14, 0.24D, 0.18D, 0.24D, 0.02D);
        discard();
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.LITTLE_BIRD_ROCKET_ITEM;
    }
}
