package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.LittleBirdRocketEntityModel;
import com.muguett.veilreach.client.render.entity.state.LittleBirdRocketRenderState;
import com.muguett.veilreach.entity.LittleBirdTrackingMissileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public final class LittleBirdTrackingMissileEntityRenderer extends EntityRenderer<LittleBirdTrackingMissileEntity, LittleBirdRocketRenderState> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/little_bird_rocket.png");

    private final LittleBirdRocketEntityModel model;

    public LittleBirdTrackingMissileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.22F;
        this.shadowOpacity = 0.4F;
        this.model = new LittleBirdRocketEntityModel(context.getPart(VeilreachEntityModelLayers.LITTLE_BIRD_ROCKET));
    }

    @Override
    public void render(LittleBirdRocketRenderState state, MatrixStack matrices, net.minecraft.client.render.command.OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - state.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.pitch));
        matrices.scale(1.08F, 1.08F, 1.08F);
        this.model.setAngles(state);
        queue.submitModelPart(this.model.getRootPart(), matrices, RenderLayers.entityCutoutNoCull(TEXTURE), state.light, OverlayTexture.DEFAULT_UV, null);
        matrices.pop();
        super.render(state, matrices, queue, cameraRenderState);
    }

    @Override
    public LittleBirdRocketRenderState createRenderState() {
        return new LittleBirdRocketRenderState();
    }

    @Override
    public void updateRenderState(LittleBirdTrackingMissileEntity entity, LittleBirdRocketRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        Vec3d velocity = entity.getVelocity();
        if (velocity.lengthSquared() > 1.0E-5D) {
            state.yaw = (float) Math.toDegrees(Math.atan2(velocity.x, velocity.z));
            state.pitch = (float) -Math.toDegrees(Math.atan2(velocity.y, velocity.horizontalLength()));
        }
        state.roll = (entity.age + tickDelta) * 0.28F;
    }
}
