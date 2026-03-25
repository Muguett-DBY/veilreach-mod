package com.muguett.veilreach.registry;

import com.muguett.veilreach.VeilreachMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class VeilreachItemGroups {
    public static final RegistryKey<ItemGroup> VEILREACH_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(VeilreachMod.MOD_ID, "veilreach"));

    private VeilreachItemGroups() {
    }

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, VEILREACH_GROUP_KEY, FabricItemGroup.builder()
                .icon(() -> new ItemStack(VeilreachItems.ASTRAL_SIGIL))
                .displayName(Text.translatable("itemGroup.veilreach.main"))
                .entries((context, entries) -> {
                    entries.add(VeilreachItems.ASTRAL_SIGIL);
                    entries.add(VeilreachItems.WARDEN_SIGIL);
                    entries.add(VeilreachItems.HUSH_WARD_CHARM);
                    entries.add(VeilreachItems.VEIL_SHARD);
                    entries.add(VeilreachItems.HUSHBERRY);
                    entries.add(VeilreachItems.MOONBROTH_TART);
                    entries.add(VeilreachItems.LUNAR_FIBER);
                    entries.add(VeilreachItems.MIRROR_INGOT);
                    entries.add(VeilreachItems.MIRROR_BLADE);
                    entries.add(VeilreachItems.MIRROR_PICKAXE);
                    entries.add(VeilreachItems.MIRROR_AXE);
                    entries.add(VeilreachItems.MIRROR_SHOVEL);
                    entries.add(VeilreachItems.GRENADE);
                    entries.add(VeilreachItems.IMPACT_GRENADE);
                    entries.add(VeilreachItems.CLUSTER_GRENADE);
                    entries.add(VeilreachItems.MOLOTOV);
                    entries.add(VeilreachItems.LIGHT_ENERGY_CELL);
                    entries.add(VeilreachItems.ENERGY_CELL);
                    entries.add(VeilreachItems.HEAVY_CHARGE);
                    entries.add(VeilreachItems.PULSE_PISTOL);
                    entries.add(VeilreachItems.RIFT_RIFLE);
                    entries.add(VeilreachItems.SILVER_SCATTER_CANNON);
                    entries.add(VeilreachItems.WATCHER_GRENADE_LAUNCHER);
                    entries.add(VeilreachItems.LITTLE_BIRD);
                    entries.add(VeilreachItems.RIFT_SKIMMER);
                    entries.add(VeilreachItems.RIFT_GUNSHIP);
                    entries.add(VeilreachItems.RIFT_SIEGE_PLATFORM);
                    entries.add(VeilreachItems.MIRROR_HELMET);
                    entries.add(VeilreachItems.MIRROR_CHESTPLATE);
                    entries.add(VeilreachItems.MIRROR_LEGGINGS);
                    entries.add(VeilreachItems.MIRROR_BOOTS);
                    entries.add(VeilreachItems.SOVEREIGN_HELMET);
                    entries.add(VeilreachItems.SOVEREIGN_CHESTPLATE);
                    entries.add(VeilreachItems.SOVEREIGN_LEGGINGS);
                    entries.add(VeilreachItems.SOVEREIGN_BOOTS);
                    entries.add(VeilreachItems.SOVEREIGN_GLAIVE);
                    entries.add(VeilreachItems.STARFRAGMENT);
                    entries.add(VeilreachItems.SOVEREIGN_CORE);
                    entries.add(VeilreachItems.SANCTUM_KEY);
                    entries.add(VeilreachItems.CATHEDRAL_SEAL);
                    entries.add(VeilreachItems.SOVEREIGN_WARD);
                    entries.add(VeilreachItems.GLOAMWOOD_SAP);

                    entries.add(VeilreachBlocks.VEILSTONE);
                    entries.add(VeilreachBlocks.POLISHED_VEILSTONE);
                    entries.add(VeilreachBlocks.MIRROR_GRASS);
                    entries.add(VeilreachBlocks.MOONMOSS);
                    entries.add(VeilreachBlocks.GLOAMWOOD_PLANKS);
                    entries.add(VeilreachBlocks.GLOAMWOOD_LOG);
                    entries.add(VeilreachBlocks.SHIMMERLEAF_LEAVES);
                    entries.add(VeilreachBlocks.DUSKCAP);
                    entries.add(VeilreachBlocks.MOONBLOSSOM);
                    entries.add(VeilreachBlocks.RIFT_BLOOM);
                    entries.add(VeilreachBlocks.VEILSTONE_PILE);
                    entries.add(VeilreachBlocks.RIFTGLASS);
                    entries.add(VeilreachBlocks.STARLANTERN);
                    entries.add(VeilreachBlocks.MOONWOVEN_FRAME);
                    entries.add(VeilreachBlocks.SEALED_ALTAR);
                    entries.add(VeilreachBlocks.AWAKENED_ALTAR);
                    entries.add(VeilreachBlocks.FOGBOUND_BARRIER);
                    entries.add(VeilreachBlocks.STARLIGHT_CACHE);
                    entries.add(VeilreachBlocks.VEHICLE_REPAIR_PAD);
                    entries.add(VeilreachBlocks.VEHICLE_REARM_NODE);
                    entries.add(VeilreachBlocks.VEHICLE_RECALL_BEACON);
                    entries.add(VeilreachBlocks.VEHICLE_DEPLOYMENT_PAD);

                    entries.add(VeilreachItems.MIRROR_STAG_SPAWN_EGG);
                    entries.add(VeilreachItems.HUSHLING_SPAWN_EGG);
                    entries.add(VeilreachItems.LANTERNKEEPER_SPAWN_EGG);
                    entries.add(VeilreachItems.MOONFALLEN_KNIGHT_SPAWN_EGG);
                    entries.add(VeilreachItems.HUSH_SOVEREIGN_SPAWN_EGG);
                    entries.add(VeilreachItems.VEILBOUND_WATCHER_SPAWN_EGG);
                    entries.add(VeilreachItems.WARDEN_OF_SILENCE_SPAWN_EGG);
                })
                .build());
    }
}
