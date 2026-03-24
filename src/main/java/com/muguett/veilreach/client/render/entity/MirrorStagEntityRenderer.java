package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.MirrorStagEntityModel;
import com.muguett.veilreach.entity.MirrorStagEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public final class MirrorStagEntityRenderer extends MobEntityRenderer<MirrorStagEntity, LivingEntityRenderState, MirrorStagEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/mirror_stag.png");

    public MirrorStagEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MirrorStagEntityModel(context.getPart(VeilreachEntityModelLayers.MIRROR_STAG)), 0.7F);
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
