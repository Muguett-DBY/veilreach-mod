package com.muguett.veilreach.client.render.entity.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

public final class LanternkeeperEntityModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart lantern;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public LanternkeeperEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.lantern = root.getChild("lantern");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                .uv(32, 0).cuboid(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F)
                .uv(0, 18).cuboid(-3.0F, -3.0F, -5.5F, 6.0F, 4.0F, 1.0F), ModelTransform.origin(0.0F, 3.5F, 0.0F));
        root.addChild("body", ModelPartBuilder.create()
                .uv(0, 29).cuboid(-4.5F, 0.0F, -2.5F, 9.0F, 12.0F, 5.0F)
                .uv(28, 29).cuboid(-5.0F, 9.0F, -3.0F, 10.0F, 7.0F, 6.0F)
                .uv(20, 20).cuboid(-5.5F, 0.0F, -3.0F, 11.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.08F, 0.0F, 0.0F));
        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(44, 0).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 13.0F, 4.0F), ModelTransform.of(-6.0F, 5.5F, 0.0F, -0.2F, 0.0F, 0.1F));
        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(44, 0).mirrored().cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 13.0F, 4.0F), ModelTransform.of(6.0F, 5.5F, 0.0F, -0.45F, 0.0F, -0.1F));
        root.addChild("lantern", ModelPartBuilder.create()
                .uv(44, 17).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F)
                .uv(56, 17).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F), ModelTransform.origin(6.0F, 16.0F, -2.0F));
        root.addChild("right_leg", ModelPartBuilder.create()
                .uv(0, 46).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.origin(-2.2F, 16.0F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create()
                .uv(0, 46).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.origin(2.2F, 16.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.6F;
        float stride = state.limbSwingAmplitude;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.7F;
        this.head.pitch = state.pitch * 0.017453292F * 0.45F;
        this.body.pitch = 0.08F + MathHelper.cos(state.age * 0.06F) * 0.02F;
        this.rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.55F * stride - 0.2F;
        this.leftArm.pitch = -0.65F + MathHelper.cos(state.age * 0.12F) * 0.05F;
        this.rightLeg.pitch = MathHelper.cos(swing) * 0.9F * stride;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.9F * stride;
        this.lantern.pitch = 0.12F + MathHelper.cos(state.age * 0.18F) * 0.08F;
        this.lantern.roll = MathHelper.cos(state.age * 0.16F) * 0.05F;
    }
}
