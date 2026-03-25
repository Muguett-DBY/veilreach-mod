package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.AbstractGrenadeEntity;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class ThrowableGrenadeItem extends Item {
    public enum PreparationStyle {
        SMOKE,
        SPARK,
        FIRE
    }

    public enum TrajectoryStyle {
        DELAYED,
        IMPACT,
        FIRE
    }

    protected ThrowableGrenadeItem(Settings settings) {
        super(settings.maxCount(16));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            SoundEvent sound = getPreparationSound();
            if (sound != null) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), sound, SoundCategory.PLAYERS, getPreparationSoundVolume(), getPreparationSoundPitch(world));
            }
        }
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player) || stack.isEmpty()) {
            return false;
        }

        throwGrenade(world, player, stack, remainingUseTicks);
        return true;
    }

    protected void throwGrenade(World world, PlayerEntity player, ItemStack stack, int remainingUseTicks) {
        if (world instanceof ServerWorld serverWorld) {
            AbstractGrenadeEntity grenade = createGrenade(serverWorld, player, stack.copyWithCount(1), remainingUseTicks);
            grenade.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, getThrowSpeed(), 1.0F);
            serverWorld.spawnEntity(grenade);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.55F, 0.8F + (world.random.nextFloat() * 0.2F));
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!player.isCreative()) {
            stack.decrement(1);
        }
    }

    public float getThrowSpeed() {
        return 1.1F;
    }

    public PreparationStyle getPreparationStyle() {
        return PreparationStyle.SMOKE;
    }

    public TrajectoryStyle getTrajectoryStyle() {
        return showsFuseHud() ? TrajectoryStyle.DELAYED : TrajectoryStyle.IMPACT;
    }

    public SoundEvent getPreparationSound() {
        return VeilreachSoundEvents.GRENADE_PIN_PULL;
    }

    public float getPreparationSoundVolume() {
        return 0.72F;
    }

    public float getPreparationSoundPitch(World world) {
        return 0.94F + (world.random.nextFloat() * 0.12F);
    }

    public boolean showsFuseHud() {
        return false;
    }

    public int getFuseTicks() {
        return 0;
    }

    protected abstract AbstractGrenadeEntity createGrenade(ServerWorld world, PlayerEntity player, ItemStack stack, int remainingUseTicks);
}
