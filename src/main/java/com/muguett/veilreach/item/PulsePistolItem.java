package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.RiftPulseBoltEntity;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class PulsePistolItem extends AbstractRiftWeaponItem {
    public PulsePistolItem(Settings settings) {
        super(settings, VeilreachItems.LIGHT_ENERGY_CELL, 6, "item.veilreach.pulse_pistol.tooltip", "item.veilreach.pulse_pistol.tooltip2");
    }

    @Override
    protected void fire(World world, PlayerEntity user, ItemStack stack) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        RiftPulseBoltEntity bolt = new RiftPulseBoltEntity(serverWorld, user);
        bolt.setPosition(user.getX(), user.getEyeY() - 0.12D, user.getZ());
        bolt.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 3.6F, 0.35F);
        serverWorld.spawnEntity(bolt);
        serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), VeilreachSoundEvents.RIFT_PULSE_BOLT_FIRE, SoundCategory.PLAYERS, 0.82F, 1.24F);
        serverWorld.spawnParticles(ParticleTypes.END_ROD, user.getX(), user.getEyeY(), user.getZ(), 7, 0.08D, 0.08D, 0.08D, 0.01D);
        serverWorld.spawnParticles(ParticleTypes.GLOW, user.getX(), user.getEyeY(), user.getZ(), 4, 0.06D, 0.06D, 0.06D, 0.0D);
        Vec3d recoil = user.getRotationVector().multiply(-0.08D);
        user.addVelocity(recoil.x, recoil.y * 0.12D, recoil.z);
    }
}
