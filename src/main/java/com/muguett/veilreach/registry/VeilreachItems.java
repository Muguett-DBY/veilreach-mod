package com.muguett.veilreach.registry;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.item.AstralSigilItem;
import com.muguett.veilreach.item.ClusterGrenadeItem;
import com.muguett.veilreach.item.GrenadeItem;
import com.muguett.veilreach.item.HushWardCharmItem;
import com.muguett.veilreach.item.ImpactGrenadeItem;
import com.muguett.veilreach.item.LoreHintItem;
import com.muguett.veilreach.item.MolotovItem;
import com.muguett.veilreach.item.SovereignWardItem;
import com.muguett.veilreach.item.VeilreachSpawnEggItem;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ShovelItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public final class VeilreachItems {
    public static final Item VEIL_SHARD = register("veil_shard", Item::new);
    public static final Item HUSHBERRY = register("hushberry", settings -> new Item(settings.food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.35F).build())));
    public static final Item MOONBROTH_TART = register("moonbroth_tart", settings -> new Item(settings.food(new FoodComponent.Builder().nutrition(8).saturationModifier(0.85F).build())));
    public static final Item LUNAR_FIBER = register("lunar_fiber", settings -> new LoreHintItem(settings, "item.veilreach.lunar_fiber.tooltip"));
    public static final Item MIRROR_INGOT = register("mirror_ingot", settings -> new LoreHintItem(settings, "item.veilreach.mirror_ingot.tooltip"));
    public static final Item MIRROR_BLADE = register("mirror_blade", settings -> new Item(VeilreachToolMaterials.MIRRORSTEEL.applySwordSettings(settings, 3.0F, -2.4F)));
    public static final Item MIRROR_PICKAXE = register("mirror_pickaxe", settings -> new Item(VeilreachToolMaterials.MIRRORSTEEL.applyToolSettings(settings, net.minecraft.registry.tag.BlockTags.PICKAXE_MINEABLE, 1.0F, -2.8F, 0.0F)));
    public static final Item MIRROR_AXE = register("mirror_axe", settings -> new AxeItem(VeilreachToolMaterials.MIRRORSTEEL, 5.5F, -3.0F, settings));
    public static final Item MIRROR_SHOVEL = register("mirror_shovel", settings -> new ShovelItem(VeilreachToolMaterials.MIRRORSTEEL, 1.5F, -3.0F, settings));
    public static final Item GRENADE = register("grenade", GrenadeItem::new);
    public static final Item IMPACT_GRENADE = register("impact_grenade", ImpactGrenadeItem::new);
    public static final Item CLUSTER_GRENADE = register("cluster_grenade", ClusterGrenadeItem::new);
    public static final Item MOLOTOV = register("molotov", MolotovItem::new);
    public static final Item MIRROR_HELMET = register("mirror_helmet", settings -> new Item(settings.armor(VeilreachArmorMaterials.MIRRORSTEEL, EquipmentType.HELMET).maxDamage(EquipmentType.HELMET.getMaxDamage(VeilreachArmorMaterials.MIRRORSTEEL.durability()))));
    public static final Item MIRROR_CHESTPLATE = register("mirror_chestplate", settings -> new Item(settings.armor(VeilreachArmorMaterials.MIRRORSTEEL, EquipmentType.CHESTPLATE).maxDamage(EquipmentType.CHESTPLATE.getMaxDamage(VeilreachArmorMaterials.MIRRORSTEEL.durability()))));
    public static final Item MIRROR_LEGGINGS = register("mirror_leggings", settings -> new Item(settings.armor(VeilreachArmorMaterials.MIRRORSTEEL, EquipmentType.LEGGINGS).maxDamage(EquipmentType.LEGGINGS.getMaxDamage(VeilreachArmorMaterials.MIRRORSTEEL.durability()))));
    public static final Item MIRROR_BOOTS = register("mirror_boots", settings -> new Item(settings.armor(VeilreachArmorMaterials.MIRRORSTEEL, EquipmentType.BOOTS).maxDamage(EquipmentType.BOOTS.getMaxDamage(VeilreachArmorMaterials.MIRRORSTEEL.durability()))));
    public static final Item SOVEREIGN_HELMET = register("sovereign_helmet", settings -> new Item(settings.armor(VeilreachArmorMaterials.SOVEREIGN, EquipmentType.HELMET).maxDamage(EquipmentType.HELMET.getMaxDamage(VeilreachArmorMaterials.SOVEREIGN.durability()))));
    public static final Item SOVEREIGN_CHESTPLATE = register("sovereign_chestplate", settings -> new Item(settings.armor(VeilreachArmorMaterials.SOVEREIGN, EquipmentType.CHESTPLATE).maxDamage(EquipmentType.CHESTPLATE.getMaxDamage(VeilreachArmorMaterials.SOVEREIGN.durability()))));
    public static final Item SOVEREIGN_LEGGINGS = register("sovereign_leggings", settings -> new Item(settings.armor(VeilreachArmorMaterials.SOVEREIGN, EquipmentType.LEGGINGS).maxDamage(EquipmentType.LEGGINGS.getMaxDamage(VeilreachArmorMaterials.SOVEREIGN.durability()))));
    public static final Item SOVEREIGN_BOOTS = register("sovereign_boots", settings -> new Item(settings.armor(VeilreachArmorMaterials.SOVEREIGN, EquipmentType.BOOTS).maxDamage(EquipmentType.BOOTS.getMaxDamage(VeilreachArmorMaterials.SOVEREIGN.durability()))));
    public static final Item SOVEREIGN_GLAIVE = register("sovereign_glaive", settings -> new Item(VeilreachToolMaterials.SOVEREIGN.applySwordSettings(settings, 5.0F, -2.7F)));
    public static final Item STARFRAGMENT = register("starfragment", settings -> new LoreHintItem(settings.rarity(Rarity.UNCOMMON), true, "item.veilreach.starfragment.tooltip"));
    public static final Item SOVEREIGN_CORE = register("sovereign_core", settings -> new LoreHintItem(settings.rarity(Rarity.EPIC), true, "item.veilreach.sovereign_core.tooltip", "item.veilreach.sovereign_core.tooltip2"));
    public static final Item SANCTUM_KEY = register("sanctum_key", settings -> new LoreHintItem(settings.rarity(Rarity.UNCOMMON), true, "item.veilreach.sanctum_key.tooltip"));
    public static final Item CATHEDRAL_SEAL = register("cathedral_seal", settings -> new LoreHintItem(settings.rarity(Rarity.UNCOMMON), true, "item.veilreach.cathedral_seal.tooltip"));
    public static final Item WARDEN_SIGIL = register("warden_sigil", settings -> new LoreHintItem(settings.rarity(Rarity.RARE), true, "item.veilreach.warden_sigil.tooltip", "item.veilreach.warden_sigil.tooltip2"));
    public static final Item HUSH_WARD_CHARM = register("hush_ward_charm", HushWardCharmItem::new);
    public static final Item SOVEREIGN_WARD = register("sovereign_ward", SovereignWardItem::new);
    public static final Item ASTRAL_SIGIL = register("astral_sigil", settings -> new AstralSigilItem(settings.rarity(Rarity.UNCOMMON)));
    public static final Item GLOAMWOOD_SAP = register("gloamwood_sap", Item::new);
    public static final Item MIRROR_STAG_SPAWN_EGG = registerSpawnEgg("mirror_stag_spawn_egg", VeilreachEntities.MIRROR_STAG);
    public static final Item HUSHLING_SPAWN_EGG = registerSpawnEgg("hushling_spawn_egg", VeilreachEntities.HUSHLING);
    public static final Item LANTERNKEEPER_SPAWN_EGG = registerSpawnEgg("lanternkeeper_spawn_egg", VeilreachEntities.LANTERNKEEPER);
    public static final Item MOONFALLEN_KNIGHT_SPAWN_EGG = registerSpawnEgg("moonfallen_knight_spawn_egg", VeilreachEntities.MOONFALLEN_KNIGHT);
    public static final Item HUSH_SOVEREIGN_SPAWN_EGG = registerSpawnEgg("hush_sovereign_spawn_egg", VeilreachEntities.HUSH_SOVEREIGN);
    public static final Item VEILBOUND_WATCHER_SPAWN_EGG = registerSpawnEgg("veilbound_watcher_spawn_egg", VeilreachEntities.VEILBOUND_WATCHER);
    public static final Item WARDEN_OF_SILENCE_SPAWN_EGG = registerSpawnEgg("warden_of_silence_spawn_egg", VeilreachEntities.WARDEN_OF_SILENCE);

    private VeilreachItems() {
    }

    public static void initialize() {
    }

    private static Item register(String path, Function<Item.Settings, Item> factory) {
        Identifier id = Identifier.of(VeilreachMod.MOD_ID, path);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = factory.apply(new Item.Settings().registryKey(key));
        return Registry.register(Registries.ITEM, id, item);
    }

    private static Item registerSpawnEgg(String path, net.minecraft.entity.EntityType<?> entityType) {
        return register(path, settings -> new VeilreachSpawnEggItem(entityType, settings));
    }
}
