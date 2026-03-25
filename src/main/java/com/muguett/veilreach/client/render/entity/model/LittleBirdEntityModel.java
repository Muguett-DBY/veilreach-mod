package com.muguett.veilreach.client.render.entity.model;

import com.muguett.veilreach.client.render.entity.state.LittleBirdRenderState;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModel;

public final class LittleBirdEntityModel extends EntityModel<LittleBirdRenderState> {
    private final ModelPart body;
    private final ModelPart cockpit;
    private final ModelPart mainRotor;
    private final ModelPart tailRotor;

    public LittleBirdEntityModel(ModelPart root) {
        super(root, RenderLayers::entityCutoutNoCull);
        this.body = root.getChild("body");
        this.cockpit = this.body.getChild("cockpit");
        this.mainRotor = this.body.getChild("rotor_mast").getChild("main_rotor");
        this.tailRotor = this.body.getChild("tail_boom").getChild("tail_rotor");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-5.0F, -3.0F, -8.0F, 10.0F, 5.0F, 18.0F)
                .uv(0, 24).cuboid(-4.0F, -4.0F, -5.0F, 8.0F, 1.0F, 10.0F)
                .uv(36, 24).cuboid(-2.0F, -2.0F, 10.0F, 4.0F, 3.0F, 7.0F), ModelTransform.origin(0.0F, 18.0F, 0.0F));

        body.addChild("cockpit", ModelPartBuilder.create()
                .uv(46, 0).cuboid(-4.0F, -4.0F, -9.0F, 8.0F, 5.0F, 8.0F)
                .uv(46, 13).cuboid(-3.0F, -6.0F, -7.0F, 6.0F, 2.0F, 5.0F)
                .uv(70, 0).cuboid(-2.0F, -1.0F, -11.0F, 4.0F, 2.0F, 2.0F), ModelTransform.of(0.0F, -1.0F, 0.0F, -0.08F, 0.0F, 0.0F));

        body.addChild("left_skid", ModelPartBuilder.create()
                .uv(0, 35).cuboid(0.0F, 0.0F, -7.5F, 1.0F, 1.0F, 15.0F)
                .uv(0, 52).cuboid(-1.5F, -5.0F, -6.0F, 1.0F, 6.0F, 1.0F)
                .uv(4, 52).cuboid(-1.5F, -5.0F, 5.0F, 1.0F, 6.0F, 1.0F), ModelTransform.of(4.8F, 5.0F, 0.0F, 0.0F, 0.0F, -0.08F));
        body.addChild("right_skid", ModelPartBuilder.create()
                .uv(0, 35).mirrored().cuboid(-1.0F, 0.0F, -7.5F, 1.0F, 1.0F, 15.0F)
                .uv(0, 52).mirrored().cuboid(0.5F, -5.0F, -6.0F, 1.0F, 6.0F, 1.0F)
                .uv(4, 52).mirrored().cuboid(0.5F, -5.0F, 5.0F, 1.0F, 6.0F, 1.0F), ModelTransform.of(-4.8F, 5.0F, 0.0F, 0.0F, 0.0F, 0.08F));

        body.addChild("left_pod", ModelPartBuilder.create()
                .uv(34, 35).cuboid(0.0F, -1.0F, -5.0F, 2.0F, 3.0F, 10.0F)
                .uv(58, 35).cuboid(2.0F, 0.0F, -3.0F, 2.0F, 1.0F, 2.0F)
                .uv(58, 39).cuboid(2.0F, 0.0F, 1.0F, 2.0F, 1.0F, 2.0F), ModelTransform.of(5.2F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1F));
        body.addChild("right_pod", ModelPartBuilder.create()
                .uv(34, 35).mirrored().cuboid(-2.0F, -1.0F, -5.0F, 2.0F, 3.0F, 10.0F)
                .uv(58, 35).mirrored().cuboid(-4.0F, 0.0F, -3.0F, 2.0F, 1.0F, 2.0F)
                .uv(58, 39).mirrored().cuboid(-4.0F, 0.0F, 1.0F, 2.0F, 1.0F, 2.0F), ModelTransform.of(-5.2F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1F));

        ModelPartData tailBoom = body.addChild("tail_boom", ModelPartBuilder.create()
                .uv(46, 20).cuboid(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 18.0F)
                .uv(70, 4).cuboid(-2.0F, -3.0F, 15.0F, 4.0F, 5.0F, 2.0F)
                .uv(68, 12).cuboid(-0.5F, -6.0F, 15.5F, 1.0F, 5.0F, 1.0F), ModelTransform.of(0.0F, -0.8F, 16.2F, -0.02F, 0.0F, 0.0F));

        tailBoom.addChild("tail_rotor", ModelPartBuilder.create()
                .uv(72, 18).cuboid(-0.5F, -4.0F, -4.0F, 1.0F, 8.0F, 8.0F)
                .uv(90, 18).cuboid(-0.5F, -8.0F, -0.5F, 1.0F, 16.0F, 1.0F), ModelTransform.of(0.0F, -0.8F, 16.8F, 0.0F, 0.0F, 0.0F));

        ModelPartData rotorMast = body.addChild("rotor_mast", ModelPartBuilder.create()
                .uv(32, 49).cuboid(-0.8F, -7.0F, -0.8F, 2.0F, 7.0F, 2.0F)
                .uv(40, 49).cuboid(-2.0F, -8.0F, -2.0F, 4.0F, 1.0F, 4.0F), ModelTransform.origin(0.0F, -4.0F, 2.0F));

        rotorMast.addChild("main_rotor", ModelPartBuilder.create()
                .uv(0, 60).cuboid(-18.0F, -0.5F, -1.0F, 36.0F, 1.0F, 2.0F)
                .uv(0, 56).cuboid(-1.0F, -0.5F, -18.0F, 2.0F, 1.0F, 36.0F)
                .uv(76, 56).cuboid(-2.0F, -0.8F, -2.0F, 4.0F, 2.0F, 4.0F), ModelTransform.origin(0.0F, -8.0F, 0.0F));

        body.addChild("engine_inlets", ModelPartBuilder.create()
                .uv(82, 0).cuboid(-3.0F, -2.2F, 6.5F, 6.0F, 2.0F, 4.0F)
                .uv(82, 6).cuboid(-3.5F, -1.2F, 7.0F, 7.0F, 1.0F, 3.0F), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 128, 96);
    }

    @Override
    public void setAngles(LittleBirdRenderState state) {
        this.resetTransforms();
        this.mainRotor.hidden = state.hideRotors;
        this.tailRotor.hidden = state.hideRotors;
        this.mainRotor.yaw = state.rotorAngle * 0.017453292F;
        this.tailRotor.roll = state.tailRotorAngle * 0.017453292F;
        this.cockpit.pitch = -0.08F + (state.throttle * 0.04F);
        this.body.pitch = state.damaged ? 0.03F : 0.0F;
    }
}
