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
    private final ModelPart spine;

    public HushlingEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.spine = this.body.getChild("spine");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -7.5F, -5.0F, 8.0F, 7.0F, 9.0F)
                .uv(34, 0).cuboid(-2.5F, -11.0F, -1.0F, 5.0F, 4.0F, 5.0F)
                .uv(0, 16).cuboid(-3.0F, -2.0F, -6.4F, 6.0F, 3.0F, 2.0F), ModelTransform.origin(0.0F, 4.0F, -1.0F));
        head.addChild("crown_spike", ModelPartBuilder.create()
                .uv(50, 0).cuboid(-1.5F, -6.0F, -1.5F, 3.0F, 6.0F, 3.0F), ModelTransform.of(0.0F, -10.5F, 1.0F, -0.35F, 0.0F, 0.0F));
        head.addChild("crest_back", ModelPartBuilder.create()
                .uv(50, 9).cuboid(-2.0F, -5.0F, 0.0F, 4.0F, 5.0F, 2.0F), ModelTransform.of(0.0F, -10.0F, 3.0F, -0.25F, 0.0F, 0.0F));
        head.addChild("left_horn", ModelPartBuilder.create()
                .uv(40, 9).mirrored().cuboid(0.0F, -5.0F, -1.0F, 1.0F, 7.0F, 2.0F), ModelTransform.of(3.7F, -7.0F, -1.5F, -0.5F, 0.0F, 0.45F));
        head.addChild("right_horn", ModelPartBuilder.create()
                .uv(40, 9).cuboid(-1.0F, -5.0F, -1.0F, 1.0F, 7.0F, 2.0F), ModelTransform.of(-3.7F, -7.0F, -1.5F, -0.5F, 0.0F, -0.45F));
        head.addChild("left_jowl", ModelPartBuilder.create()
                .uv(18, 16).mirrored().cuboid(0.0F, -1.0F, -4.0F, 2.0F, 4.0F, 3.0F), ModelTransform.of(3.0F, -1.0F, 0.0F, 0.12F, 0.0F, 0.15F));
        head.addChild("right_jowl", ModelPartBuilder.create()
                .uv(18, 16).cuboid(-2.0F, -1.0F, -4.0F, 2.0F, 4.0F, 3.0F), ModelTransform.of(-3.0F, -1.0F, 0.0F, 0.12F, 0.0F, -0.15F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 24).cuboid(-4.5F, 0.0F, -3.0F, 9.0F, 12.0F, 6.0F)
                .uv(30, 24).cuboid(-5.0F, 1.0F, -4.0F, 10.0F, 7.0F, 2.0F)
                .uv(0, 42).cuboid(-4.0F, 3.0F, -4.4F, 8.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.24F, 0.0F, 0.0F));
        ModelPartData spine = body.addChild("spine", ModelPartBuilder.create()
                .uv(46, 24).cuboid(-1.0F, -2.0F, 2.0F, 2.0F, 14.0F, 2.0F)
                .uv(54, 24).cuboid(-2.0F, -4.0F, 2.5F, 4.0F, 4.0F, 2.0F), ModelTransform.of(0.0F, 0.0F, 0.5F, -0.1F, 0.0F, 0.0F));
        body.addChild("cloak", ModelPartBuilder.create()
                .uv(20, 42).cuboid(-5.0F, 0.0F, 2.0F, 10.0F, 14.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 0.5F, 0.16F, 0.0F, 0.0F));
        body.addChild("left_shoulder", ModelPartBuilder.create()
                .uv(42, 42).mirrored().cuboid(0.0F, -1.0F, -3.0F, 3.0F, 6.0F, 6.0F), ModelTransform.of(4.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0.32F));
        body.addChild("right_shoulder", ModelPartBuilder.create()
                .uv(42, 42).cuboid(-3.0F, -1.0F, -3.0F, 3.0F, 6.0F, 6.0F), ModelTransform.of(-4.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.32F));

        ModelPartData rightArm = root.addChild("right_arm", ModelPartBuilder.create()
                .uv(64, 0).cuboid(-1.8F, -1.0F, -1.8F, 4.0F, 15.0F, 4.0F)
                .uv(64, 19).cuboid(-2.2F, 7.0F, -2.6F, 4.0F, 6.0F, 3.0F), ModelTransform.of(-5.5F, 6.0F, 0.0F, -0.42F, 0.0F, 0.18F));
        rightArm.addChild("right_claw", ModelPartBuilder.create()
                .uv(52, 16).cuboid(-2.4F, 10.0F, -5.0F, 2.0F, 5.0F, 2.0F), ModelTransform.of(-0.2F, 0.0F, 0.0F, -0.28F, 0.0F, -0.2F));

        ModelPartData leftArm = root.addChild("left_arm", ModelPartBuilder.create()
                .uv(64, 0).mirrored().cuboid(-2.2F, -1.0F, -1.8F, 4.0F, 15.0F, 4.0F)
                .uv(64, 19).mirrored().cuboid(-1.8F, 7.0F, -2.6F, 4.0F, 6.0F, 3.0F), ModelTransform.of(5.5F, 6.0F, 0.0F, -0.42F, 0.0F, -0.18F));
        leftArm.addChild("left_claw", ModelPartBuilder.create()
                .uv(52, 16).mirrored().cuboid(0.4F, 10.0F, -5.0F, 2.0F, 5.0F, 2.0F), ModelTransform.of(0.2F, 0.0F, 0.0F, -0.28F, 0.0F, 0.2F));

        ModelPartData rightLeg = root.addChild("right_leg", ModelPartBuilder.create()
                .uv(0, 57).cuboid(-1.8F, 0.0F, -1.8F, 4.0F, 14.0F, 4.0F)
                .uv(16, 57).cuboid(-2.2F, 0.0F, -2.2F, 4.0F, 5.0F, 5.0F), ModelTransform.origin(-2.2F, 16.0F, 0.0F));
        rightLeg.addChild("right_spur", ModelPartBuilder.create()
                .uv(34, 57).cuboid(-1.0F, 4.0F, 1.8F, 2.0F, 5.0F, 1.0F), ModelTransform.NONE);
        rightLeg.addChild("right_heel", ModelPartBuilder.create()
                .uv(40, 57).cuboid(-1.0F, 12.0F, 1.2F, 2.0F, 2.0F, 2.0F), ModelTransform.NONE);

        ModelPartData leftLeg = root.addChild("left_leg", ModelPartBuilder.create()
                .uv(0, 57).mirrored().cuboid(-2.2F, 0.0F, -1.8F, 4.0F, 14.0F, 4.0F)
                .uv(16, 57).mirrored().cuboid(-1.8F, 0.0F, -2.2F, 4.0F, 5.0F, 5.0F), ModelTransform.origin(2.2F, 16.0F, 0.0F));
        leftLeg.addChild("left_spur", ModelPartBuilder.create()
                .uv(34, 57).mirrored().cuboid(-1.0F, 4.0F, 1.8F, 2.0F, 5.0F, 1.0F), ModelTransform.NONE);
        leftLeg.addChild("left_heel", ModelPartBuilder.create()
                .uv(40, 57).mirrored().cuboid(-1.0F, 12.0F, 1.2F, 2.0F, 2.0F, 2.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 96, 96);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.72F;
        float stride = state.limbSwingAmplitude;
        float idle = MathHelper.cos(state.age * 0.11F) * 0.08F;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.78F;
        this.head.pitch = state.pitch * 0.017453292F * 0.72F + 0.1F;
        this.body.pitch = 0.24F + idle * 0.6F;
        this.spine.pitch = -0.1F + idle;
        this.rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 1.02F * stride - 0.7F;
        this.leftArm.pitch = MathHelper.cos(swing) * 1.02F * stride - 0.7F;
        this.rightArm.roll = 0.2F - idle;
        this.leftArm.roll = -0.2F + idle;
        this.rightLeg.pitch = MathHelper.cos(swing) * 1.18F * stride - 0.15F;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 1.18F * stride - 0.15F;
    }
}
