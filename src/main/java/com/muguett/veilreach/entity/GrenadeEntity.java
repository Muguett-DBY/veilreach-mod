package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public final class GrenadeEntity extends AbstractGrenadeEntity {
    public static final float DEFAULT_EXPLOSION_POWER = 2.8F;
    private int fuseTicks;

    public GrenadeEntity(EntityType<GrenadeEntity> entityType, World world) {
        super(entityType, world);
        this.fuseTicks = 1;
    }

    public GrenadeEntity(World world, LivingEntity owner, ItemStack stack, int fuseTicks) {
        super(VeilreachEntities.GRENADE, owner, world, stack);
        this.fuseTicks = Math.max(1, fuseTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (getEntityWorld().isClient()) {
            if (age % 2 == 0) {
                getEntityWorld().addParticleClient(ParticleTypes.SMOKE, getX(), getY() + 0.1D, getZ(), 0.0D, 0.005D, 0.0D);
            }
            return;
        }

        fuseTicks--;
        if (fuseTicks <= 0) {
            explode(DEFAULT_EXPLOSION_POWER);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.GRENADE;
    }
}
