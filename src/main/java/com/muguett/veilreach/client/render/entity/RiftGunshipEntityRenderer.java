package com.muguett.veilreach.client.render.entity;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.client.render.entity.model.RiftGunshipEntityModel;
import com.muguett.veilreach.client.render.entity.state.HoverVehicleRenderState;
import com.muguett.veilreach.entity.RiftGunshipEntity;
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

public final class RiftGunshipEntityRenderer extends EntityRenderer<RiftGunshipEntity, HoverVehicleRenderState> {
    private static final Identifier TEXTURE = Identifier.of(VeilreachMod.MOD_ID, "textures/entity/rift_gunship.png");
    private static final float MODEL_SCALE = 1.26F;

    private final RiftGunshipEntityModel model;

    public RiftGunshipEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 1.56F;
        this.shadowOpacity = 0.78F;
        this.model = new RiftGunshipEntityModel(context.getPart(VeilreachEntityModelLayers.RIFT_GUNSHIP));
    }

    @Override
    public void render(HoverVehicleRenderState state, MatrixStack matrices, net.minecraft.client.render.command.OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        if (state.hideEntireCraft) {
            return;
        }
        matrices.push();
        matrices.translate(0.0D, 1.2D, 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - state.bodyYaw));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.bodyRoll));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.bodyPitch));
        matrices.scale(-MODEL_SCALE, -MODEL_SCALE, MODEL_SCALE);
        matrices.translate(0.0D, -1.5D, 0.0D);
        this.model.setAngles(state);
        queue.submitModelPart(this.model.getRootPart(), matrices, RenderLayers.entityCutoutNoCull(TEXTURE), state.light, OverlayTexture.DEFAULT_UV, null);
        matrices.pop();
        super.render(state, matrices, queue, cameraRenderState);
    }

    @Override
    public HoverVehicleRenderState createRenderState() {
        return new HoverVehicleRenderState();
    }

    @Override
    public void updateRenderState(RiftGunshipEntity entity, HoverVehicleRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.lastYaw, entity.getYaw());
        state.bodyPitch = entity.getVisualPitch();
        state.bodyRoll = entity.getVisualRoll();
        state.turretYaw = entity.getTurretYaw();
        state.turretPitch = entity.getTurretPitch();
        state.throttle = entity.getThrottle();
        state.damageFlash = entity.getDamageFlash(tickDelta);
        state.damaged = entity.isDamaged();
        state.critical = entity.isCriticalDamage();
        MinecraftClient client = MinecraftClient.getInstance();
        state.hideEntireCraft = client != null
                && client.player != null
                && client.options.getPerspective().isFirstPerson()
                && client.player.getVehicle() == entity
                && entity.hasPassenger(client.player);
    }
}
