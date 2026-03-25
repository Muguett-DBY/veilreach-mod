package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.LittleBirdEntityModel;
import com.muguett.veilreach.client.render.entity.state.LittleBirdRenderState;
import com.muguett.veilreach.entity.LittleBirdEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public final class LittleBirdEntityRenderer extends EntityRenderer<LittleBirdEntity, LittleBirdRenderState> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/little_bird.png");
    private static final float MODEL_SCALE = 1.34F;

    private final LittleBirdEntityModel model;

    public LittleBirdEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 1.48F;
        this.shadowOpacity = 0.75F;
        this.model = new LittleBirdEntityModel(context.getPart(VeilreachEntityModelLayers.LITTLE_BIRD));
    }

    @Override
    public void render(LittleBirdRenderState state, MatrixStack matrices, net.minecraft.client.render.command.OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        if (state.hideEntireCraft) {
            return;
        }
        matrices.push();
        matrices.translate(0.0D, 1.58D, 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - state.bodyYaw));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.bodyRoll));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.bodyPitch));
        matrices.scale(-MODEL_SCALE, -MODEL_SCALE, MODEL_SCALE);
        matrices.translate(0.0D, -1.46D, 0.0D);
        this.model.setAngles(state);
        queue.submitModelPart(this.model.getRootPart(), matrices, RenderLayers.entityCutoutNoCull(TEXTURE), state.light, OverlayTexture.DEFAULT_UV, null);
        matrices.pop();
        super.render(state, matrices, queue, cameraRenderState);
    }

    @Override
    public LittleBirdRenderState createRenderState() {
        return new LittleBirdRenderState();
    }

    @Override
    public void updateRenderState(LittleBirdEntity entity, LittleBirdRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.lastYaw, entity.getYaw());
        state.bodyPitch = entity.getVisualPitch();
        state.bodyRoll = entity.getVisualRoll();
        state.rotorAngle = entity.getRotorAngle(tickDelta);
        state.tailRotorAngle = entity.getTailRotorAngle(tickDelta);
        state.throttle = entity.getThrottle();
        state.damageFlash = entity.getDamageFlash(tickDelta);
        state.damaged = entity.isDamaged();
        MinecraftClient client = MinecraftClient.getInstance();
        boolean firstPersonPilot = client != null
                && client.player != null
                && client.options.getPerspective().isFirstPerson()
                && (client.player.getVehicle() == entity
                || client.getCameraEntity() == entity
                || (client.getCameraEntity() != null && client.getCameraEntity().getVehicle() == entity))
                && entity.hasPassenger(client.player);
        state.hideRotors = firstPersonPilot;
        state.hideEntireCraft = firstPersonPilot;
    }
}
