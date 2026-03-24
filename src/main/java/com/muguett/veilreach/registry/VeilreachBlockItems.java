package com.muguett.veilreach.registry;

import com.muguett.veilreach.VeilreachMod;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class VeilreachBlockItems {
    private VeilreachBlockItems() {
    }

    public static void initialize() {
        register("veilstone", VeilreachBlocks.VEILSTONE);
        register("polished_veilstone", VeilreachBlocks.POLISHED_VEILSTONE);
        register("mirror_grass", VeilreachBlocks.MIRROR_GRASS);
        register("moonmoss", VeilreachBlocks.MOONMOSS);
        register("gloamwood_planks", VeilreachBlocks.GLOAMWOOD_PLANKS);
        register("gloamwood_log", VeilreachBlocks.GLOAMWOOD_LOG);
        register("shimmerleaf_leaves", VeilreachBlocks.SHIMMERLEAF_LEAVES);
        register("moonblossom", VeilreachBlocks.MOONBLOSSOM);
        register("rift_bloom", VeilreachBlocks.RIFT_BLOOM);
        register("riftglass", VeilreachBlocks.RIFTGLASS);
        register("starlantern", VeilreachBlocks.STARLANTERN);
        register("moonwoven_frame", VeilreachBlocks.MOONWOVEN_FRAME);
        register("sealed_altar", VeilreachBlocks.SEALED_ALTAR);
        register("awakened_altar", VeilreachBlocks.AWAKENED_ALTAR);
        register("fogbound_barrier", VeilreachBlocks.FOGBOUND_BARRIER);
        register("starlight_cache", VeilreachBlocks.STARLIGHT_CACHE);
    }

    private static void register(String path, net.minecraft.block.Block block) {
        Identifier id = Identifier.of(VeilreachMod.MOD_ID, path);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings().registryKey(key).useBlockPrefixedTranslationKey()));
    }
}
