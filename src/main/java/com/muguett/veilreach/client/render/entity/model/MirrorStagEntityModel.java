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
                .uv(0, 0).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 16.0F)
                .uv(0, 24).cuboid(-3.0F, -7.0F, -6.0F, 6.0F, 3.0F, 8.0F), ModelTransform.of(0.0F, 13.0F, 0.0F, 0.05F, 0.0F, 0.0F));

        ModelPartData neck = body.addChild("neck", ModelPartBuilder.create()
                .uv(32, 24).cuboid(-2.0F, -8.0F, -2.0F, 4.0F, 9.0F, 4.0F), ModelTransform.of(0.0F, -1.0F, -6.0F, -0.65F, 0.0F, 0.0F));

        ModelPartData head = neck.addChild("head", ModelPartBuilder.create()
                .uv(32, 0).cuboid(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F)
                .uv(0, 35).cuboid(-1.5F, -1.5F, -9.0F, 3.0F, 3.0F, 3.0F), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.25F, 0.0F, 0.0F));

        head.addChild("left_antler", ModelPartBuilder.create()
                .uv(18, 35).cuboid(0.0F, -8.0F, -1.0F, 1.0F, 8.0F, 1.0F)
                .uv(22, 35).cuboid(1.0F, -7.0F, -1.0F, 3.0F, 1.0F, 1.0F)
                .uv(22, 38).cuboid(1.0F, -4.0F, -1.0F, 2.0F, 1.0F, 1.0F), ModelTransform.of(1.5F, -4.0F, -1.0F, -0.1F, 0.2F, 0.2F));
        head.addChild("right_antler", ModelPartBuilder.create()
                .uv(18, 35).mirrored().cuboid(-1.0F, -8.0F, -1.0F, 1.0F, 8.0F, 1.0F)
                .uv(22, 35).mirrored().cuboid(-4.0F, -7.0F, -1.0F, 3.0F, 1.0F, 1.0F)
                .uv(22, 38).mirrored().cuboid(-3.0F, -4.0F, -1.0F, 2.0F, 1.0F, 1.0F), ModelTransform.of(-1.5F, -4.0F, -1.0F, -0.1F, -0.2F, -0.2F));

        body.addChild("tail", ModelPartBuilder.create()
                .uv(48, 24).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 7.0F, 1.0F), ModelTransform.of(0.0F, -2.0F, 8.0F, 0.5F, 0.0F, 0.0F));

        root.addChild("front_left_leg", ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F), ModelTransform.origin(2.5F, 13.0F, -5.0F));
        root.addChild("front_right_leg", ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F), ModelTransform.origin(-2.5F, 13.0F, -5.0F));
        root.addChild("back_left_leg", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F), ModelTransform.origin(2.5F, 13.0F, 6.0F));
        root.addChild("back_right_leg", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F), ModelTransform.origin(-2.5F, 13.0F, 6.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.8F;
        float stride = state.limbSwingAmplitude * 0.9F;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.6F;
        this.head.pitch = 0.25F + (state.pitch * 0.017453292F * 0.5F);
        this.neck.pitch = -0.65F + (state.pitch * 0.017453292F * 0.2F);
        this.frontLeftLeg.pitch = MathHelper.cos(swing) * 0.9F * stride;
        this.frontRightLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.9F * stride;
        this.backLeftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.9F * stride;
        this.backRightLeg.pitch = MathHelper.cos(swing) * 0.9F * stride;
        this.body.pitch = 0.05F + (MathHelper.cos(swing * 0.5F) * 0.05F * stride);
        this.tail.yaw = MathHelper.cos(state.age * 0.12F) * 0.16F;
    }
}
