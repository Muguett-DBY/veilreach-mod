package com.muguett.veilreach.entity;

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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public final class RiftWeaponProjectileEntity extends ThrownItemEntity {
    private static final int PULSE_LIFETIME = 42;
    private static final int RIFLE_LIFETIME = 64;
    private static final int SCATTER_LIFETIME = 46;
    private static final int SHELL_LIFETIME = 84;
    private static final float PULSE_DAMAGE = 5.0F;
    private static final float RIFLE_DAMAGE = 7.5F;
    private static final float SCATTER_DAMAGE = 3.8F;
    private static final float SHELL_DAMAGE = 11.0F;
    private static final float PULSE_BLAST = 0.35F;
    private static final float RIFLE_BLAST = 0.85F;
    private static final float SCATTER_BLAST = 0.55F;
    private static final float SHELL_BLAST = 4.4F;
    private static final double PULSE_GRAVITY = 0.0D;
    private static final double RIFLE_GRAVITY = 0.0D;
    private static final double SCATTER_GRAVITY = 0.0045D;
    private static final double SHELL_GRAVITY = 0.02D;

    private int safetyTicks = 4;

    public RiftWeaponProjectileEntity(EntityType<? extends RiftWeaponProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public RiftWeaponProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(com.muguett.veilreach.registry.VeilreachEntities.RIFT_WEAPON_PROJECTILE, owner, world, stack);
    }

    @Override
    public void tick() {
        super.tick();
        if (safetyTicks > 0) {
            safetyTicks--;
        }

        if (getEntityWorld().isClient()) {
            addClientTrail();
            return;
        }

        if ((age & 1) == 0 && getEntityWorld() instanceof ServerWorld serverWorld) {
            spawnServerTrail(serverWorld);
        }

        if (age > getMaxLifetime()) {
            detonate(null);
        }
    }

    @Override
    protected double getGravity() {
        return getPreset().gravity();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        Entity owner = getOwner();
        if (safetyTicks > 0 && (hitEntity == owner || (owner != null && hitEntity == owner.getVehicle()))) {
            return;
        }
        detonate(hitEntity);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        detonate(null);
    }

    private void addClientTrail() {
        switch (getPreset()) {
            case PULSE -> {
                getEntityWorld().addParticleClient(ParticleTypes.END_ROD, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
                getEntityWorld().addParticleClient(ParticleTypes.GLOW, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
            case RIFLE -> {
                getEntityWorld().addParticleClient(ParticleTypes.GLOW, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
                getEntityWorld().addParticleClient(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
            case SCATTER -> {
                getEntityWorld().addParticleClient(ParticleTypes.GLOW, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
                getEntityWorld().addParticleClient(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0.0D, 0.01D, 0.0D);
            }
            case SHELL -> {
                getEntityWorld().addParticleClient(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0.0D, 0.005D, 0.0D);
                getEntityWorld().addParticleClient(ParticleTypes.CLOUD, getX(), getY(), getZ(), 0.0D, 0.005D, 0.0D);
            }
        }
    }

    private void spawnServerTrail(ServerWorld serverWorld) {
        switch (getPreset()) {
            case PULSE -> {
                serverWorld.spawnParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 2, 0.05D, 0.05D, 0.05D, 0.0D);
                serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 2, 0.05D, 0.05D, 0.05D, 0.0D);
            }
            case RIFLE -> {
                serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 2, 0.04D, 0.04D, 0.04D, 0.0D);
                serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), 2, 0.04D, 0.04D, 0.04D, 0.0D);
            }
            case SCATTER -> {
                serverWorld.spawnParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 4, 0.06D, 0.04D, 0.06D, 0.008D);
                serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), 2, 0.06D, 0.04D, 0.06D, 0.0D);
            }
            case SHELL -> {
                serverWorld.spawnParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 6, 0.09D, 0.06D, 0.09D, 0.01D);
            }
        }
    }

    private void detonate(Entity directHit) {
        if (!(getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        WeaponPreset preset = getPreset();
        if (directHit instanceof LivingEntity livingTarget) {
            livingTarget.damage(serverWorld, getDamageSources().thrown(this, getOwner()), preset.damage());
        }

        serverWorld.playSound(null, getX(), getY(), getZ(), preset.impactSound(), SoundCategory.HOSTILE, preset.soundVolume(), preset.soundPitch());
        if (preset.explosionPower() > 0.0F) {
            serverWorld.createExplosion(this, getX(), getY(), getZ(), preset.explosionPower(), World.ExplosionSourceType.TNT);
        }
        serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), preset.explosionParticles(), 0.08D, 0.08D, 0.08D, 0.0D);
        serverWorld.spawnParticles(ParticleTypes.GLOW, getX(), getY(), getZ(), preset.glowParticles(), 0.15D, 0.1D, 0.15D, 0.01D);
        serverWorld.spawnParticles(ParticleTypes.POOF, getX(), getY(), getZ(), preset.poofParticles(), 0.18D, 0.12D, 0.18D, 0.02D);
        discard();
    }

    private int getMaxLifetime() {
        return switch (getPreset()) {
            case PULSE -> PULSE_LIFETIME;
            case RIFLE -> RIFLE_LIFETIME;
            case SCATTER -> SCATTER_LIFETIME;
            case SHELL -> SHELL_LIFETIME;
        };
    }

    private WeaponPreset getPreset() {
        Item item = getDefaultItem();
        if (item == VeilreachItems.RIFT_RIFLE_ROUND_ITEM) {
            return WeaponPreset.RIFLE;
        }
        if (item == VeilreachItems.SCATTER_SHARD_ITEM) {
            return WeaponPreset.SCATTER;
        }
        if (item == VeilreachItems.WATCHER_SHELL_ITEM) {
            return WeaponPreset.SHELL;
        }
        return WeaponPreset.PULSE;
    }

    @Override
    protected Item getDefaultItem() {
        return getStack().getItem();
    }

    private enum WeaponPreset {
        PULSE(PULSE_DAMAGE, PULSE_BLAST, PULSE_GRAVITY, VeilreachSoundEvents.RIFT_PULSE_BOLT_FIRE, 0.56F, 1.2F, 1, 8, 6),
        RIFLE(RIFLE_DAMAGE, RIFLE_BLAST, RIFLE_GRAVITY, VeilreachSoundEvents.RIFT_RIFLE_FIRE, 0.72F, 1.02F, 2, 9, 7),
        SCATTER(SCATTER_DAMAGE, SCATTER_BLAST, SCATTER_GRAVITY, VeilreachSoundEvents.SILVER_SCATTER_FIRE, 0.68F, 0.92F, 2, 8, 6),
        SHELL(SHELL_DAMAGE, SHELL_BLAST, SHELL_GRAVITY, VeilreachSoundEvents.WATCHER_LAUNCH, 0.88F, 0.82F, 4, 12, 18);

        private final float damage;
        private final float explosionPower;
        private final double gravity;
        private final net.minecraft.sound.SoundEvent impactSound;
        private final float soundVolume;
        private final float soundPitch;
        private final int explosionParticles;
        private final int glowParticles;
        private final int poofParticles;
        WeaponPreset(float damage, float explosionPower, double gravity, net.minecraft.sound.SoundEvent impactSound, float soundVolume, float soundPitch, int explosionParticles, int glowParticles, int poofParticles) {
            this.damage = damage;
            this.explosionPower = explosionPower;
            this.gravity = gravity;
            this.impactSound = impactSound;
            this.soundVolume = soundVolume;
            this.soundPitch = soundPitch;
            this.explosionParticles = explosionParticles;
            this.glowParticles = glowParticles;
            this.poofParticles = poofParticles;
        }

        public float damage() {
            return damage;
        }

        public float explosionPower() {
            return explosionPower;
        }

        public double gravity() {
            return gravity;
        }

        public net.minecraft.sound.SoundEvent impactSound() {
            return impactSound;
        }

        public float soundVolume() {
            return soundVolume;
        }

        public float soundPitch() {
            return soundPitch;
        }

        public int explosionParticles() {
            return explosionParticles;
        }

        public int glowParticles() {
            return glowParticles;
        }

        public int poofParticles() {
            return poofParticles;
        }

    }
}
