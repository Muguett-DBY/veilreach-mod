package com.muguett.veilreach.entity;

import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class ClusterGrenadeEntity extends AbstractGrenadeEntity {
    public static final float PRIMARY_BLAST_POWER = 4.8F;
    public static final float SECONDARY_BLAST_POWER = 3.4F;
    public static final float FINAL_BLAST_POWER = 2.3F;
    private static final int PRIMARY_CHILDREN = 3;
    private static final int SECONDARY_CHILDREN = 2;
    private static final int SECONDARY_FUSE_TICKS = 12;
    private static final int FINAL_FUSE_TICKS = 18;

    private int stage;
    private int fuseTicks;

    public ClusterGrenadeEntity(EntityType<ClusterGrenadeEntity> entityType, World world) {
        super(entityType, world);
        this.stage = 0;
        this.fuseTicks = 1;
    }

    public ClusterGrenadeEntity(World world, LivingEntity owner, ItemStack stack, int stage, int fuseTicks) {
        super(VeilreachEntities.CLUSTER_GRENADE, owner, world, stack);
        this.stage = stage;
        this.fuseTicks = Math.max(1, fuseTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (getEntityWorld().isClient()) {
            if ((age & 1) == 0) {
                getEntityWorld().addParticleClient(stage >= 2 ? ParticleTypes.CRIT : ParticleTypes.SMOKE, getX(), getY() + 0.1D, getZ(), 0.0D, 0.005D, 0.0D);
            }
            return;
        }

        fuseTicks--;
        if (fuseTicks <= 0) {
            burst();
        }
    }

    private void burst() {
        if (!(getEntityWorld() instanceof ServerWorld world)) {
            return;
        }

        float blastPower = stage == 0 ? PRIMARY_BLAST_POWER : stage == 1 ? SECONDARY_BLAST_POWER : FINAL_BLAST_POWER;
        world.createExplosion(this, getX(), getY(), getZ(), blastPower, World.ExplosionSourceType.TNT);
        world.spawnParticles(stage >= 2 ? ParticleTypes.FLAME : ParticleTypes.SMOKE, getX(), getY() + 0.12D, getZ(), 8, 0.12D, 0.08D, 0.12D, 0.01D);

        if (stage == 0) {
            spawnChildren(world, PRIMARY_CHILDREN, 1, SECONDARY_FUSE_TICKS, 0.38D);
        } else if (stage == 1) {
            spawnChildren(world, SECONDARY_CHILDREN, 2, FINAL_FUSE_TICKS, 0.30D);
        }

        discard();
    }

    private void spawnChildren(ServerWorld world, int count, int nextStage, int childFuse, double horizontalSpeed) {
        LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;
        for (int i = 0; i < count; i++) {
            ClusterGrenadeEntity child = new ClusterGrenadeEntity(world, owner, new ItemStack(VeilreachItems.CLUSTER_GRENADE), nextStage, childFuse + world.random.nextInt(5));
            child.refreshPositionAndAngles(getX(), getY() + 0.08D, getZ(), world.random.nextFloat() * 360.0F, 0.0F);
            double angle = (Math.PI * 2.0D * i) / count + (world.random.nextDouble() * 0.35D);
            double speed = horizontalSpeed + (world.random.nextDouble() * 0.12D);
            child.setVelocity(MathHelper.cos((float) angle) * speed, 0.24D + (world.random.nextDouble() * 0.12D), MathHelper.sin((float) angle) * speed);
            world.spawnEntity(child);
            world.spawnParticles(ParticleTypes.SMOKE, child.getX(), child.getY(), child.getZ(), 5, 0.06D, 0.06D, 0.06D, 0.01D);
            world.spawnParticles(ParticleTypes.FLAME, child.getX(), child.getY(), child.getZ(), 3, 0.04D, 0.04D, 0.04D, 0.005D);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.CLUSTER_GRENADE;
    }
}
