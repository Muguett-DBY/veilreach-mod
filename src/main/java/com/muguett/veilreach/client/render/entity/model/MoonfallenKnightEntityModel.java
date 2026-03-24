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

    public MoonfallenKnightEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.mantle = this.body.getChild("mantle");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                .uv(32, 0).cuboid(-4.5F, -9.0F, -4.5F, 9.0F, 4.0F, 9.0F)
                .uv(0, 16).cuboid(-1.5F, -4.0F, -5.2F, 3.0F, 3.0F, 1.0F), ModelTransform.origin(0.0F, 3.0F, 0.0F));
        head.addChild("crest_left", ModelPartBuilder.create()
                .uv(18, 16).cuboid(0.0F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F), ModelTransform.of(2.2F, -8.5F, -0.5F, -0.5F, 0.15F, 0.35F));
        head.addChild("crest_right", ModelPartBuilder.create()
                .uv(18, 16).mirrored().cuboid(-1.0F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F), ModelTransform.of(-2.2F, -8.5F, -0.5F, -0.5F, -0.15F, -0.35F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 24).cuboid(-5.0F, 0.0F, -2.5F, 10.0F, 12.0F, 5.0F)
                .uv(30, 24).cuboid(-6.0F, 1.0F, -3.0F, 12.0F, 8.0F, 6.0F)
                .uv(20, 13).cuboid(-4.0F, 4.0F, -3.7F, 8.0F, 5.0F, 1.0F), ModelTransform.origin(0.0F, 4.0F, 0.0F));
        body.addChild("mantle", ModelPartBuilder.create()
                .uv(0, 41).cuboid(-6.0F, 0.0F, 0.0F, 12.0F, 15.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 2.5F, 0.2F, 0.0F, 0.0F));

        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(48, 38).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 14.0F, 4.0F)
                .uv(24, 41).cuboid(-2.6F, 2.0F, -2.6F, 5.0F, 5.0F, 5.0F), ModelTransform.of(-6.5F, 5.0F, 0.0F, -0.1F, 0.0F, 0.12F));
        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(48, 38).mirrored().cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 14.0F, 4.0F)
                .uv(24, 41).mirrored().cuboid(-2.4F, 2.0F, -2.6F, 5.0F, 5.0F, 5.0F), ModelTransform.of(6.5F, 5.0F, 0.0F, -0.25F, 0.0F, -0.12F));
        root.addChild("right_leg", ModelPartBuilder.create()
                .uv(32, 56).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F)
                .uv(0, 57).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 4.0F, 5.0F), ModelTransform.origin(-2.5F, 16.0F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create()
                .uv(32, 56).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F)
                .uv(0, 57).mirrored().cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 4.0F, 5.0F), ModelTransform.origin(2.5F, 16.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 80);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.56F;
        float stride = state.limbSwingAmplitude * 0.7F;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.6F;
        this.head.pitch = state.pitch * 0.017453292F * 0.35F;
        this.rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.7F * stride - 0.1F;
        this.leftArm.pitch = MathHelper.cos(swing) * 0.7F * stride - 0.25F;
        this.rightLeg.pitch = MathHelper.cos(swing) * 0.85F * stride;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.85F * stride;
        this.mantle.pitch = 0.2F + (MathHelper.cos(swing) * 0.06F * stride);
    }
}
