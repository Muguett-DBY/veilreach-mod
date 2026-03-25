package com.muguett.veilreach;

import com.muguett.veilreach.client.GrenadeHudOverlay;
import com.muguett.veilreach.client.GrenadeTrajectoryPreview;
import com.muguett.veilreach.client.HushlandsClientEffects;
import com.muguett.veilreach.client.HushlandsHudOverlay;
import com.muguett.veilreach.client.render.entity.HushlingEntityRenderer;
import com.muguett.veilreach.client.render.entity.HushSovereignEntityRenderer;
import com.muguett.veilreach.client.render.entity.LanternkeeperEntityRenderer;
import com.muguett.veilreach.client.render.entity.MoonfallenKnightEntityRenderer;
import com.muguett.veilreach.client.render.entity.MirrorStagEntityRenderer;
import com.muguett.veilreach.client.render.entity.VeilboundWatcherEntityRenderer;
import com.muguett.veilreach.client.render.entity.VeilreachEntityModelLayers;
import com.muguett.veilreach.client.render.entity.WardenOfSilenceEntityRenderer;
import com.muguett.veilreach.client.render.entity.model.HushlingEntityModel;
import com.muguett.veilreach.client.render.entity.model.HushSovereignEntityModel;
import com.muguett.veilreach.client.render.entity.model.LanternkeeperEntityModel;
import com.muguett.veilreach.client.render.entity.model.MoonfallenKnightEntityModel;
import com.muguett.veilreach.client.render.entity.model.MirrorStagEntityModel;
import com.muguett.veilreach.client.render.entity.model.VeilboundWatcherEntityModel;
import com.muguett.veilreach.client.render.entity.model.WardenOfSilenceEntityModel;
import com.muguett.veilreach.registry.VeilreachEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public final class VeilreachModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HushlandsClientEffects.initialize();
        HushlandsHudOverlay.initialize();
        GrenadeHudOverlay.initialize();
        GrenadeTrajectoryPreview.initialize();
        EntityModelLayerRegistry.registerModelLayer(VeilreachEntityModelLayers.MIRROR_STAG, MirrorStagEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(VeilreachEntityModelLayers.HUSHLING, HushlingEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(VeilreachEntityModelLayers.HUSH_SOVEREIGN, HushSovereignEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(VeilreachEntityModelLayers.LANTERNKEEPER, LanternkeeperEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(VeilreachEntityModelLayers.MOONFALLEN_KNIGHT, MoonfallenKnightEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(VeilreachEntityModelLayers.VEILBOUND_WATCHER, VeilboundWatcherEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(VeilreachEntityModelLayers.WARDEN_OF_SILENCE, WardenOfSilenceEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(VeilreachEntities.MIRROR_STAG, MirrorStagEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.GRENADE, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.IMPACT_GRENADE, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.CLUSTER_GRENADE, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.MOLOTOV, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.HUSHLING, HushlingEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.HUSH_SOVEREIGN, HushSovereignEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.LANTERNKEEPER, LanternkeeperEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.MOONFALLEN_KNIGHT, MoonfallenKnightEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.VEILBOUND_WATCHER, VeilboundWatcherEntityRenderer::new);
        EntityRendererRegistry.register(VeilreachEntities.WARDEN_OF_SILENCE, WardenOfSilenceEntityRenderer::new);
        VeilreachMod.LOGGER.info("Veilreach client initialized.");
    }
}
