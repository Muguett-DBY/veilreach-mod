package com.muguett.veilreach.registry;

import com.muguett.veilreach.VeilreachMod;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class VeilreachArmorMaterials {
    public static final RegistryKey<net.minecraft.item.equipment.EquipmentAsset> MIRRORSTEEL_ASSET = EquipmentAssetKeys.register(Identifier.of(VeilreachMod.MOD_ID, "mirrorsteel").getPath());
    public static final TagKey<Item> REPAIRS_MIRRORSTEEL_ARMOR = VeilreachToolMaterials.REPAIRS_MIRRORSTEEL;
    public static final ArmorMaterial MIRRORSTEEL = new ArmorMaterial(
            23,
            Map.of(
                    EquipmentType.HELMET, 3,
                    EquipmentType.CHESTPLATE, 8,
                    EquipmentType.LEGGINGS, 6,
                    EquipmentType.BOOTS, 3
            ),
            19,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            2.5F,
            0.05F,
            REPAIRS_MIRRORSTEEL_ARMOR,
            MIRRORSTEEL_ASSET
    );

    private VeilreachArmorMaterials() {
    }
}
