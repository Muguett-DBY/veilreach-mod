package com.muguett.veilreach.client;

import com.muguett.veilreach.item.GrenadeItem;
import com.muguett.veilreach.item.ThrowableGrenadeItem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class GrenadeHudOverlay {
    private GrenadeHudOverlay() {
    }

    public static void initialize() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> render(drawContext));
    }

    private static void render(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !client.player.isUsingItem()) {
            return;
        }

        ItemStack stack = client.player.getActiveItem();
        if (!(stack.getItem() instanceof ThrowableGrenadeItem grenadeItem) || !grenadeItem.showsFuseHud()) {
            return;
        }

        int maxUse = stack.getMaxUseTime(client.player);
        int usedTicks = maxUse - client.player.getItemUseTimeLeft();
        int remainingTicks = Math.max(0, grenadeItem.getFuseTicks() - usedTicks);
        float remainingSeconds = remainingTicks / 20.0F;
        float progress = 1.0F - (remainingTicks / (float) grenadeItem.getFuseTicks());

        int width = drawContext.getScaledWindowWidth();
        int height = drawContext.getScaledWindowHeight();
        int barWidth = 120;
        int x = (width - barWidth) / 2;
        int y = height - 48;
        int fillWidth = Math.max(0, Math.min(barWidth, Math.round(barWidth * (1.0F - progress))));

        drawContext.fill(x - 2, y - 2, x + barWidth + 2, y + 10, 0x99000000);
        drawContext.fill(x, y, x + barWidth, y + 6, 0xFF341C26);
        drawContext.fill(x, y, x + fillWidth, y + 6, remainingTicks <= 20 ? 0xFFFF5A36 : 0xFFE8C66A);
        drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.translatable("hud.veilreach.grenade_timer", String.format("%.1f", remainingSeconds)).formatted(Formatting.GOLD), width / 2, y - 12, 0xF8F2D0);
    }
}
