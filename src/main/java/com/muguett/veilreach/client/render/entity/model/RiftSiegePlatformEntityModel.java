package com.muguett.veilreach.client.render.entity.model;

import com.muguett.veilreach.client.render.entity.state.HoverVehicleRenderState;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModel;

public final class RiftSiegePlatformEntityModel extends EntityModel<HoverVehicleRenderState> {
    private final ModelPart platform;
    private final ModelPart cannon;

    public RiftSiegePlatformEntityModel(ModelPart root) {
        super(root, RenderLayers::entityCutoutNoCull);
        this.platform = root.getChild("platform");
        this.cannon = this.platform.getChild("cannon");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData platform = root.addChild("platform", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-9.0F, -3.0F, -9.0F, 18.0F, 4.0F, 18.0F)
                .uv(0, 22).cuboid(-7.0F, -5.0F, -7.0F, 14.0F, 2.0F, 14.0F)
                .uv(0, 38).cuboid(-5.0F, 1.0F, -5.0F, 10.0F, 2.0F, 10.0F), ModelTransform.origin(0.0F, 19.0F, 0.0F));
        platform.addChild("cannon", ModelPartBuilder.create()
                .uv(40, 22).cuboid(-2.0F, -2.0F, -13.0F, 4.0F, 4.0F, 9.0F)
                .uv(44, 35).cuboid(-4.0F, -3.0F, -5.0F, 8.0F, 5.0F, 5.0F), ModelTransform.of(0.0F, -4.0F, -4.0F, -0.08F, 0.0F, 0.0F));
        platform.addChild("pods", ModelPartBuilder.create()
                .uv(40, 0).cuboid(-11.0F, -2.0F, -4.0F, 2.0F, 3.0F, 8.0F)
                .uv(52, 0).cuboid(9.0F, -2.0F, -4.0F, 2.0F, 3.0F, 8.0F)
                .uv(40, 11).cuboid(-4.0F, -1.0F, 9.0F, 8.0F, 2.0F, 2.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 80, 48);
    }

    @Override
    public void setAngles(HoverVehicleRenderState state) {
        this.resetTransforms();
        this.platform.pitch = state.damaged ? 0.02F : 0.0F;
        this.cannon.pitch = state.critical ? -0.12F : -0.04F;
    }
}
