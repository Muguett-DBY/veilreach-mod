package com.muguett.veilreach.mixin.client;

import com.muguett.veilreach.item.ThrowableGrenadeItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Inject(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void veilreach$tiltGrenadesWhileUsing(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, OrderedRenderCommandQueue renderQueue, int light, CallbackInfo ci) {
        if (!(item.getItem() instanceof ThrowableGrenadeItem) || !player.isUsingItem() || player.getActiveHand() != hand) {
            return;
        }

        boolean rightHand = hand == Hand.MAIN_HAND ? player.getMainArm() == net.minecraft.util.Arm.RIGHT : player.getMainArm() != net.minecraft.util.Arm.RIGHT;
        float charge = 1.0F - (player.getItemUseTimeLeft() / 72000.0F);
        float eased = Math.min(1.0F, charge * 10.0F);
        float handSign = rightHand ? 1.0F : -1.0F;

        matrices.translate(handSign * -0.16F * eased, -0.18F * eased, -0.24F * eased);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-28.0F * eased));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(handSign * 18.0F * eased));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(handSign * -14.0F * eased));
    }
}
