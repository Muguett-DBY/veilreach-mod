package com.muguett.veilreach.registry;

import com.muguett.veilreach.VeilreachMod;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class VeilreachToolMaterials {
    public static final TagKey<net.minecraft.item.Item> REPAIRS_MIRRORSTEEL = TagKey.of(RegistryKeys.ITEM, Identifier.of(VeilreachMod.MOD_ID, "repairs_mirrorsteel"));
    public static final ToolMaterial MIRRORSTEEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            462,
            7.0F,
            2.5F,
            17,
            REPAIRS_MIRRORSTEEL
    );
    public static final ToolMaterial SOVEREIGN = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            1180,
            8.8F,
            4.0F,
            22,
            REPAIRS_MIRRORSTEEL
    );

    private VeilreachToolMaterials() {
    }
}
