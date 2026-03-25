package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.AbstractGrenadeEntity;
import com.muguett.veilreach.entity.MolotovEntity;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public final class MolotovItem extends ThrowableGrenadeItem {
    public static final float DEFAULT_THROW_SPEED = 1.18F;

    public MolotovItem(Settings settings) {
        super(settings);
    }

    @Override
    public float getThrowSpeed() {
        return DEFAULT_THROW_SPEED;
    }

    @Override
    public PreparationStyle getPreparationStyle() {
        return PreparationStyle.FIRE;
    }

    @Override
    public TrajectoryStyle getTrajectoryStyle() {
        return TrajectoryStyle.FIRE;
    }

    @Override
    public SoundEvent getPreparationSound() {
        return VeilreachSoundEvents.MOLOTOV_IGNITE;
    }

    @Override
    public float getPreparationSoundVolume() {
        return 0.82F;
    }

    @Override
    public float getPreparationSoundPitch(World world) {
        return 0.92F + (world.random.nextFloat() * 0.08F);
    }

    @Override
    protected AbstractGrenadeEntity createGrenade(ServerWorld world, PlayerEntity player, ItemStack stack, int remainingUseTicks) {
        return new MolotovEntity(world, player, stack);
    }
}
