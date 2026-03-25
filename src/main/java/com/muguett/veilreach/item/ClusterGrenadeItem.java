package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.AbstractGrenadeEntity;
import com.muguett.veilreach.entity.ClusterGrenadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public final class ClusterGrenadeItem extends ThrowableGrenadeItem {
    public static final int DEFAULT_FUSE_TICKS = 70;
    public static final float DEFAULT_THROW_SPEED = 1.24F;

    public ClusterGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient() || stack.isEmpty()) {
            return;
        }

        int cookedTicks = getMaxUseTime(stack, user) - remainingUseTicks;
        if (cookedTicks < DEFAULT_FUSE_TICKS) {
            return;
        }

        explodeInHand(world, user, stack);
    }

    private void explodeInHand(World world, LivingEntity user, ItemStack stack) {
        world.createExplosion(user, user.getX(), user.getBodyY(0.5D), user.getZ(), ClusterGrenadeEntity.PRIMARY_BLAST_POWER, World.ExplosionSourceType.TNT);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS, 1.0F, 0.9F);
        if (user instanceof PlayerEntity player && !player.isCreative()) {
            stack.decrement(1);
        }
        user.stopUsingItem();
    }

    @Override
    public float getThrowSpeed() {
        return DEFAULT_THROW_SPEED;
    }

    @Override
    public PreparationStyle getPreparationStyle() {
        return PreparationStyle.SMOKE;
    }

    @Override
    public TrajectoryStyle getTrajectoryStyle() {
        return TrajectoryStyle.DELAYED;
    }

    @Override
    public boolean showsFuseHud() {
        return true;
    }

    @Override
    public int getFuseTicks() {
        return DEFAULT_FUSE_TICKS;
    }

    @Override
    protected AbstractGrenadeEntity createGrenade(ServerWorld world, PlayerEntity player, ItemStack stack, int remainingUseTicks) {
        int cookedTicks = getMaxUseTime(stack, player) - remainingUseTicks;
        int remainingFuse = Math.max(1, DEFAULT_FUSE_TICKS - cookedTicks);
        return new ClusterGrenadeEntity(world, player, stack, 0, remainingFuse);
    }
}
