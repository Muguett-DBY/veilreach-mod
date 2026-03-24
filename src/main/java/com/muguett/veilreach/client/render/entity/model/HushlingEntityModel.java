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

public final class HushlingEntityModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public HushlingEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                .uv(32, 0).cuboid(-2.0F, -12.0F, -1.0F, 4.0F, 4.0F, 4.0F), ModelTransform.origin(0.0F, 4.0F, 0.0F));
        head.addChild("crown_spike", ModelPartBuilder.create()
                .uv(32, 8).cuboid(-1.5F, -5.0F, -1.5F, 3.0F, 5.0F, 3.0F), ModelTransform.of(0.0F, -12.0F, 0.5F, -0.25F, 0.0F, 0.0F));
        head.addChild("jaw", ModelPartBuilder.create()
                .uv(44, 0).cuboid(-3.5F, -1.0F, -5.5F, 7.0F, 3.0F, 2.0F), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.18F, 0.0F, 0.0F));
        head.addChild("left_horn", ModelPartBuilder.create()
                .uv(48, 5).mirrored().cuboid(0.0F, -4.0F, -1.0F, 1.0F, 6.0F, 2.0F), ModelTransform.of(3.5F, -8.0F, -0.5F, -0.4F, 0.0F, 0.35F));
        head.addChild("right_horn", ModelPartBuilder.create()
                .uv(48, 5).cuboid(-1.0F, -4.0F, -1.0F, 1.0F, 6.0F, 2.0F), ModelTransform.of(-3.5F, -8.0F, -0.5F, -0.4F, 0.0F, -0.35F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 16).cuboid(-4.0F, 0.0F, -2.5F, 8.0F, 12.0F, 5.0F)
                .uv(26, 16).cuboid(-4.5F, 0.0F, -3.0F, 9.0F, 7.0F, 1.0F), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.18F, 0.0F, 0.0F));
        body.addChild("spine_fins", ModelPartBuilder.create()
                .uv(26, 24).cuboid(-1.0F, -1.0F, 2.0F, 2.0F, 13.0F, 2.0F)
                .uv(34, 24).cuboid(-0.5F, -3.0F, 2.5F, 1.0F, 3.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 0.5F, -0.08F, 0.0F, 0.0F));
        body.addChild("tattered_cloak", ModelPartBuilder.create()
                .uv(0, 50).cuboid(-4.5F, 0.5F, 2.0F, 9.0F, 13.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 0.5F, 0.1F, 0.0F, 0.0F));
        body.addChild("left_shoulder", ModelPartBuilder.create()
                .uv(38, 24).mirrored().cuboid(0.0F, -1.0F, -2.5F, 2.0F, 5.0F, 5.0F), ModelTransform.of(4.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.25F));
        body.addChild("right_shoulder", ModelPartBuilder.create()
                .uv(38, 24).cuboid(-2.0F, -1.0F, -2.5F, 2.0F, 5.0F, 5.0F), ModelTransform.of(-4.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.25F));

        ModelPartData rightArm = root.addChild("right_arm", ModelPartBuilder.create()
                .uv(46, 12).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 15.0F, 3.0F), ModelTransform.of(-5.0F, 6.0F, 0.0F, -0.35F, 0.0F, 0.12F));
        rightArm.addChild("right_claw", ModelPartBuilder.create()
                .uv(52, 30).cuboid(-2.0F, 9.5F, -4.5F, 2.0F, 5.0F, 2.0F), ModelTransform.of(-0.5F, 0.0F, 0.0F, -0.22F, 0.0F, -0.18F));

        ModelPartData leftArm = root.addChild("left_arm", ModelPartBuilder.create()
                .uv(46, 12).mirrored().cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 15.0F, 3.0F), ModelTransform.of(5.0F, 6.0F, 0.0F, -0.35F, 0.0F, -0.12F));
        leftArm.addChild("left_claw", ModelPartBuilder.create()
                .uv(52, 30).mirrored().cuboid(0.0F, 9.5F, -4.5F, 2.0F, 5.0F, 2.0F), ModelTransform.of(0.5F, 0.0F, 0.0F, -0.22F, 0.0F, 0.18F));

        ModelPartData rightLeg = root.addChild("right_leg", ModelPartBuilder.create()
                .uv(0, 33).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F), ModelTransform.origin(-2.0F, 16.0F, 0.0F));
        rightLeg.addChild("right_knee_spur", ModelPartBuilder.create()
                .uv(20, 50).cuboid(-1.0F, 4.0F, 1.5F, 2.0F, 4.0F, 1.0F), ModelTransform.NONE);

        ModelPartData leftLeg = root.addChild("left_leg", ModelPartBuilder.create()
                .uv(0, 33).mirrored().cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F), ModelTransform.origin(2.0F, 16.0F, 0.0F));
        leftLeg.addChild("left_knee_spur", ModelPartBuilder.create()
                .uv(20, 50).mirrored().cuboid(-1.0F, 4.0F, 1.5F, 2.0F, 4.0F, 1.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.66F;
        float stride = state.limbSwingAmplitude;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.75F;
        this.head.pitch = state.pitch * 0.017453292F * 0.7F + 0.08F;
        this.body.pitch = 0.28F + MathHelper.cos(state.age * 0.08F) * 0.04F;
        this.rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.95F * stride - 0.55F;
        this.leftArm.pitch = MathHelper.cos(swing) * 0.95F * stride - 0.55F;
        this.rightArm.roll = 0.16F - (MathHelper.cos(state.age * 0.09F) * 0.1F);
        this.leftArm.roll = -0.16F + (MathHelper.cos(state.age * 0.09F) * 0.1F);
        this.rightLeg.pitch = MathHelper.cos(swing) * 1.15F * stride - 0.08F;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 1.15F * stride - 0.08F;
    }
}
