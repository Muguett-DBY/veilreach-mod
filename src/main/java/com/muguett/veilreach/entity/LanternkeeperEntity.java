package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LanternkeeperEntity extends ZombieEntity {
    public LanternkeeperEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_LANTERN_FALL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_CHAIN_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_LANTERN_BREAK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 0.85F);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && !this.isRemoved()) {
            this.dropStack(serverWorld, new ItemStack(VeilreachBlocks.STARLANTERN));
            if (this.getRandom().nextFloat() < 0.35F) {
                this.dropStack(serverWorld, new ItemStack(VeilreachItems.HUSH_WARD_CHARM));
            }
        }
        super.onDeath(damageSource);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTarget() != null && this.age % 70 == 0) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60, 0));
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 80, 0));
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 50, 0));
            this.playSound(SoundEvents.BLOCK_LANTERN_PLACE, 0.7F, 0.8F);
        }
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.age % 10 == 0) {
            serverWorld.spawnParticles(ParticleTypes.GLOW, this.getX(), this.getBodyY(0.7D), this.getZ(), 1, 0.1D, 0.1D, 0.1D, 0.0D);
            if (this.age % 30 == 0) {
                serverWorld.spawnParticles(ParticleTypes.END_ROD, this.getX(), this.getBodyY(1.0D), this.getZ(), 2, 0.15D, 0.2D, 0.15D, 0.01D);
            }
        }
    }
}
