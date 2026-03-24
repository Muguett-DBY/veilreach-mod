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

public final class VeilboundWatcherEntityModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart core;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart lowerVeil;
    private final ModelPart halo;

    public VeilboundWatcherEntityModel(ModelPart root) {
        super(root);
        this.core = root.getChild("core");
        this.head = this.core.getChild("head");
        this.leftArm = this.core.getChild("left_arm");
        this.rightArm = this.core.getChild("right_arm");
        this.lowerVeil = this.core.getChild("lower_veil");
        this.halo = this.head.getChild("halo");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData core = root.addChild("core", ModelPartBuilder.create()
                .uv(0, 18).cuboid(-4.5F, -8.0F, -2.5F, 9.0F, 12.0F, 5.0F)
                .uv(28, 18).cuboid(-3.0F, 4.0F, -2.0F, 6.0F, 7.0F, 4.0F)
                .uv(0, 35).cuboid(-2.0F, -6.0F, 2.5F, 4.0F, 11.0F, 2.0F), ModelTransform.origin(0.0F, 8.0F, 0.0F));
        ModelPartData head = core.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -6.0F, -4.5F, 8.0F, 6.0F, 9.0F)
                .uv(34, 0).cuboid(-2.0F, -8.0F, -1.0F, 4.0F, 2.0F, 4.0F)
                .uv(50, 0).cuboid(-1.5F, -2.0F, -5.5F, 3.0F, 4.0F, 1.0F), ModelTransform.origin(0.0F, -7.0F, 0.0F));
        head.addChild("halo", ModelPartBuilder.create()
                .uv(40, 6).cuboid(-6.0F, -5.0F, -0.5F, 12.0F, 1.0F, 1.0F)
                .uv(40, 8).cuboid(-0.5F, -9.0F, -0.5F, 1.0F, 4.0F, 1.0F), ModelTransform.of(0.0F, -3.0F, 0.0F, -0.25F, 0.0F, 0.0F));
        head.addChild("face_veil", ModelPartBuilder.create()
                .uv(50, 5).cuboid(-4.0F, -1.0F, -5.0F, 8.0F, 8.0F, 1.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.18F, 0.0F, 0.0F));
        head.addChild("left_face_fin", ModelPartBuilder.create()
                .uv(68, 0).mirrored().cuboid(0.0F, -1.0F, -3.5F, 2.0F, 8.0F, 1.0F), ModelTransform.of(3.2F, 0.0F, 0.0F, 0.12F, 0.0F, -0.16F));
        head.addChild("right_face_fin", ModelPartBuilder.create()
                .uv(68, 0).cuboid(-2.0F, -1.0F, -3.5F, 2.0F, 8.0F, 1.0F), ModelTransform.of(-3.2F, 0.0F, 0.0F, 0.12F, 0.0F, 0.16F));
        core.addChild("left_arm", ModelPartBuilder.create()
                .uv(44, 18).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 18.0F, 2.0F), ModelTransform.of(5.5F, -5.5F, 0.0F, 0.3F, 0.0F, -0.38F));
        core.addChild("right_arm", ModelPartBuilder.create()
                .uv(44, 18).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 18.0F, 2.0F), ModelTransform.of(-5.5F, -5.5F, 0.0F, 0.3F, 0.0F, 0.38F));
        core.addChild("left_shroud", ModelPartBuilder.create()
                .uv(52, 18).mirrored().cuboid(0.0F, -4.0F, -2.0F, 3.0F, 16.0F, 4.0F), ModelTransform.of(4.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.28F));
        core.addChild("right_shroud", ModelPartBuilder.create()
                .uv(52, 18).cuboid(-3.0F, -4.0F, -2.0F, 3.0F, 16.0F, 4.0F), ModelTransform.of(-4.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.28F));
        core.addChild("lower_veil", ModelPartBuilder.create()
                .uv(24, 30).cuboid(-5.5F, 0.0F, -3.0F, 11.0F, 9.0F, 6.0F), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        core.addChild("left_tail", ModelPartBuilder.create()
                .uv(0, 48).mirrored().cuboid(0.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F), ModelTransform.of(2.0F, 7.0F, 0.0F, 0.45F, 0.0F, -0.34F));
        core.addChild("right_tail", ModelPartBuilder.create()
                .uv(0, 48).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F), ModelTransform.of(-2.0F, 7.0F, 0.0F, 0.45F, 0.0F, 0.34F));
        core.addChild("back_banner", ModelPartBuilder.create()
                .uv(8, 48).cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 13.0F, 1.0F), ModelTransform.of(0.0F, -1.0F, 2.5F, 0.22F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 96, 96);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        this.resetTransforms();
        float sway = MathHelper.cos(state.age * 0.08F) * 0.08F;
        this.core.originY = 8.0F + (MathHelper.cos(state.age * 0.12F) * 1.4F);
        this.head.yaw = state.relativeHeadYaw * 0.017453292F * 0.72F;
        this.head.pitch = state.pitch * 0.017453292F * 0.4F;
        this.leftArm.pitch = -0.35F + sway;
        this.rightArm.pitch = -0.35F - sway;
        this.leftArm.roll = -0.38F - sway;
        this.rightArm.roll = 0.38F + sway;
        this.lowerVeil.yaw = sway * 1.7F;
        this.halo.roll = MathHelper.cos(state.age * 0.06F) * 0.12F;
    }
}
