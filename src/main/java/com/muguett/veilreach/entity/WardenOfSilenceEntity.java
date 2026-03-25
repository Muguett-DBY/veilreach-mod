package com.muguett.veilreach.entity;

import com.muguett.veilreach.progress.HushlandsProgression;
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
            if (damageSource.getAttacker() instanceof net.minecraft.server.network.ServerPlayerEntity player) {
                HushlandsProgression.markWardenDefeated(player);
                HushlandsProgression.sync(player, true);
            }
            serverWorld.spawnParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getBodyY(0.85D), this.getZ(), 36, 0.5D, 0.8D, 0.5D, 0.06D);
            serverWorld.spawnParticles(ParticleTypes.END_ROD, this.getX(), this.getBodyY(1.1D), this.getZ(), 20, 0.35D, 0.65D, 0.35D, 0.04D);
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BEACON_DEACTIVATE, this.getSoundCategory(), 0.95F, 0.65F);
        }
        super.onDeath(damageSource);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.age % 14 == 0) {
            serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getBodyY(0.85D), this.getZ(), 3, 0.25D, 0.35D, 0.25D, 0.01D);
        }
        if (this.getTarget() != null && this.age % 80 == 0) {
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 70, 0));
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 70, 0));
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 45, 0));
            double dx = this.getTarget().getX() - this.getX();
            double dz = this.getTarget().getZ() - this.getZ();
            double scale = Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));
            this.getTarget().addVelocity(dx / scale * 0.75D, 0.18D, dz / scale * 0.75D);
            this.playSound(SoundEvents.ENTITY_WARDEN_NEARBY_CLOSER, 0.75F, 0.6F);
        }
        if (this.getTarget() != null && this.age % 120 == 0 && this.squaredDistanceTo(this.getTarget()) > 16.0D) {
            double dx = this.getTarget().getX() - this.getX();
            double dz = this.getTarget().getZ() - this.getZ();
            double scale = Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));
            this.addVelocity(dx / scale * 0.7D, 0.3D, dz / scale * 0.7D);
            this.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 0.6F, 0.8F);
        }
    }
}
