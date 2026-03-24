package com.muguett.veilreach.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractGrenadeEntity extends ThrownItemEntity {
    protected static final int DEFAULT_SAFETY_TICKS = 4;
    private int safetyTicks = DEFAULT_SAFETY_TICKS;

    protected AbstractGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    protected AbstractGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, LivingEntity owner, World world, ItemStack stack) {
        super(entityType, owner, world, stack);
    }

    @Override
    public void tick() {
        super.tick();
        if (safetyTicks > 0) {
            safetyTicks--;
        }
        if (isOnGround()) {
            setVelocity(getVelocity().multiply(0.7D, 0.45D, 0.7D));
        }
    }

    protected boolean isSafetyExpired() {
        return safetyTicks <= 0;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        handleBlockHit(blockHitResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        handleEntityHit(entityHitResult);
    }

    protected void handleBlockHit(BlockHitResult hitResult) {
        bounce(hitResult.getSide());
    }

    protected void handleEntityHit(EntityHitResult hitResult) {
        Vec3d away = new Vec3d(getX() - hitResult.getEntity().getX(), getY() - hitResult.getEntity().getY(), getZ() - hitResult.getEntity().getZ()).normalize().multiply(0.18D);
        setVelocity(getVelocity().multiply(0.35D).add(away));
    }

    protected void bounce(Direction side) {
        Vec3d velocity = getVelocity();
        double x = side.getAxis() == Direction.Axis.X ? -velocity.x * 0.45D : velocity.x * 0.7D;
        double y = side.getAxis() == Direction.Axis.Y ? -velocity.y * 0.38D : velocity.y * 0.75D;
        double z = side.getAxis() == Direction.Axis.Z ? -velocity.z * 0.45D : velocity.z * 0.7D;
        setVelocity(x, y, z);
    }

    protected void explode(float power) {
        if (getEntityWorld() instanceof ServerWorld serverWorld) {
            serverWorld.createExplosion(this, getX(), getY(), getZ(), power, World.ExplosionSourceType.TNT);
            discard();
        }
    }

    @Override
    protected abstract Item getDefaultItem();
}
