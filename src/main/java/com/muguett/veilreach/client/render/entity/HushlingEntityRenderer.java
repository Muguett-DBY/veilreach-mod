package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.HushlingEntityModel;
import com.muguett.veilreach.entity.HushlingEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public final class HushlingEntityRenderer extends MobEntityRenderer<HushlingEntity, LivingEntityRenderState, HushlingEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/hushling.png");

    public HushlingEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HushlingEntityModel(context.getPart(VeilreachEntityModelLayers.HUSHLING)), 0.55F);
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
