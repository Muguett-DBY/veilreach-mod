package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.AbstractGrenadeEntity;
import com.muguett.veilreach.entity.ImpactGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public final class ImpactGrenadeItem extends ThrowableGrenadeItem {
    public static final float DEFAULT_THROW_SPEED = 1.18F;

    public ImpactGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public float getThrowSpeed() {
        return DEFAULT_THROW_SPEED;
    }

    @Override
    protected AbstractGrenadeEntity createGrenade(ServerWorld world, PlayerEntity player, ItemStack stack, int remainingUseTicks) {
        return new ImpactGrenadeEntity(world, player, stack);
    }
}
