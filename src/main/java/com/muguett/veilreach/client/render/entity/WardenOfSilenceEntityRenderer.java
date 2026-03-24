package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.WardenOfSilenceEntityModel;
import com.muguett.veilreach.entity.WardenOfSilenceEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public final class WardenOfSilenceEntityRenderer extends MobEntityRenderer<WardenOfSilenceEntity, LivingEntityRenderState, WardenOfSilenceEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/warden_of_silence.png");

    public WardenOfSilenceEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WardenOfSilenceEntityModel(context.getPart(VeilreachEntityModelLayers.WARDEN_OF_SILENCE)), 0.75F);
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
