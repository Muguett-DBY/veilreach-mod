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
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.5F, -8.0F, -4.5F, 9.0F, 8.0F, 9.0F)
                .uv(36, 0).cuboid(-5.5F, -9.0F, -5.5F, 11.0F, 10.0F, 11.0F)
                .uv(0, 18).cuboid(-3.5F, -3.0F, -6.0F, 7.0F, 4.0F, 1.0F), ModelTransform.origin(0.0F, 3.5F, 0.0F));
        head.addChild("hood_peak", ModelPartBuilder.create()
                .uv(16, 18).cuboid(-2.0F, -4.0F, -1.0F, 4.0F, 4.0F, 4.0F), ModelTransform.of(0.0F, -9.0F, 1.5F, -0.25F, 0.0F, 0.0F));
        head.addChild("hood_tassel", ModelPartBuilder.create()
                .uv(40, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(0.0F, -7.5F, 4.2F, 0.42F, 0.0F, 0.0F));
        head.addChild("left_mask_flange", ModelPartBuilder.create()
                .uv(32, 18).mirrored().cuboid(0.0F, -1.0F, -4.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(4.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.2F));
        head.addChild("right_mask_flange", ModelPartBuilder.create()
                .uv(32, 18).cuboid(-2.0F, -1.0F, -4.0F, 2.0F, 6.0F, 2.0F), ModelTransform.of(-4.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.2F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 32).cuboid(-5.0F, 0.0F, -3.0F, 10.0F, 12.0F, 6.0F)
                .uv(32, 32).cuboid(-6.0F, 9.0F, -3.5F, 12.0F, 8.0F, 7.0F)
                .uv(0, 50).cuboid(-6.0F, 0.0F, -3.5F, 12.0F, 5.0F, 1.0F), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.08F, 0.0F, 0.0F));
        body.addChild("back_drape", ModelPartBuilder.create()
                .uv(26, 50).cuboid(-5.5F, 0.0F, 0.0F, 11.0F, 15.0F, 1.0F), ModelTransform.of(0.0F, 1.0F, 3.0F, 0.16F, 0.0F, 0.0F));
        body.addChild("chest_straps", ModelPartBuilder.create()
                .uv(64, 50).cuboid(-4.0F, 0.0F, -3.6F, 8.0F, 8.0F, 1.0F), ModelTransform.of(0.0F, 0.8F, 0.0F, -0.04F, 0.0F, 0.0F));
        body.addChild("left_pack", ModelPartBuilder.create()
                .uv(50, 50).mirrored().cuboid(0.0F, 0.0F, -2.0F, 3.0F, 9.0F, 4.0F), ModelTransform.of(4.5F, 4.0F, 0.0F, 0.0F, 0.0F, -0.18F));
        body.addChild("right_pack", ModelPartBuilder.create()
                .uv(50, 50).cuboid(-3.0F, 0.0F, -2.0F, 3.0F, 9.0F, 4.0F), ModelTransform.of(-4.5F, 4.0F, 0.0F, 0.0F, 0.0F, 0.18F));
        body.addChild("front_satchel", ModelPartBuilder.create()
                .uv(82, 50).cuboid(-2.5F, 7.0F, -4.6F, 5.0F, 7.0F, 2.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.06F, 0.0F, 0.0F));

        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(68, 0).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 14.0F, 4.0F), ModelTransform.of(-6.5F, 5.5F, 0.0F, -0.18F, 0.0F, 0.12F));
        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(68, 18).mirrored().cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 14.0F, 4.0F), ModelTransform.of(6.5F, 5.5F, 0.0F, -0.55F, 0.0F, -0.12F));
        root.addChild("lantern", ModelPartBuilder.create()
                .uv(84, 0).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F)
                .uv(84, 11).cuboid(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F)
                .uv(84, 17).cuboid(-1.0F, 6.0F, -1.0F, 2.0F, 2.0F, 2.0F)
                .uv(96, 17).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F), ModelTransform.origin(6.8F, 16.5F, -2.0F));
        root.addChild("right_leg", ModelPartBuilder.create()
                .uv(0, 66).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F), ModelTransform.origin(-2.2F, 16.0F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create()
                .uv(16, 66).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F), ModelTransform.origin(2.2F, 16.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 96);
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
        this.leftArm.pitch = -0.75F + MathHelper.cos(state.age * 0.12F) * 0.05F;
        this.rightLeg.pitch = MathHelper.cos(swing) * 0.9F * stride;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.9F * stride;
        this.lantern.pitch = 0.18F + MathHelper.cos(state.age * 0.18F) * 0.1F;
        this.lantern.roll = MathHelper.cos(state.age * 0.16F) * 0.08F;
    }
}
