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
    private final ModelPart mantle;
    private final ModelPart waistDrape;

    public WardenOfSilenceEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.cloak = this.body.getChild("cloak");
        this.mantle = this.body.getChild("mantle");
        this.waistDrape = this.body.getChild("waist_drape");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.5F, -8.5F, -4.0F, 9.0F, 9.0F, 8.0F)
                .uv(36, 0).cuboid(-5.5F, -10.0F, -5.0F, 11.0F, 3.0F, 10.0F)
                .uv(0, 53).cuboid(-2.5F, -2.5F, -5.5F, 5.0F, 4.0F, 2.0F), ModelTransform.origin(0.0F, 1.5F, 0.0F));
        head.addChild("jaw_drape", ModelPartBuilder.create()
                .uv(74, 0).cuboid(-2.5F, 0.0F, -0.5F, 5.0F, 7.0F, 1.0F), ModelTransform.of(0.0F, -1.0F, -4.15F, 0.35F, 0.0F, 0.0F));
        head.addChild("left_horn", ModelPartBuilder.create()
                .uv(18, 53).cuboid(-0.5F, -8.0F, -1.5F, 2.0F, 8.0F, 3.0F)
                .uv(28, 53).cuboid(-0.5F, -13.0F, -1.0F, 2.0F, 5.0F, 2.0F), ModelTransform.of(4.2F, -8.0F, -1.3F, -0.65F, 0.12F, 0.48F));
        head.addChild("right_horn", ModelPartBuilder.create()
                .uv(18, 53).mirrored().cuboid(-1.5F, -8.0F, -1.5F, 2.0F, 8.0F, 3.0F)
                .uv(28, 53).mirrored().cuboid(-1.5F, -13.0F, -1.0F, 2.0F, 5.0F, 2.0F), ModelTransform.of(-4.2F, -8.0F, -1.3F, -0.65F, -0.12F, -0.48F));
        head.addChild("crown_spine", ModelPartBuilder.create()
                .uv(38, 53).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 7.0F, 2.0F), ModelTransform.of(0.0F, -9.0F, 2.8F, -0.4F, 0.0F, 0.0F));
        head.addChild("brow_spikes", ModelPartBuilder.create()
                .uv(46, 53).cuboid(-3.0F, -1.0F, -5.8F, 6.0F, 2.0F, 1.0F), ModelTransform.of(0.0F, -3.2F, 0.0F, 0.08F, 0.0F, 0.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 18).cuboid(-5.5F, 0.0F, -3.0F, 11.0F, 13.0F, 6.0F)
                .uv(34, 18).cuboid(-7.0F, 1.0F, -3.8F, 14.0F, 7.0F, 8.0F)
                .uv(74, 10).cuboid(-4.0F, 12.5F, -2.5F, 8.0F, 2.0F, 5.0F), ModelTransform.origin(0.0F, 2.5F, 0.0F));
        body.addChild("mantle", ModelPartBuilder.create()
                .uv(0, 38).cuboid(-8.0F, -1.0F, -4.5F, 16.0F, 5.0F, 9.0F)
                .uv(52, 38).cuboid(-6.0F, 3.0F, -4.0F, 12.0F, 2.0F, 8.0F), ModelTransform.NONE);
        body.addChild("cloak", ModelPartBuilder.create()
                .uv(0, 53).cuboid(-6.5F, 0.0F, 0.0F, 13.0F, 18.0F, 1.0F), ModelTransform.of(0.0F, -0.5F, 3.0F, 0.18F, 0.0F, 0.0F));
        body.addChild("waist_drape", ModelPartBuilder.create()
                .uv(30, 64).cuboid(-4.5F, 0.0F, -0.5F, 9.0F, 10.0F, 1.0F), ModelTransform.of(0.0F, 10.5F, -3.05F, -0.08F, 0.0F, 0.0F));
        body.addChild("back_spikes", ModelPartBuilder.create()
                .uv(52, 50).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F), ModelTransform.of(0.0F, 2.0F, 3.2F, -0.28F, 0.0F, 0.0F));
        body.addChild("left_funeral_banner", ModelPartBuilder.create()
                .uv(60, 50).mirrored().cuboid(0.0F, 0.0F, -0.5F, 2.0F, 15.0F, 1.0F), ModelTransform.of(6.8F, 4.0F, 0.0F, 0.18F, 0.0F, -0.14F));
        body.addChild("right_funeral_banner", ModelPartBuilder.create()
                .uv(60, 50).cuboid(-2.0F, 0.0F, -0.5F, 2.0F, 15.0F, 1.0F), ModelTransform.of(-6.8F, 4.0F, 0.0F, 0.18F, 0.0F, 0.14F));

        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(64, 18).cuboid(-2.5F, -1.5F, -2.5F, 5.0F, 14.0F, 5.0F)
                .uv(86, 18).cuboid(-3.0F, 10.5F, -3.0F, 6.0F, 3.0F, 6.0F)
                .uv(84, 0).cuboid(-1.5F, 13.0F, -4.0F, 3.0F, 1.0F, 7.0F)
                .uv(96, 30).cuboid(-3.2F, 7.0F, -3.6F, 1.0F, 5.0F, 4.0F), ModelTransform.of(-8.0F, 4.5F, 0.0F, -0.18F, 0.0F, 0.2F));
        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(64, 18).mirrored().cuboid(-2.5F, -1.5F, -2.5F, 5.0F, 14.0F, 5.0F)
                .uv(86, 18).mirrored().cuboid(-3.0F, 10.5F, -3.0F, 6.0F, 3.0F, 6.0F)
                .uv(84, 0).mirrored().cuboid(-1.5F, 13.0F, -4.0F, 3.0F, 1.0F, 7.0F)
                .uv(96, 30).mirrored().cuboid(2.2F, 7.0F, -3.6F, 1.0F, 5.0F, 4.0F), ModelTransform.of(8.0F, 4.5F, 0.0F, -0.18F, 0.0F, -0.2F));
        root.addChild("right_leg", ModelPartBuilder.create()
                .uv(52, 61).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 13.0F, 5.0F)
                .uv(74, 61).cuboid(-3.0F, 11.5F, -3.5F, 6.0F, 2.0F, 7.0F), ModelTransform.origin(-3.2F, 15.5F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create()
                .uv(52, 61).mirrored().cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 13.0F, 5.0F)
                .uv(74, 61).mirrored().cuboid(-3.0F, 11.5F, -3.5F, 6.0F, 2.0F, 7.0F), ModelTransform.origin(3.2F, 15.5F, 0.0F));
        return TexturedModelData.of(modelData, 128, 96);
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
        this.cloak.pitch = 0.18F + (MathHelper.cos(swing) * 0.08F * stride);
        this.mantle.roll = MathHelper.cos(state.age * 0.06F) * 0.03F;
        this.waistDrape.pitch = -0.08F + MathHelper.cos(swing + 0.8F) * 0.06F * stride;
        this.body.pitch = MathHelper.cos(swing) * 0.03F * stride;
    }
}
