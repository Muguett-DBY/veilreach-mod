package com.muguett.veilreach.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VeilboundWatcherEntity extends EndermanEntity {
    public VeilboundWatcherEntity(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ALLAY_DEATH;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTarget() != null && this.age % 80 == 0) {
            Vec3d targetPos = new Vec3d(this.getTarget().getX(), this.getTarget().getY(), this.getTarget().getZ());
            this.requestTeleport(targetPos.x + (this.random.nextDouble() - 0.5D) * 4.0D, targetPos.y, targetPos.z + (this.random.nextDouble() - 0.5D) * 4.0D);
            this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.7F, 0.6F);
            if (this.squaredDistanceTo(this.getTarget()) < 16.0D) {
                this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50, 0));
                this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 50, 0));
            }
        }
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.age % 14 == 0) {
            serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.REVERSE_PORTAL, this.getX(), this.getBodyY(0.8D), this.getZ(), 2, 0.15D, 0.2D, 0.15D, 0.02D);
            if (this.age % 28 == 0) {
                serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.GLOW, this.getX(), this.getBodyY(0.95D), this.getZ(), 3, 0.2D, 0.3D, 0.2D, 0.01D);
            }
        }
    }
}
