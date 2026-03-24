package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.LanternkeeperEntityModel;
import com.muguett.veilreach.entity.LanternkeeperEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public final class LanternkeeperEntityRenderer extends MobEntityRenderer<LanternkeeperEntity, LivingEntityRenderState, LanternkeeperEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/lanternkeeper.png");

    public LanternkeeperEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new LanternkeeperEntityModel(context.getPart(VeilreachEntityModelLayers.LANTERNKEEPER)), 0.6F);
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
