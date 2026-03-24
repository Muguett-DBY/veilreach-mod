package com.muguett.veilreach.registry;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.block.FogboundBarrierBlock;
import com.muguett.veilreach.block.HushLanternBlock;
import com.muguett.veilreach.block.HushPortalBlock;
import com.muguett.veilreach.block.MoonwovenFrameBlock;
import com.muguett.veilreach.block.SealedAltarBlock;
import com.muguett.veilreach.block.StarlightCacheBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class VeilreachBlocks {
    public static final Block VEILSTONE = register("veilstone", settings -> new Block(settings.mapColor(MapColor.DEEPSLATE_GRAY).strength(2.2F, 7.0F).sounds(BlockSoundGroup.DEEPSLATE)));
    public static final Block POLISHED_VEILSTONE = register("polished_veilstone", settings -> new Block(settings.mapColor(MapColor.GRAY).strength(2.6F, 7.5F).sounds(BlockSoundGroup.DEEPSLATE)));
    public static final Block MIRROR_GRASS = register("mirror_grass", settings -> new Block(settings.mapColor(MapColor.TEAL).strength(0.8F).sounds(BlockSoundGroup.GRASS)));
    public static final Block MOONMOSS = register("moonmoss", settings -> new Block(settings.mapColor(MapColor.PALE_GREEN).strength(0.2F).sounds(BlockSoundGroup.MOSS_BLOCK)));
    public static final Block GLOAMWOOD_PLANKS = register("gloamwood_planks", settings -> new Block(settings.mapColor(MapColor.DARK_AQUA).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block GLOAMWOOD_LOG = register("gloamwood_log", settings -> new Block(settings.mapColor(MapColor.BLACK).strength(2.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block SHIMMERLEAF_LEAVES = register("shimmerleaf_leaves", settings -> new Block(settings.mapColor(MapColor.PALE_PURPLE).strength(0.2F).sounds(BlockSoundGroup.AZALEA_LEAVES).nonOpaque().burnable()));
    public static final Block DUSKCAP = register("duskcap", settings -> new Block(settings.mapColor(MapColor.DARK_AQUA).strength(0.0F).sounds(BlockSoundGroup.FUNGUS).nonOpaque().noCollision().replaceable().burnable()));
    public static final Block MOONBLOSSOM = register("moonblossom", settings -> new Block(settings.mapColor(MapColor.PALE_PURPLE).strength(0.0F).sounds(BlockSoundGroup.GRASS).nonOpaque().noCollision().replaceable().burnable()));
    public static final Block RIFT_BLOOM = register("rift_bloom", settings -> new Block(settings.mapColor(MapColor.LIGHT_BLUE).strength(0.0F).sounds(BlockSoundGroup.AMETHYST_CLUSTER).nonOpaque().noCollision().replaceable()));
    public static final Block VEILSTONE_PILE = register("veilstone_pile", settings -> new Block(settings.mapColor(MapColor.GRAY).strength(0.2F).sounds(BlockSoundGroup.TUFF).nonOpaque().noCollision().replaceable()));
    public static final Block RIFTGLASS = register("riftglass", settings -> new Block(settings.mapColor(MapColor.LIGHT_BLUE_GRAY).strength(0.4F).sounds(BlockSoundGroup.GLASS).nonOpaque()));
    public static final Block STARLANTERN = register("starlantern", settings -> new HushLanternBlock(settings.mapColor(MapColor.OFF_WHITE).strength(1.2F).luminance(state -> 13).sounds(BlockSoundGroup.LANTERN).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)));
    public static final Block MOONWOVEN_FRAME = register("moonwoven_frame", settings -> new MoonwovenFrameBlock(settings.mapColor(MapColor.DARK_AQUA).strength(4.0F, 14.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool()));
    public static final Block HUSH_PORTAL = register("hush_portal", settings -> new HushPortalBlock(settings.mapColor(MapColor.LIGHT_BLUE).strength(-1.0F, 3600000.0F).dropsNothing().luminance(state -> 11).sounds(BlockSoundGroup.AMETHYST_CLUSTER).noCollision().nonOpaque().pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block SEALED_ALTAR = register("sealed_altar", settings -> new SealedAltarBlock(settings.mapColor(MapColor.LIGHT_BLUE_GRAY).strength(4.2F, 12.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool().luminance(state -> 7)));
    public static final Block AWAKENED_ALTAR = register("awakened_altar", settings -> new Block(settings.mapColor(MapColor.OFF_WHITE).strength(4.2F, 12.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool().luminance(state -> 12)));
    public static final Block FOGBOUND_BARRIER = register("fogbound_barrier", settings -> new FogboundBarrierBlock(settings.mapColor(MapColor.GRAY).strength(5.0F, 18.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool().luminance(state -> 5)));
    public static final Block STARLIGHT_CACHE = register("starlight_cache", settings -> new StarlightCacheBlock(settings.mapColor(MapColor.OFF_WHITE).strength(3.8F, 10.0F).sounds(BlockSoundGroup.AMETHYST_CLUSTER).requiresTool().luminance(state -> 10)));

    private VeilreachBlocks() {
    }

    public static void initialize() {
    }

    private static Block register(String path, Function<AbstractBlock.Settings, Block> factory) {
        Identifier id = Identifier.of(VeilreachMod.MOD_ID, path);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.STONE).registryKey(key);
        Block block = factory.apply(settings);
        return Registry.register(Registries.BLOCK, id, block);
    }
}
