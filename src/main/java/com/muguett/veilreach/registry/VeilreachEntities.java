package com.muguett.veilreach.registry;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.entity.HushlingEntity;
import com.muguett.veilreach.entity.HushSovereignEntity;
import com.muguett.veilreach.entity.GrenadeEntity;
import com.muguett.veilreach.entity.ImpactGrenadeEntity;
import com.muguett.veilreach.entity.LanternkeeperEntity;
import com.muguett.veilreach.entity.MoonfallenKnightEntity;
import com.muguett.veilreach.entity.MirrorStagEntity;
import com.muguett.veilreach.entity.VeilboundWatcherEntity;
import com.muguett.veilreach.entity.WardenOfSilenceEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class VeilreachEntities {
    public static final EntityType<MirrorStagEntity> MIRROR_STAG = register("mirror_stag",
            EntityType.Builder.create(MirrorStagEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.4F));
    public static final EntityType<GrenadeEntity> GRENADE = register("grenade",
            EntityType.Builder.<GrenadeEntity>create(GrenadeEntity::new, SpawnGroup.MISC).dimensions(0.25F, 0.25F).maxTrackingRange(6).trackingTickInterval(1));
    public static final EntityType<ImpactGrenadeEntity> IMPACT_GRENADE = register("impact_grenade",
            EntityType.Builder.<ImpactGrenadeEntity>create(ImpactGrenadeEntity::new, SpawnGroup.MISC).dimensions(0.25F, 0.25F).maxTrackingRange(6).trackingTickInterval(1));

    public static final EntityType<HushlingEntity> HUSHLING = register("hushling",
            EntityType.Builder.create(HushlingEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F));
    public static final EntityType<LanternkeeperEntity> LANTERNKEEPER = register("lanternkeeper",
            EntityType.Builder.create(LanternkeeperEntity::new, SpawnGroup.MONSTER).dimensions(0.7F, 2.2F));
    public static final EntityType<MoonfallenKnightEntity> MOONFALLEN_KNIGHT = register("moonfallen_knight",
            EntityType.Builder.create(MoonfallenKnightEntity::new, SpawnGroup.MONSTER).dimensions(0.75F, 2.45F).maxTrackingRange(12));
    public static final EntityType<HushSovereignEntity> HUSH_SOVEREIGN = register("hush_sovereign",
            EntityType.Builder.create(HushSovereignEntity::new, SpawnGroup.MONSTER).dimensions(0.95F, 3.2F).maxTrackingRange(14));

    public static final EntityType<VeilboundWatcherEntity> VEILBOUND_WATCHER = register("veilbound_watcher",
            EntityType.Builder.create(VeilboundWatcherEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 2.9F));

    public static final EntityType<WardenOfSilenceEntity> WARDEN_OF_SILENCE = register("warden_of_silence",
            EntityType.Builder.create(WardenOfSilenceEntity::new, SpawnGroup.MONSTER).dimensions(0.7F, 2.4F).maxTrackingRange(12));

    private VeilreachEntities() {
    }

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(MIRROR_STAG, CowEntity.createCowAttributes());
        FabricDefaultAttributeRegistry.register(HUSHLING, ZombieEntity.createZombieAttributes());
        FabricDefaultAttributeRegistry.register(LANTERNKEEPER, lanternkeeperAttributes());
        FabricDefaultAttributeRegistry.register(MOONFALLEN_KNIGHT, moonfallenKnightAttributes());
        FabricDefaultAttributeRegistry.register(HUSH_SOVEREIGN, hushSovereignAttributes());
        FabricDefaultAttributeRegistry.register(VEILBOUND_WATCHER, EndermanEntity.createEndermanAttributes());
        FabricDefaultAttributeRegistry.register(WARDEN_OF_SILENCE, silenceWardenAttributes());
    }

    private static DefaultAttributeContainer.Builder lanternkeeperAttributes() {
        return ZombieEntity.createZombieAttributes()
                .add(EntityAttributes.MAX_HEALTH, 36.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 7.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.29D)
                .add(EntityAttributes.FOLLOW_RANGE, 28.0D)
                .add(EntityAttributes.ARMOR, 4.0D);
    }

    private static DefaultAttributeContainer.Builder silenceWardenAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 80.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 9.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.32D)
                .add(EntityAttributes.FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    private static DefaultAttributeContainer.Builder moonfallenKnightAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 52.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 8.5D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.31D)
                .add(EntityAttributes.FOLLOW_RANGE, 30.0D)
                .add(EntityAttributes.ARMOR, 8.0D)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.35D);
    }

    private static DefaultAttributeContainer.Builder hushSovereignAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 160.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 14.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.33D)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0D)
                .add(EntityAttributes.ARMOR, 12.0D)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.75D);
    }

    private static <T extends net.minecraft.entity.Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
        Identifier id = Identifier.of(VeilreachMod.MOD_ID, path);
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
        return Registry.register(Registries.ENTITY_TYPE, id, builder.build(key));
    }
}
