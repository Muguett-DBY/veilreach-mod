package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public final class VeilreachEntityModelLayers {
    public static final EntityModelLayer MIRROR_STAG = create("mirror_stag");
    public static final EntityModelLayer HUSHLING = create("hushling");
    public static final EntityModelLayer LANTERNKEEPER = create("lanternkeeper");
    public static final EntityModelLayer LITTLE_BIRD = create("little_bird");
    public static final EntityModelLayer LITTLE_BIRD_ROCKET = create("little_bird_rocket");
    public static final EntityModelLayer RIFT_SKIMMER = create("rift_skimmer");
    public static final EntityModelLayer RIFT_GUNSHIP = create("rift_gunship");
    public static final EntityModelLayer RIFT_SIEGE_PLATFORM = create("rift_siege_platform");
    public static final EntityModelLayer MOONFALLEN_KNIGHT = create("moonfallen_knight");
    public static final EntityModelLayer HUSH_SOVEREIGN = create("hush_sovereign");
    public static final EntityModelLayer VEILBOUND_WATCHER = create("veilbound_watcher");
    public static final EntityModelLayer WARDEN_OF_SILENCE = create("warden_of_silence");

    private VeilreachEntityModelLayers() {
    }

    private static EntityModelLayer create(String path) {
        return new EntityModelLayer(Identifier.of(VeilreachMod.MOD_ID, path), "main");
    }
}
