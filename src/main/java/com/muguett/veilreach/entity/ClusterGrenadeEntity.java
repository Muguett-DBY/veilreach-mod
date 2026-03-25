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
    public static final float PRIMARY_BLAST_POWER = 7.0F;
    public static final float CASCADE_BLAST_POWER = 4.8F;
    public static final float TERMINAL_BLAST_POWER = 3.4F;
    private static final int MAX_SPLIT_WAVES = 10;
    private static final int WAVE_BOMBLET_COUNT = 8;
    private static final int NEXT_WAVE_FUSE_TICKS = 18;
    private static final int BOMBLET_FUSE_TICKS = 12;

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

        boolean carrierWave = stage < MAX_SPLIT_WAVES;
        float blastPower = stage == 0 ? PRIMARY_BLAST_POWER : carrierWave ? CASCADE_BLAST_POWER : TERMINAL_BLAST_POWER;
        world.createExplosion(this, getX(), getY(), getZ(), blastPower, World.ExplosionSourceType.TNT);
        world.spawnParticles(carrierWave ? ParticleTypes.FLAME : ParticleTypes.EXPLOSION, getX(), getY() + 0.12D, getZ(), 16, 0.26D, 0.12D, 0.26D, 0.01D);
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.08D, getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);

        if (carrierWave) {
            spawnWaveBomblets(world);
            if (stage + 1 < MAX_SPLIT_WAVES) {
                spawnNextWaveCarrier(world);
            }
        }

        discard();
    }

    private void spawnWaveBomblets(ServerWorld world) {
        LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;
        for (int i = 0; i < WAVE_BOMBLET_COUNT; i++) {
            ClusterGrenadeEntity child = new ClusterGrenadeEntity(world, owner, new ItemStack(VeilreachItems.CLUSTER_GRENADE), MAX_SPLIT_WAVES, BOMBLET_FUSE_TICKS + world.random.nextInt(6));
            child.refreshPositionAndAngles(getX(), getY() + 0.08D, getZ(), world.random.nextFloat() * 360.0F, 0.0F);
            double angle = (Math.PI * 2.0D * i) / WAVE_BOMBLET_COUNT + (world.random.nextDouble() * 0.16D);
            double speed = 0.56D + (world.random.nextDouble() * 0.22D);
            double lift = 0.36D + (world.random.nextDouble() * 0.14D);
            child.setVelocity(MathHelper.cos((float) angle) * speed, lift, MathHelper.sin((float) angle) * speed);
            world.spawnEntity(child);
            world.spawnParticles(ParticleTypes.SMOKE, child.getX(), child.getY(), child.getZ(), 8, 0.1D, 0.1D, 0.1D, 0.01D);
            world.spawnParticles(ParticleTypes.FLAME, child.getX(), child.getY(), child.getZ(), 5, 0.06D, 0.06D, 0.06D, 0.005D);
        }
    }

    private void spawnNextWaveCarrier(ServerWorld world) {
        LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;
        ClusterGrenadeEntity carrier = new ClusterGrenadeEntity(world, owner, new ItemStack(VeilreachItems.CLUSTER_GRENADE), stage + 1, NEXT_WAVE_FUSE_TICKS + world.random.nextInt(6));
        carrier.refreshPositionAndAngles(getX(), getY() + 0.08D, getZ(), world.random.nextFloat() * 360.0F, 0.0F);
        double verticalBoost = 0.58D + (world.random.nextDouble() * 0.12D);
        double lateralDrift = 0.08D + (world.random.nextDouble() * 0.08D);
        double angle = world.random.nextDouble() * Math.PI * 2.0D;
        carrier.setVelocity(MathHelper.cos((float) angle) * lateralDrift, verticalBoost, MathHelper.sin((float) angle) * lateralDrift);
        world.spawnEntity(carrier);
        world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, carrier.getX(), carrier.getY(), carrier.getZ(), 6, 0.04D, 0.08D, 0.04D, 0.01D);
    }

    @Override
    protected Item getDefaultItem() {
        return VeilreachItems.CLUSTER_GRENADE;
    }
}
