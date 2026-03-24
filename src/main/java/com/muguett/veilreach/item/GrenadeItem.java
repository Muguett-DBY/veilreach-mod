package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.AbstractGrenadeEntity;
import com.muguett.veilreach.entity.GrenadeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public final class GrenadeItem extends ThrowableGrenadeItem {
    public static final int DEFAULT_FUSE_TICKS = 60;
    public static final float DEFAULT_THROW_SPEED = 1.28F;

    public GrenadeItem(Settings settings) {
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
        world.createExplosion(user, user.getX(), user.getBodyY(0.5D), user.getZ(), GrenadeEntity.DEFAULT_EXPLOSION_POWER, World.ExplosionSourceType.TNT);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS, 0.95F, 0.94F);
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
        return new GrenadeEntity(world, player, stack, remainingFuse);
    }
}
