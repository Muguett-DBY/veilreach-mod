package com.muguett.veilreach.entity;

import com.muguett.veilreach.progress.HushlandsProgression;
import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
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
        return VeilreachSoundEvents.HUSH_SOVEREIGN_PULSE;
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
            this.dropStack(serverWorld, new ItemStack(VeilreachItems.SOVEREIGN_WARD));
            this.dropStack(serverWorld, new ItemStack(VeilreachItems.STARFRAGMENT, 4));
            if (damageSource.getAttacker() instanceof net.minecraft.server.network.ServerPlayerEntity player) {
                HushlandsProgression.markSovereignDefeated(player);
                HushlandsProgression.sync(player, true);
            }
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
        float healthRatio = this.getHealth() / this.getMaxHealth();
        int phase = healthRatio < 0.34F ? 3 : (healthRatio < 0.67F ? 2 : 1);
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.age % 12 == 0) {
            serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getBodyY(0.9D), this.getZ(), 3, 0.2D, 0.3D, 0.2D, 0.01D);
            if (this.age % 24 == 0) {
                serverWorld.spawnParticles(ParticleTypes.GLOW, this.getX(), this.getBodyY(1.1D), this.getZ(), 4, 0.35D, 0.45D, 0.35D, 0.01D);
            }
        }
        if (phase >= 2 && this.age % 100 == 0 && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            HushlingEntity hushling = VeilreachEntities.HUSHLING.spawn(serverWorld, this.getBlockPos().add(this.random.nextBetween(-2, 2), 0, this.random.nextBetween(-2, 2)), net.minecraft.entity.SpawnReason.EVENT);
            if (hushling != null) {
                hushling.setPersistent();
            }
        }
        if (phase == 3 && this.age % 120 == 0 && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            VeilboundWatcherEntity watcher = VeilreachEntities.VEILBOUND_WATCHER.spawn(serverWorld, this.getBlockPos().add(this.random.nextBetween(-3, 3), 0, this.random.nextBetween(-3, 3)), net.minecraft.entity.SpawnReason.EVENT);
            if (watcher != null) {
                watcher.setPersistent();
            }
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 80, 1, false, false));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 80, 0, false, false));
        }
        if (this.getTarget() != null && this.age % (phase == 1 ? 90 : (phase == 2 ? 65 : 50)) == 0) {
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, phase - 1));
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60 + (phase * 20), 0));
            if (phase >= 2) {
                this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, phase - 2));
            }
            this.getTarget().addVelocity(this.getTarget().getX() > this.getX() ? 0.8D : -0.8D, 0.25D, this.getTarget().getZ() > this.getZ() ? 0.8D : -0.8D);
            this.playSound(VeilreachSoundEvents.HUSH_SOVEREIGN_PULSE, 0.95F, 0.72F);
        }
    }
}
