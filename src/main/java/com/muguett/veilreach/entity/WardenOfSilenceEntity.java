package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WardenOfSilenceEntity extends WitherSkeletonEntity {
    public WardenOfSilenceEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WARDEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WARDEN_STEP, 0.2F, 0.85F);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && !this.isRemoved()) {
            this.dropStack(serverWorld, new ItemStack(VeilreachItems.WARDEN_SIGIL));
        }
        super.onDeath(damageSource);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTarget() != null && this.age % 80 == 0) {
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 0));
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0));
        }
    }
}
