package com.muguett.veilreach.client.render.entity.model;

import com.muguett.veilreach.client.render.entity.state.HoverVehicleRenderState;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

public final class RiftGunshipEntityModel extends EntityModel<HoverVehicleRenderState> {
    private final ModelPart hull;
    private final ModelPart turretBase;
    private final ModelPart turretPitch;

    public RiftGunshipEntityModel(ModelPart root) {
        super(root, RenderLayers::entityCutoutNoCull);
        this.hull = root.getChild("hull");
        this.turretBase = this.hull.getChild("turret_base");
        this.turretPitch = this.turretBase.getChild("turret_pitch");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData hull = root.addChild("hull", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 6.0F, 24.0F)
                .uv(0, 30).cuboid(-6.0F, -7.0F, -8.0F, 12.0F, 3.0F, 14.0F)
                .uv(0, 47).cuboid(-3.0F, -2.0F, -16.0F, 6.0F, 3.0F, 4.0F), ModelTransform.origin(0.0F, 17.0F, 0.0F));

        hull.addChild("cockpit_frame", ModelPartBuilder.create()
                .uv(52, 30).cuboid(-5.5F, -5.0F, -10.0F, 11.0F, 4.0F, 8.0F)
                .uv(52, 42).cuboid(-4.0F, -7.0F, -7.0F, 8.0F, 2.0F, 5.0F), ModelTransform.of(0.0F, -1.0F, 0.0F, -0.05F, 0.0F, 0.0F));

        hull.addChild("engine_pods", ModelPartBuilder.create()
                .uv(86, 0).cuboid(-12.0F, -2.0F, -4.0F, 4.0F, 4.0F, 10.0F)
                .uv(86, 14).cuboid(8.0F, -2.0F, -4.0F, 4.0F, 4.0F, 10.0F)
                .uv(88, 28).cuboid(-2.0F, -2.0F, 11.0F, 4.0F, 3.0F, 5.0F), ModelTransform.NONE);

        hull.addChild("winglets", ModelPartBuilder.create()
                .uv(52, 0).cuboid(-15.0F, -1.0F, -3.0F, 7.0F, 1.0F, 10.0F)
                .uv(52, 11).cuboid(8.0F, -1.0F, -3.0F, 7.0F, 1.0F, 10.0F)
                .uv(56, 22).cuboid(-2.0F, -1.0F, 12.0F, 4.0F, 1.0F, 8.0F), ModelTransform.NONE);

        hull.addChild("skids", ModelPartBuilder.create()
                .uv(0, 54).cuboid(-11.0F, 4.0F, -8.0F, 2.0F, 1.0F, 18.0F)
                .uv(0, 73).cuboid(9.0F, 4.0F, -8.0F, 2.0F, 1.0F, 18.0F)
                .uv(40, 54).cuboid(-10.0F, 0.0F, -6.0F, 1.0F, 4.0F, 1.0F)
                .uv(44, 54).cuboid(-10.0F, 0.0F, 6.0F, 1.0F, 4.0F, 1.0F)
                .uv(48, 54).cuboid(9.0F, 0.0F, -6.0F, 1.0F, 4.0F, 1.0F)
                .uv(52, 54).cuboid(9.0F, 0.0F, 6.0F, 1.0F, 4.0F, 1.0F), ModelTransform.NONE);

        ModelPartData turretBase = hull.addChild("turret_base", ModelPartBuilder.create()
                .uv(86, 36).cuboid(-3.0F, -2.0F, -7.0F, 6.0F, 3.0F, 7.0F)
                .uv(86, 46).cuboid(-2.0F, -3.0F, -5.0F, 4.0F, 1.0F, 4.0F), ModelTransform.of(0.0F, 1.0F, -7.0F, 0.0F, 0.0F, 0.0F));

        turretBase.addChild("turret_pitch", ModelPartBuilder.create()
                .uv(78, 54).cuboid(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 13.0F)
                .uv(108, 54).cuboid(-2.5F, -1.5F, -5.0F, 5.0F, 3.0F, 5.0F), ModelTransform.of(0.0F, -0.2F, -6.6F, -0.05F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 128, 96);
    }

    @Override
    public void setAngles(HoverVehicleRenderState state) {
        this.resetTransforms();
        this.hull.pitch = state.damaged ? 0.02F : 0.0F;
        this.hull.roll = state.critical ? 0.015F : 0.0F;
        this.turretBase.yaw = MathHelper.wrapDegrees(state.turretYaw - state.bodyYaw) * MathHelper.RADIANS_PER_DEGREE;
        this.turretPitch.pitch = state.turretPitch * MathHelper.RADIANS_PER_DEGREE;
    }
}
