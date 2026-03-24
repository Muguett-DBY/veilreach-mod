package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.VeilboundWatcherEntityModel;
import com.muguett.veilreach.entity.VeilboundWatcherEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public final class VeilboundWatcherEntityRenderer extends MobEntityRenderer<VeilboundWatcherEntity, LivingEntityRenderState, VeilboundWatcherEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/veilbound_watcher.png");

    public VeilboundWatcherEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VeilboundWatcherEntityModel(context.getPart(VeilreachEntityModelLayers.VEILBOUND_WATCHER)), 0.45F);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}
