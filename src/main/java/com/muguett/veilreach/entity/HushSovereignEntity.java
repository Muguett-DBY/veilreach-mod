package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HushSovereignEntity extends WitherSkeletonEntity {
    public HushSovereignEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WARDEN_STEP, 0.24F, 0.7F);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && !this.isRemoved()) {
            this.dropStack(serverWorld, new ItemStack(VeilreachItems.SOVEREIGN_CORE));
            this.dropStack(serverWorld, new ItemStack(VeilreachItems.STARFRAGMENT, 4));
            serverWorld.spawnParticles(ParticleTypes.END_ROD, this.getX(), this.getBodyY(0.9D), this.getZ(), 48, 0.9D, 1.0D, 0.9D, 0.05D);
            serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getBodyY(0.9D), this.getZ(), 64, 0.8D, 1.2D, 0.8D, 0.03D);
            serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, this.getX(), this.getBodyY(1.0D), this.getZ(), 24, 0.6D, 0.8D, 0.6D, 0.1D);
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, this.getSoundCategory(), 1.0F, 0.7F);
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT, this.getSoundCategory(), 1.0F, 0.6F);
        }
        super.onDeath(damageSource);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.age % 12 == 0) {
            serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getBodyY(0.9D), this.getZ(), 3, 0.2D, 0.3D, 0.2D, 0.01D);
        }
        if (this.getTarget() != null && this.age % 90 == 0) {
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 1));
            this.getTarget().addVelocity(this.getTarget().getX() > this.getX() ? 0.8D : -0.8D, 0.25D, this.getTarget().getZ() > this.getZ() ? 0.8D : -0.8D);
        }
    }
}
