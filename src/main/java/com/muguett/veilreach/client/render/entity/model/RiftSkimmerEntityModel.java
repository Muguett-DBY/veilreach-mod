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

public final class RiftSkimmerEntityModel extends EntityModel<HoverVehicleRenderState> {
    private final ModelPart root;
    private final ModelPart fins;

    public RiftSkimmerEntityModel(ModelPart root) {
        super(root, RenderLayers::entityCutoutNoCull);
        this.root = root.getChild("body");
        this.fins = this.root.getChild("fins");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-7.0F, -2.0F, -8.0F, 14.0F, 3.0F, 16.0F)
                .uv(0, 19).cuboid(-5.0F, -4.0F, -4.0F, 10.0F, 2.0F, 8.0F)
                .uv(0, 29).cuboid(-3.0F, -1.0F, -11.0F, 6.0F, 2.0F, 3.0F), ModelTransform.origin(0.0F, 21.0F, 0.0F));
        body.addChild("fins", ModelPartBuilder.create()
                .uv(44, 0).cuboid(-10.0F, -1.0F, -4.0F, 3.0F, 1.0F, 8.0F)
                .uv(44, 9).cuboid(7.0F, -1.0F, -4.0F, 3.0F, 1.0F, 8.0F)
                .uv(44, 18).cuboid(-2.0F, -1.0F, 8.0F, 4.0F, 1.0F, 5.0F), ModelTransform.NONE);
        body.addChild("engine_glow", ModelPartBuilder.create()
                .uv(24, 19).cuboid(-4.0F, 0.0F, 3.0F, 8.0F, 1.0F, 3.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(HoverVehicleRenderState state) {
        this.resetTransforms();
        this.fins.roll = state.critical ? 0.06F : 0.0F;
        this.root.pitch = state.damaged ? 0.03F : 0.0F;
    }
}
