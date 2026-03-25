package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class LittleBirdBombEntity extends ThrownItemEntity {
    private static final float PRIMARY_POWER = 4.8F;
    private static final float SECONDARY_POWER = 3.2F;
    private static final float TERTIARY_POWER = 2.15F;
    private static final int PRIMARY_SPLITS = 6;
    private static final int SECONDARY_SPLITS = 2;
    private static final int ARMING_TICKS = 6;

    private int stage;
    private float heading;

    public LittleBirdBombEntity(EntityType<? extends LittleBirdBombEntity> entityType, World world) {
        super(entityType, world);
    }

    public LittleBirdBombEntity(World world, LivingEntity owner, int stage, float heading) {
        super(VeilreachEntities.LITTLE_BIRD_BOMB, owner, world, new ItemStack(stage == 0 ? VeilreachItems.LITTLE_BIRD_BOMB_ITEM : VeilreachItems.LITTLE_BIRD_BOMBLET_ITEM));
        this.stage = stage;
        this.heading = heading;
    }

    @Override
    public void tick() {
        super.tick();
        if (getEntityWorld().isClient()) {
            if ((age & 1) == 0) {
                getEntityWorld().addParticleClient(ParticleTypes.SMOKE, getX(), getY() + 0.02D, getZ(), 0.0D, 0.004D, 0.0D);
            }
            return;
        }

        if (age > 180 && getEntityWorld() instanceof ServerWorld serverWorld) {
            burst(serverWorld);
        }

        if (age > ARMING_TICKS && isOnGround() && getEntityWorld() instanceof ServerWorld serverWorld) {
            burst(serverWorld);
        }
    }

    @Override
    protected double getGravity() {
        return stage == 0 ? 0.055D : 0.05D;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hit = entityHitResult.getEntity();
        Entity owner = getOwner();
        if (hit == owner || (owner != null && hit == owner.getVehicle())) {
            return;
        }
        setVelocity(getVelocity().multiply(0.6D, 0.25D, 0.6D));
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (age > ARMING_TICKS && getEntityWorld() instanceof ServerWorld serverWorld) {
            burst(serverWorld);
        }
    }

    private void burst(ServerWorld world) {
        float blast = switch (stage) {
            case 0 -> PRIMARY_POWER;
            case 1 -> SECONDARY_POWER;
            default -> TERTIARY_POWER;
        };
        world.playSound(null, getX(), getY(), getZ(), stage == 0 ? VeilreachSoundEvents.LITTLE_BIRD_BOMB_IMPACT : VeilreachSoundEvents.LITTLE_BIRD_EXPLOSION, SoundCategory.HOSTILE, 0.95F, 0.88F + (world.random.nextFloat() * 0.12F));
        world.createExplosion(this, getX(), getY(), getZ(), blast, World.ExplosionSourceType.TNT);
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.spawnParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 16, 0.35D, 0.15D, 0.35D, 0.03D);

        if (stage == 0) {
            spawnRadialWave(world, PRIMARY_SPLITS, 1, 0.82D, 0.12D, 0.0F);
        } else if (stage == 1) {
            spawnRadialWave(world, SECONDARY_SPLITS, 2, 0.58D, 0.06D, heading);
        }

        discard();
    }

    private void spawnRadialWave(ServerWorld world, int count, int nextStage, double speed, double lift, float baseHeading) {
        LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;
        for (int index = 0; index < count; index++) {
            float angle = stage == 0
                    ? (float) ((Math.PI * 2.0D * index) / count)
                    : baseHeading + ((index == 0) ? -0.35F : 0.35F);

            LittleBirdBombEntity child = new LittleBirdBombEntity(world, owner, nextStage, angle);
            child.refreshPositionAndAngles(getX(), getY() + 0.12D, getZ(), world.random.nextFloat() * 360.0F, 0.0F);
            child.setVelocity(MathHelper.cos(angle) * speed, lift, MathHelper.sin(angle) * speed);
            world.spawnEntity(child);
            world.spawnParticles(ParticleTypes.SMOKE, child.getX(), child.getY(), child.getZ(), 5, 0.06D, 0.03D, 0.06D, 0.01D);
        }
    }

    @Override
    protected void writeCustomData(WriteView writeView) {
        super.writeCustomData(writeView);
        writeView.putInt("stage", stage);
        writeView.putFloat("heading", heading);
    }

    @Override
    protected void readCustomData(ReadView readView) {
        super.readCustomData(readView);
        this.stage = readView.getInt("stage", 0);
        this.heading = readView.getFloat("heading", 0.0F);
    }

    @Override
    protected Item getDefaultItem() {
        return stage == 0 ? VeilreachItems.LITTLE_BIRD_BOMB_ITEM : VeilreachItems.LITTLE_BIRD_BOMBLET_ITEM;
    }
}
