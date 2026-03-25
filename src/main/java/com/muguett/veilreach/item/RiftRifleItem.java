package com.muguett.veilreach.item;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

public final class RiftRifleItem extends AbstractRiftWeaponItem {
    public RiftRifleItem(Settings settings) {
        super(settings, VeilreachItems.ENERGY_CELL, 9, "item.veilreach.rift_rifle.tooltip", "item.veilreach.rift_rifle.tooltip2");
    }

    @Override
    protected void fire(net.minecraft.world.World world, PlayerEntity user, ItemStack stack) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        com.muguett.veilreach.entity.RiftWeaponProjectileEntity bolt = new com.muguett.veilreach.entity.RiftWeaponProjectileEntity(VeilreachEntities.RIFT_WEAPON_PROJECTILE, serverWorld);
        bolt.setPosition(user.getX(), user.getEyeY() - 0.12D, user.getZ());
        bolt.setOwner(user);
        bolt.setItem(VeilreachItems.RIFT_RIFLE_ROUND_ITEM.getDefaultStack());
        bolt.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 4.2F, 0.2F);
        serverWorld.spawnEntity(bolt);
        serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), VeilreachSoundEvents.RIFT_RIFLE_FIRE, SoundCategory.PLAYERS, 0.9F, 1.02F);
        serverWorld.spawnParticles(ParticleTypes.GLOW, user.getX(), user.getEyeY(), user.getZ(), 8, 0.08D, 0.08D, 0.08D, 0.0D);
    }
}
