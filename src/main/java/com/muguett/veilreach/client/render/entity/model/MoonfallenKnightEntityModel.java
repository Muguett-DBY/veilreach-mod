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

public final class MoonfallenKnightEntityModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart mantle;
    private final ModelPart tabard;

    public MoonfallenKnightEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.mantle = this.body.getChild("mantle");
        this.tabard = this.body.getChild("tabard");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -8.0F, -4.5F, 8.0F, 8.0F, 9.0F)
                .uv(34, 0).cuboid(-5.0F, -9.0F, -5.0F, 10.0F, 4.0F, 10.0F)
                .uv(0, 17).cuboid(-2.0F, -4.0F, -6.0F, 4.0F, 3.0F, 1.0F), ModelTransform.origin(0.0F, 3.0F, 0.0F));
        head.addChild("crest_left", ModelPartBuilder.create()
                .uv(10, 17).cuboid(0.0F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F), ModelTransform.of(2.8F, -8.8F, -0.8F, -0.55F, 0.15F, 0.4F));
        head.addChild("crest_right", ModelPartBuilder.create()
                .uv(10, 17).mirrored().cuboid(-1.0F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F), ModelTransform.of(-2.8F, -8.8F, -0.8F, -0.55F, -0.15F, -0.4F));
        head.addChild("back_fin", ModelPartBuilder.create()
                .uv(18, 17).cuboid(-1.5F, -5.0F, 0.0F, 3.0F, 5.0F, 2.0F), ModelTransform.of(0.0F, -8.8F, 3.0F, -0.18F, 0.0F, 0.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 24).cuboid(-5.0F, 0.0F, -3.0F, 10.0F, 12.0F, 6.0F)
                .uv(32, 24).cuboid(-6.0F, 1.0F, -4.0F, 12.0F, 8.0F, 7.0F)
                .uv(0, 42).cuboid(-4.5F, 4.0F, -4.6F, 9.0F, 5.0F, 1.0F), ModelTransform.origin(0.0F, 4.0F, 0.0F));
        body.addChild("mantle", ModelPartBuilder.create()
                .uv(20, 42).cuboid(-6.5F, 0.0F, 0.0F, 13.0F, 16.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 2.8F, 0.24F, 0.0F, 0.0F));
        body.addChild("tabard", ModelPartBuilder.create()
                .uv(0, 59).cuboid(-2.5F, 0.0F, -4.2F, 5.0F, 13.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.06F, 0.0F, 0.0F));
        body.addChild("left_pauldron", ModelPartBuilder.create()
                .uv(48, 42).mirrored().cuboid(0.0F, -1.0F, -3.5F, 3.0F, 6.0F, 7.0F), ModelTransform.of(5.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.22F));
        body.addChild("right_pauldron", ModelPartBuilder.create()
                .uv(48, 42).cuboid(-3.0F, -1.0F, -3.5F, 3.0F, 6.0F, 7.0F), ModelTransform.of(-5.0F, 0.5F, 0.0F, 0.0F, 0.0F, -0.22F));

        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(72, 0).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 15.0F, 4.0F)
                .uv(72, 19).cuboid(-2.6F, 2.0F, -2.6F, 5.0F, 6.0F, 5.0F), ModelTransform.of(-6.5F, 5.0F, 0.0F, -0.08F, 0.0F, 0.12F));
        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(72, 0).mirrored().cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 15.0F, 4.0F)
                .uv(72, 19).mirrored().cuboid(-2.4F, 2.0F, -2.6F, 5.0F, 6.0F, 5.0F), ModelTransform.of(6.5F, 5.0F, 0.0F, -0.3F, 0.0F, -0.12F));
        root.addChild("right_leg", ModelPartBuilder.create()
                .uv(92, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F)
                .uv(92, 18).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 5.0F, 5.0F), ModelTransform.origin(-2.5F, 16.0F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create()
                .uv(92, 0).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F)
                .uv(92, 18).mirrored().cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 5.0F, 5.0F), ModelTransform.origin(2.5F, 16.0F, 0.0F));

        return TexturedModelData.of(modelData, 128, 96);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.56F;
        float stride = state.limbSwingAmplitude * 0.7F;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.6F;
        this.head.pitch = state.pitch * 0.017453292F * 0.32F;
        this.rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.7F * stride - 0.05F;
        this.leftArm.pitch = MathHelper.cos(swing) * 0.7F * stride - 0.3F;
        this.rightLeg.pitch = MathHelper.cos(swing) * 0.82F * stride;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.82F * stride;
        this.mantle.pitch = 0.24F + (MathHelper.cos(swing) * 0.06F * stride);
        this.tabard.pitch = -0.06F + (MathHelper.cos(swing + 0.4F) * 0.12F * stride);
        this.rightArm.yaw = 0.08F;
        this.leftArm.yaw = -0.12F;
    }
}
