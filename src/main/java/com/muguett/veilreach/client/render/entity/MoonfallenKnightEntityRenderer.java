package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.MoonfallenKnightEntityModel;
import com.muguett.veilreach.entity.MoonfallenKnightEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public final class MoonfallenKnightEntityRenderer extends MobEntityRenderer<MoonfallenKnightEntity, LivingEntityRenderState, MoonfallenKnightEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/moonfallen_knight.png");

    public MoonfallenKnightEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MoonfallenKnightEntityModel(context.getPart(VeilreachEntityModelLayers.MOONFALLEN_KNIGHT)), 0.72F);
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
