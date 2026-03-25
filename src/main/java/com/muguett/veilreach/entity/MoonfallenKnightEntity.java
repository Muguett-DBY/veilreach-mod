package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoonfallenKnightEntity extends WitherSkeletonEntity {
    public MoonfallenKnightEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE.value();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ITEM_SHIELD_BLOCK.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_STEP, 0.18F, 0.9F);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && !this.isRemoved()) {
            if (this.getRandom().nextFloat() < 0.25F) {
                this.dropStack(serverWorld, new ItemStack(VeilreachItems.MIRROR_AXE));
            }
            if (this.getRandom().nextFloat() < 0.8F) {
                this.dropStack(serverWorld, new ItemStack(VeilreachItems.STARFRAGMENT, 2 + this.getRandom().nextInt(2)));
            }
            if (this.getRandom().nextFloat() < 0.3F) {
                this.dropStack(serverWorld, new ItemStack(VeilreachItems.MIRROR_INGOT, 1 + this.getRandom().nextInt(2)));
            }
            serverWorld.spawnParticles(ParticleTypes.END_ROD, this.getX(), this.getBodyY(0.85D), this.getZ(), 18, 0.35D, 0.45D, 0.35D, 0.03D);
            serverWorld.spawnParticles(ParticleTypes.SOUL, this.getX(), this.getBodyY(0.8D), this.getZ(), 12, 0.25D, 0.35D, 0.25D, 0.02D);
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, this.getSoundCategory(), 0.8F, 0.7F);
        }
        super.onDeath(damageSource);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.age % 16 == 0) {
            serverWorld.spawnParticles(ParticleTypes.ENCHANT, this.getX(), this.getBodyY(0.8D), this.getZ(), 3, 0.22D, 0.35D, 0.22D, 0.01D);
        }
        if (this.getTarget() != null && this.age % 70 == 0 && this.squaredDistanceTo(this.getTarget()) < 49.0D) {
            this.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.SPEED, 50, 1, false, false));
            this.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.RESISTANCE, 36, 0, false, false));
            double dx = this.getTarget().getX() - this.getX();
            double dz = this.getTarget().getZ() - this.getZ();
            double scale = Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));
            this.addVelocity(dx / scale * 0.65D, 0.14D, dz / scale * 0.65D);
            this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 0.7F, 0.55F);
        }
    }
}
