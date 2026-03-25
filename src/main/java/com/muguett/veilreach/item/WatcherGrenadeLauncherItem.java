package com.muguett.veilreach.item;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

public final class WatcherGrenadeLauncherItem extends AbstractRiftWeaponItem {
    public WatcherGrenadeLauncherItem(Settings settings) {
        super(settings, VeilreachItems.HEAVY_CHARGE, 24, "item.veilreach.watcher_grenade_launcher.tooltip", "item.veilreach.watcher_grenade_launcher.tooltip2");
    }

    @Override
    protected void fire(net.minecraft.world.World world, PlayerEntity user, ItemStack stack) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        com.muguett.veilreach.entity.RiftWeaponProjectileEntity shell = new com.muguett.veilreach.entity.RiftWeaponProjectileEntity(VeilreachEntities.RIFT_WEAPON_PROJECTILE, serverWorld);
        shell.setPosition(user.getX(), user.getEyeY() - 0.12D, user.getZ());
        shell.setOwner(user);
        shell.setItem(VeilreachItems.WATCHER_SHELL_ITEM.getDefaultStack());
        shell.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.42F, 0.85F);
        serverWorld.spawnEntity(shell);
        serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), VeilreachSoundEvents.WATCHER_LAUNCH, SoundCategory.PLAYERS, 0.9F, 0.88F);
        serverWorld.spawnParticles(ParticleTypes.SMOKE, user.getX(), user.getEyeY(), user.getZ(), 18, 0.16D, 0.08D, 0.16D, 0.01D);
    }
}
