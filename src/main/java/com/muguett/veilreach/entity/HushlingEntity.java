package com.muguett.veilreach.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HushlingEntity extends ZombieEntity {
    public HushlingEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_STRAY_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_STRAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HUSK_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_HUSK_STEP, 0.15F, 0.9F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.age % 12 == 0) {
            serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.SOUL, this.getX(), this.getBodyY(0.5D), this.getZ(), 2, 0.18D, 0.25D, 0.18D, 0.01D);
        }
        if (this.getTarget() != null && this.age % 60 == 0 && this.squaredDistanceTo(this.getTarget()) < 6.25D) {
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 0));
        }
        if (this.getTarget() != null && this.age % 70 == 0 && this.squaredDistanceTo(this.getTarget()) < 16.0D) {
            double dx = this.getTarget().getX() - this.getX();
            double dz = this.getTarget().getZ() - this.getZ();
            double scale = 0.18D / Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));
            this.addVelocity(dx * scale, 0.22D, dz * scale);
            this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.4F, 0.65F);
        }
    }
}
