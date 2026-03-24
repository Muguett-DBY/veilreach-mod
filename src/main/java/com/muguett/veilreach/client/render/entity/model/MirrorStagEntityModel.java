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

public final class MirrorStagEntityModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart tail;

    public MirrorStagEntityModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
        this.tail = this.body.getChild("tail");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.5F, -4.0F, -9.0F, 9.0F, 9.0F, 18.0F)
                .uv(0, 27).cuboid(-3.0F, -7.0F, -6.0F, 6.0F, 3.0F, 10.0F)
                .uv(32, 27).cuboid(-2.0F, -2.0F, 8.0F, 4.0F, 4.0F, 3.0F), ModelTransform.of(0.0F, 13.0F, 0.0F, 0.08F, 0.0F, 0.0F));

        ModelPartData neck = body.addChild("neck", ModelPartBuilder.create()
                .uv(38, 0).cuboid(-2.5F, -10.0F, -2.5F, 5.0F, 11.0F, 5.0F)
                .uv(54, 0).cuboid(-1.5F, -11.0F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.of(0.0F, -1.0F, -7.0F, -0.72F, 0.0F, 0.0F));

        ModelPartData head = neck.addChild("head", ModelPartBuilder.create()
                .uv(34, 16).cuboid(-3.5F, -4.5F, -7.0F, 7.0F, 7.0F, 9.0F)
                .uv(0, 40).cuboid(-2.0F, -1.8F, -10.0F, 4.0F, 3.0F, 3.0F)
                .uv(14, 40).cuboid(-3.0F, -2.0F, -6.8F, 6.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, -8.0F, -0.5F, 0.22F, 0.0F, 0.0F));

        head.addChild("left_antler", ModelPartBuilder.create()
                .uv(26, 40).cuboid(0.0F, -10.0F, -1.0F, 1.0F, 10.0F, 1.0F)
                .uv(30, 40).cuboid(1.0F, -9.0F, -1.0F, 4.0F, 1.0F, 1.0F)
                .uv(30, 42).cuboid(2.0F, -6.0F, -1.0F, 3.0F, 1.0F, 1.0F)
                .uv(30, 44).cuboid(3.0F, -3.0F, -1.0F, 2.0F, 1.0F, 1.0F)
                .uv(38, 44).cuboid(1.5F, -8.0F, -1.0F, 1.0F, 3.0F, 1.0F)
                .uv(38, 48).cuboid(3.4F, -5.0F, -1.0F, 1.0F, 3.0F, 1.0F), ModelTransform.of(1.8F, -4.0F, -1.0F, -0.12F, 0.28F, 0.22F));
        head.addChild("right_antler", ModelPartBuilder.create()
                .uv(26, 40).mirrored().cuboid(-1.0F, -10.0F, -1.0F, 1.0F, 10.0F, 1.0F)
                .uv(30, 40).mirrored().cuboid(-5.0F, -9.0F, -1.0F, 4.0F, 1.0F, 1.0F)
                .uv(30, 42).mirrored().cuboid(-5.0F, -6.0F, -1.0F, 3.0F, 1.0F, 1.0F)
                .uv(30, 44).mirrored().cuboid(-5.0F, -3.0F, -1.0F, 2.0F, 1.0F, 1.0F)
                .uv(38, 44).mirrored().cuboid(-2.5F, -8.0F, -1.0F, 1.0F, 3.0F, 1.0F)
                .uv(38, 48).mirrored().cuboid(-4.4F, -5.0F, -1.0F, 1.0F, 3.0F, 1.0F), ModelTransform.of(-1.8F, -4.0F, -1.0F, -0.12F, -0.28F, -0.22F));
        head.addChild("beard", ModelPartBuilder.create()
                .uv(40, 40).cuboid(-1.5F, 0.0F, -6.2F, 3.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, 1.0F, 0.0F, 0.18F, 0.0F, 0.0F));
        head.addChild("brow_crown", ModelPartBuilder.create()
                .uv(48, 40).cuboid(-3.0F, -5.4F, -6.2F, 6.0F, 2.0F, 4.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.12F, 0.0F, 0.0F));

        body.addChild("tail", ModelPartBuilder.create()
                .uv(48, 32).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F), ModelTransform.of(0.0F, -2.0F, 9.0F, 0.55F, 0.0F, 0.0F));
        body.addChild("chest_ruff", ModelPartBuilder.create()
                .uv(58, 16).cuboid(-3.5F, -2.0F, -10.2F, 7.0F, 8.0F, 2.0F), ModelTransform.of(0.0F, -0.5F, 0.0F, -0.1F, 0.0F, 0.0F));
        body.addChild("left_flank_prism", ModelPartBuilder.create()
                .uv(58, 26).mirrored().cuboid(0.0F, -1.5F, -3.0F, 2.0F, 6.0F, 6.0F), ModelTransform.of(4.4F, -0.2F, 0.0F, 0.0F, 0.0F, -0.16F));
        body.addChild("right_flank_prism", ModelPartBuilder.create()
                .uv(58, 26).cuboid(-2.0F, -1.5F, -3.0F, 2.0F, 6.0F, 6.0F), ModelTransform.of(-4.4F, -0.2F, 0.0F, 0.0F, 0.0F, 0.16F));

        root.addChild("front_left_leg", ModelPartBuilder.create()
                .uv(56, 32).cuboid(-1.2F, 0.0F, -1.2F, 3.0F, 13.0F, 3.0F)
                .uv(68, 32).cuboid(-1.4F, 10.0F, -1.4F, 3.0F, 4.0F, 3.0F), ModelTransform.origin(3.0F, 13.0F, -6.0F));
        root.addChild("front_right_leg", ModelPartBuilder.create()
                .uv(56, 32).mirrored().cuboid(-1.8F, 0.0F, -1.2F, 3.0F, 13.0F, 3.0F)
                .uv(68, 32).mirrored().cuboid(-1.6F, 10.0F, -1.4F, 3.0F, 4.0F, 3.0F), ModelTransform.origin(-3.0F, 13.0F, -6.0F));
        root.addChild("back_left_leg", ModelPartBuilder.create()
                .uv(56, 48).cuboid(-1.2F, 0.0F, -1.2F, 3.0F, 13.0F, 3.0F)
                .uv(68, 48).cuboid(-1.4F, 10.0F, -1.4F, 3.0F, 4.0F, 3.0F), ModelTransform.origin(3.0F, 13.0F, 7.0F));
        root.addChild("back_right_leg", ModelPartBuilder.create()
                .uv(56, 48).mirrored().cuboid(-1.8F, 0.0F, -1.2F, 3.0F, 13.0F, 3.0F)
                .uv(68, 48).mirrored().cuboid(-1.6F, 10.0F, -1.4F, 3.0F, 4.0F, 3.0F), ModelTransform.origin(-3.0F, 13.0F, 7.0F));

        return TexturedModelData.of(modelData, 96, 96);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.8F;
        float stride = state.limbSwingAmplitude * 0.92F;
        float idle = MathHelper.cos(state.age * 0.09F) * 0.04F;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.6F;
        this.head.pitch = 0.22F + (state.pitch * 0.017453292F * 0.46F);
        this.neck.pitch = -0.72F + (state.pitch * 0.017453292F * 0.18F) + idle;
        this.frontLeftLeg.pitch = MathHelper.cos(swing) * 0.95F * stride;
        this.frontRightLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.95F * stride;
        this.backLeftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.95F * stride;
        this.backRightLeg.pitch = MathHelper.cos(swing) * 0.95F * stride;
        this.body.pitch = 0.08F + (MathHelper.cos(swing * 0.5F) * 0.05F * stride);
        this.tail.yaw = MathHelper.cos(state.age * 0.12F) * 0.18F;
        this.tail.pitch = 0.55F + idle;
    }
}
