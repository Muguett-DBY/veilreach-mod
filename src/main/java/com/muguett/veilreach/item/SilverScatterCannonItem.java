package com.muguett.veilreach.item;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

public final class SilverScatterCannonItem extends AbstractRiftWeaponItem {
    public SilverScatterCannonItem(Settings settings) {
        super(settings, VeilreachItems.HEAVY_CHARGE, 14, "item.veilreach.silver_scatter_cannon.tooltip", "item.veilreach.silver_scatter_cannon.tooltip2");
    }

    @Override
    protected void fire(net.minecraft.world.World world, PlayerEntity user, ItemStack stack) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        for (int i = 0; i < 5; i++) {
            com.muguett.veilreach.entity.RiftWeaponProjectileEntity shard = new com.muguett.veilreach.entity.RiftWeaponProjectileEntity(VeilreachEntities.RIFT_WEAPON_PROJECTILE, serverWorld);
            shard.setPosition(user.getX(), user.getEyeY() - 0.10D, user.getZ());
            shard.setOwner(user);
            shard.setItem(VeilreachItems.SCATTER_SHARD_ITEM.getDefaultStack());
            shard.setVelocity(user, user.getPitch(), user.getYaw() + ((i - 2) * 4.0F), 0.0F, 2.95F, 2.0F);
            serverWorld.spawnEntity(shard);
        }
        serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), VeilreachSoundEvents.SILVER_SCATTER_FIRE, SoundCategory.PLAYERS, 0.88F, 0.96F);
        serverWorld.spawnParticles(ParticleTypes.GLOW, user.getX(), user.getEyeY(), user.getZ(), 14, 0.12D, 0.08D, 0.12D, 0.0D);
        serverWorld.spawnParticles(ParticleTypes.SMOKE, user.getX(), user.getEyeY(), user.getZ(), 10, 0.12D, 0.06D, 0.12D, 0.01D);
    }
}
