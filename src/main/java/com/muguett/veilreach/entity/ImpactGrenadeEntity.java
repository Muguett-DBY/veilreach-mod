package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public final class ImpactGrenadeEntity extends AbstractGrenadeEntity {
    public static final float DEFAULT_EXPLOSION_POWER = 2.6F;

    public ImpactGrenadeEntity(EntityType<ImpactGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public ImpactGrenadeEntity(World world, LivingEntity owner, ItemStack stack) {
        super(VeilreachEntities.IMPACT_GRENADE, owner, world, stack);
    }

    @Override
    protected void handleBlockHit(BlockHitResult hitResult) {
        if (isSafetyExpired()) {
            explode(DEFAULT_EXPLOSION_POWER);
            return;
        }
        super.handleBlockHit(hitResult);
    }

    @Override
    protected void handleEntityHit(EntityHitResult hitResult) {
        if (isSafetyExpired() && hitResult.getEntity() != getOwner()) {
            explode(DEFAULT_EXPLOSION_POWER);
            return;
        }
        super.handleEntityHit(hitResult);
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.IMPACT_GRENADE;
    }
}
