package com.muguett.veilreach.client.render.entity.model;

import com.muguett.veilreach.client.render.entity.state.LittleBirdRocketRenderState;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModel;

public final class LittleBirdRocketEntityModel extends EntityModel<LittleBirdRocketRenderState> {
    private final ModelPart body;

    public LittleBirdRocketEntityModel(ModelPart root) {
        super(root, RenderLayers::entityCutoutNoCull);
        this.body = root.getChild("body");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 2.0F, 12.0F)
                .uv(0, 14).cuboid(-0.5F, -0.5F, -8.5F, 1.0F, 1.0F, 3.0F)
                .uv(12, 14).cuboid(-1.5F, -1.5F, 4.0F, 3.0F, 3.0F, 2.0F), ModelTransform.NONE);

        body.addChild("fin_top", ModelPartBuilder.create()
                .uv(0, 19).cuboid(-0.5F, -4.0F, 3.5F, 1.0F, 4.0F, 4.0F), ModelTransform.NONE);
        body.addChild("fin_bottom", ModelPartBuilder.create()
                .uv(10, 19).cuboid(-0.5F, 0.0F, 3.5F, 1.0F, 4.0F, 4.0F), ModelTransform.NONE);
        body.addChild("fin_left", ModelPartBuilder.create()
                .uv(20, 19).cuboid(-4.0F, -0.5F, 3.5F, 4.0F, 1.0F, 4.0F), ModelTransform.NONE);
        body.addChild("fin_right", ModelPartBuilder.create()
                .uv(20, 24).cuboid(0.0F, -0.5F, 3.5F, 4.0F, 1.0F, 4.0F), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(LittleBirdRocketRenderState state) {
        this.resetTransforms();
        this.body.roll = state.roll;
    }
}
