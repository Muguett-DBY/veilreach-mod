package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.item.ItemStack;
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
        return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
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
        }
        super.onDeath(damageSource);
    }
}
