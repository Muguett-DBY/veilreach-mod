package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class MolotovEntity extends AbstractGrenadeEntity {
    public static final float DIRECT_IMPACT_DAMAGE = 0.6F;
    public static final int FIRE_ZONE_DURATION_TICKS = 80;
    public static final float FIRE_ZONE_RADIUS = 2.5F;

    public MolotovEntity(EntityType<MolotovEntity> entityType, World world) {
        super(entityType, world);
    }

    public MolotovEntity(World world, LivingEntity owner, ItemStack stack) {
        super(VeilreachEntities.MOLOTOV, owner, world, stack);
    }

    @Override
    public void tick() {
        super.tick();
        if (getEntityWorld().isClient() && (age & 1) == 0) {
            getEntityWorld().addParticleClient(ParticleTypes.SMALL_FLAME, getX(), getY() + 0.05D, getZ(), 0.0D, 0.01D, 0.0D);
            getEntityWorld().addParticleClient(ParticleTypes.SMOKE, getX(), getY() + 0.08D, getZ(), 0.0D, 0.01D, 0.0D);
        }
    }

    @Override
    protected void handleBlockHit(BlockHitResult hitResult) {
        if (isSafetyExpired()) {
            shatter(null);
            return;
        }
        super.handleBlockHit(hitResult);
    }

    @Override
    protected void handleEntityHit(EntityHitResult hitResult) {
        if (isSafetyExpired() && hitResult.getEntity() != getOwner()) {
            shatter(hitResult.getEntity());
            return;
        }
        super.handleEntityHit(hitResult);
    }

    private void shatter(Entity directTarget) {
        if (!(getEntityWorld() instanceof ServerWorld world)) {
            return;
        }

        if (directTarget instanceof LivingEntity livingTarget) {
            livingTarget.damage(world, getDamageSources().thrown(this, getOwner()), DIRECT_IMPACT_DAMAGE);
            livingTarget.setOnFireFor(4);
        }

        world.playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.85F, 0.84F + (world.random.nextFloat() * 0.12F));
        world.spawnParticles(ParticleTypes.SMALL_FLAME, getX(), getY() + 0.1D, getZ(), 18, 0.35D, 0.2D, 0.35D, 0.015D);
        world.spawnParticles(ParticleTypes.SMOKE, getX(), getY() + 0.08D, getZ(), 12, 0.28D, 0.15D, 0.28D, 0.01D);
        igniteZone(world);
        discard();
    }

    private void igniteZone(ServerWorld world) {
        Vec3d center = new Vec3d(getX(), getY(), getZ());
        Box box = new Box(center, center).expand(FIRE_ZONE_RADIUS, 1.25D, FIRE_ZONE_RADIUS);
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, box, entity -> entity.isAlive() && entity != getOwner())) {
            entity.damage(world, getDamageSources().inFire(), DIRECT_IMPACT_DAMAGE);
            entity.setOnFireFor(4);
        }

        BlockState fireState = Blocks.FIRE.getDefaultState();
        BlockPos centerPos = BlockPos.ofFloored(center);
        int radius = (int) Math.ceil(FIRE_ZONE_RADIUS);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (Math.sqrt((dx * dx) + (dz * dz)) > FIRE_ZONE_RADIUS) {
                    continue;
                }

                BlockPos candidate = centerPos.add(dx, 0, dz);
                if (!world.getBlockState(candidate).isAir()) {
                    candidate = candidate.up();
                }
                if (!world.getBlockState(candidate).isAir()) {
                    continue;
                }
                if (!fireState.canPlaceAt(world, candidate)) {
                    continue;
                }

                world.setBlockState(candidate, fireState);
                world.scheduleBlockTick(candidate, Blocks.FIRE, FIRE_ZONE_DURATION_TICKS);
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.MOLOTOV;
    }
}
