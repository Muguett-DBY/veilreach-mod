package com.muguett.veilreach.mixin.client;

import com.muguett.veilreach.item.ThrowableGrenadeItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void veilreach$tiltGrenadesWhileUsing(net.minecraft.entity.LivingEntity entity, ItemStack item, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue renderQueue, int light, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayerEntity player) || !(item.getItem() instanceof ThrowableGrenadeItem) || !player.isUsingItem()) {
            return;
        }
        if (displayContext != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND && displayContext != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            return;
        }
        if (!ItemStack.areItemsAndComponentsEqual(player.getActiveItem(), item)) {
            return;
        }

        boolean rightHand = displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        float charge = 1.0F - (player.getItemUseTimeLeft() / 72000.0F);
        float eased = Math.min(1.0F, charge * 10.0F);
        float handSign = rightHand ? 1.0F : -1.0F;

        matrices.translate(handSign * -0.22F * eased, -0.24F * eased, -0.3F * eased);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-36.0F * eased));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(handSign * 22.0F * eased));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(handSign * -18.0F * eased));
    }
}
