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

public final class HushSovereignEntityModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart shroud;
    private final ModelPart crown;

    public HushSovereignEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.shroud = this.body.getChild("shroud");
        this.crown = this.head.getChild("crown_spine");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-5.0F, -9.0F, -5.5F, 10.0F, 9.0F, 11.0F)
                .uv(44, 0).cuboid(-6.0F, -11.0F, -6.0F, 12.0F, 4.0F, 12.0F)
                .uv(0, 20).cuboid(-2.5F, -4.5F, -6.6F, 5.0F, 5.0F, 1.0F), ModelTransform.origin(0.0F, 1.0F, 0.0F));
        head.addChild("crown_left", ModelPartBuilder.create()
                .uv(12, 20).cuboid(0.0F, -6.0F, -1.0F, 1.0F, 6.0F, 2.0F), ModelTransform.of(4.0F, -10.5F, -1.5F, -0.65F, 0.12F, 0.42F));
        head.addChild("crown_right", ModelPartBuilder.create()
                .uv(12, 20).mirrored().cuboid(-1.0F, -6.0F, -1.0F, 1.0F, 6.0F, 2.0F), ModelTransform.of(-4.0F, -10.5F, -1.5F, -0.65F, -0.12F, -0.42F));
        head.addChild("crown_spine", ModelPartBuilder.create()
                .uv(20, 20).cuboid(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F), ModelTransform.of(0.0F, -10.5F, 1.0F, -0.3F, 0.0F, 0.0F));
        head.addChild("jaw_guard", ModelPartBuilder.create()
                .uv(32, 20).cuboid(-4.0F, -1.0F, -6.0F, 8.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.12F, 0.0F, 0.0F));
        head.addChild("face_veil", ModelPartBuilder.create()
                .uv(50, 20).cuboid(-4.0F, -1.0F, -6.8F, 8.0F, 9.0F, 1.0F), ModelTransform.of(0.0F, -0.2F, 0.0F, 0.12F, 0.0F, 0.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 32).cuboid(-6.0F, 0.0F, -3.5F, 12.0F, 15.0F, 7.0F)
                .uv(38, 32).cuboid(-7.0F, 1.0F, -4.0F, 14.0F, 10.0F, 8.0F)
                .uv(0, 54).cuboid(-5.0F, 6.0F, -4.6F, 10.0F, 5.0F, 1.0F), ModelTransform.origin(0.0F, 2.0F, 0.0F));
        body.addChild("shroud", ModelPartBuilder.create()
                .uv(24, 54).cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 20.0F, 1.0F)
                .uv(58, 54).cuboid(-7.0F, 11.0F, -0.5F, 14.0F, 8.0F, 2.0F), ModelTransform.of(0.0F, 0.0F, 3.5F, 0.24F, 0.0F, 0.0F));
        body.addChild("left_pauldron", ModelPartBuilder.create()
                .uv(78, 18).mirrored().cuboid(0.0F, -2.0F, -4.0F, 4.0F, 8.0F, 8.0F), ModelTransform.of(6.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.22F));
        body.addChild("right_pauldron", ModelPartBuilder.create()
                .uv(78, 18).cuboid(-4.0F, -2.0F, -4.0F, 4.0F, 8.0F, 8.0F), ModelTransform.of(-6.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.22F));
        body.addChild("front_drape", ModelPartBuilder.create()
                .uv(92, 42).cuboid(-3.5F, 0.0F, -4.2F, 7.0F, 18.0F, 1.0F), ModelTransform.of(0.0F, 3.0F, 0.0F, -0.04F, 0.0F, 0.0F));
        body.addChild("left_side_curtain", ModelPartBuilder.create()
                .uv(108, 42).mirrored().cuboid(0.0F, 0.0F, -0.5F, 2.0F, 16.0F, 1.0F), ModelTransform.of(6.8F, 4.0F, 0.0F, 0.18F, 0.0F, -0.14F));
        body.addChild("right_side_curtain", ModelPartBuilder.create()
                .uv(108, 42).cuboid(-2.0F, 0.0F, -0.5F, 2.0F, 16.0F, 1.0F), ModelTransform.of(-6.8F, 4.0F, 0.0F, 0.18F, 0.0F, 0.14F));

        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(96, 0).cuboid(-2.6F, -1.0F, -2.6F, 5.0F, 17.0F, 5.0F)
                .uv(82, 34).cuboid(-3.2F, 4.0F, -3.2F, 6.0F, 7.0F, 6.0F)
                .uv(114, 0).cuboid(-3.4F, 10.0F, -3.6F, 1.0F, 6.0F, 4.0F), ModelTransform.of(-8.5F, 4.0F, 0.0F, -0.12F, 0.0F, 0.12F));
        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(96, 0).mirrored().cuboid(-2.4F, -1.0F, -2.6F, 5.0F, 17.0F, 5.0F)
                .uv(82, 34).mirrored().cuboid(-2.8F, 4.0F, -3.2F, 6.0F, 7.0F, 6.0F)
                .uv(114, 0).mirrored().cuboid(2.4F, 10.0F, -3.6F, 1.0F, 6.0F, 4.0F), ModelTransform.of(8.5F, 4.0F, 0.0F, -0.42F, 0.0F, -0.12F));
        root.addChild("right_leg", ModelPartBuilder.create()
                .uv(96, 22).cuboid(-2.8F, 0.0F, -2.8F, 5.0F, 15.0F, 5.0F)
                .uv(78, 50).cuboid(-3.2F, 0.0F, -3.2F, 6.0F, 5.0F, 6.0F)
                .uv(114, 10).cuboid(-1.5F, 8.0F, -3.6F, 3.0F, 5.0F, 1.0F), ModelTransform.origin(-3.2F, 17.0F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create()
                .uv(96, 22).mirrored().cuboid(-2.2F, 0.0F, -2.8F, 5.0F, 15.0F, 5.0F)
                .uv(78, 50).mirrored().cuboid(-2.8F, 0.0F, -3.2F, 6.0F, 5.0F, 6.0F)
                .uv(114, 10).mirrored().cuboid(-1.5F, 8.0F, -3.6F, 3.0F, 5.0F, 1.0F), ModelTransform.origin(3.2F, 17.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.5F;
        float stride = state.limbSwingAmplitude * 0.65F;
        float idle = MathHelper.cos(state.age * 0.06F) * 0.05F;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.55F;
        this.head.pitch = state.pitch * 0.017453292F * 0.2F;
        this.crown.pitch = -0.3F + idle;
        this.rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.55F * stride - 0.1F;
        this.leftArm.pitch = MathHelper.cos(swing) * 0.55F * stride - 0.4F;
        this.rightLeg.pitch = MathHelper.cos(swing) * 0.72F * stride;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.72F * stride;
        this.shroud.pitch = 0.24F + idle;
    }
}
