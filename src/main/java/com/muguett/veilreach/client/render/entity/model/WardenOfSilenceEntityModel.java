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

public final class WardenOfSilenceEntityModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart cloak;

    public WardenOfSilenceEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.cloak = this.body.getChild("cloak");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                .uv(32, 0).cuboid(-4.5F, -9.0F, -4.5F, 9.0F, 3.0F, 9.0F), ModelTransform.origin(0.0F, 3.0F, 0.0F));
        head.addChild("left_horn", ModelPartBuilder.create()
                .uv(0, 54).cuboid(0.0F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F), ModelTransform.of(2.5F, -8.0F, -1.5F, -0.35F, 0.15F, 0.35F));
        head.addChild("right_horn", ModelPartBuilder.create()
                .uv(0, 54).mirrored().cuboid(-1.0F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F), ModelTransform.of(-2.5F, -8.0F, -1.5F, -0.35F, -0.15F, -0.35F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 16).cuboid(-5.0F, 0.0F, -2.5F, 10.0F, 13.0F, 5.0F)
                .uv(30, 16).cuboid(-6.0F, 1.0F, -3.0F, 12.0F, 7.0F, 6.0F), ModelTransform.origin(0.0F, 3.0F, 0.0F));
        body.addChild("cloak", ModelPartBuilder.create()
                .uv(0, 34).cuboid(-5.5F, 0.0F, 0.0F, 11.0F, 16.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 2.5F, 0.15F, 0.0F, 0.0F));

        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(48, 29).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 14.0F, 4.0F), ModelTransform.of(-7.0F, 5.0F, 0.0F, -0.15F, 0.0F, 0.12F));
        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(48, 29).mirrored().cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 14.0F, 4.0F), ModelTransform.of(7.0F, 5.0F, 0.0F, -0.15F, 0.0F, -0.12F));
        root.addChild("right_leg", ModelPartBuilder.create().uv(32, 47).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F), ModelTransform.origin(-2.5F, 16.0F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create().uv(32, 47).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F), ModelTransform.origin(2.5F, 16.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.55F;
        float stride = state.limbSwingAmplitude * 0.75F;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.65F;
        this.head.pitch = state.pitch * 0.017453292F * 0.4F;
        this.rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.8F * stride - 0.15F;
        this.leftArm.pitch = MathHelper.cos(swing) * 0.8F * stride - 0.15F;
        this.rightLeg.pitch = MathHelper.cos(swing) * 0.9F * stride;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.9F * stride;
        this.cloak.pitch = 0.15F + (MathHelper.cos(swing) * 0.08F * stride);
    }
}
