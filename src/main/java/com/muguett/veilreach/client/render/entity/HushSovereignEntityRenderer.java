package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.HushSovereignEntityModel;
import com.muguett.veilreach.entity.HushSovereignEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public final class HushSovereignEntityRenderer extends MobEntityRenderer<HushSovereignEntity, LivingEntityRenderState, HushSovereignEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/hush_sovereign.png");

    public HushSovereignEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HushSovereignEntityModel(context.getPart(VeilreachEntityModelLayers.HUSH_SOVEREIGN)), 0.92F);
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
