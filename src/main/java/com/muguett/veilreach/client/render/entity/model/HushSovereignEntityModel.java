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

    public HushSovereignEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.shroud = this.body.getChild("shroud");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-5.0F, -9.0F, -5.0F, 10.0F, 9.0F, 10.0F)
                .uv(40, 0).cuboid(-6.0F, -11.0F, -6.0F, 12.0F, 4.0F, 12.0F)
                .uv(0, 19).cuboid(-2.0F, -4.0F, -6.4F, 4.0F, 4.0F, 1.0F), ModelTransform.origin(0.0F, 1.0F, 0.0F));
        head.addChild("crown_left", ModelPartBuilder.create().uv(10, 19).cuboid(0.0F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F), ModelTransform.of(3.5F, -10.0F, -1.0F, -0.55F, 0.12F, 0.35F));
        head.addChild("crown_right", ModelPartBuilder.create().uv(10, 19).mirrored().cuboid(-1.0F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F), ModelTransform.of(-3.5F, -10.0F, -1.0F, -0.55F, -0.12F, -0.35F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(0, 28).cuboid(-6.0F, 0.0F, -3.0F, 12.0F, 15.0F, 6.0F)
                .uv(36, 28).cuboid(-7.0F, 1.0F, -3.5F, 14.0F, 9.0F, 7.0F), ModelTransform.origin(0.0F, 2.0F, 0.0F));
        body.addChild("shroud", ModelPartBuilder.create()
                .uv(0, 49).cuboid(-7.0F, 0.0F, 0.0F, 14.0F, 19.0F, 1.0F)
                .uv(30, 49).cuboid(-6.5F, 10.0F, -0.5F, 13.0F, 7.0F, 2.0F), ModelTransform.of(0.0F, 0.0F, 3.0F, 0.2F, 0.0F, 0.0F));

        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(60, 44).cuboid(-2.5F, -1.0F, -2.5F, 5.0F, 16.0F, 5.0F)
                .uv(48, 16).cuboid(-3.0F, 4.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.of(-8.0F, 4.0F, 0.0F, -0.15F, 0.0F, 0.08F));
        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(60, 44).mirrored().cuboid(-2.5F, -1.0F, -2.5F, 5.0F, 16.0F, 5.0F)
                .uv(48, 16).mirrored().cuboid(-3.0F, 4.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.of(8.0F, 4.0F, 0.0F, -0.35F, 0.0F, -0.08F));
        root.addChild("right_leg", ModelPartBuilder.create().uv(72, 0).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 14.0F, 5.0F), ModelTransform.origin(-3.0F, 17.0F, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create().uv(72, 0).mirrored().cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 14.0F, 5.0F), ModelTransform.origin(3.0F, 17.0F, 0.0F));
        return TexturedModelData.of(modelData, 96, 80);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float swing = state.limbSwingAnimationProgress * 0.5F;
        float stride = state.limbSwingAmplitude * 0.65F;
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.55F;
        this.head.pitch = state.pitch * 0.017453292F * 0.25F;
        this.rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.55F * stride - 0.15F;
        this.leftArm.pitch = MathHelper.cos(swing) * 0.55F * stride - 0.35F;
        this.rightLeg.pitch = MathHelper.cos(swing) * 0.75F * stride;
        this.leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 0.75F * stride;
        this.shroud.pitch = 0.2F + (MathHelper.cos(state.age * 0.08F) * 0.04F);
    }
}
