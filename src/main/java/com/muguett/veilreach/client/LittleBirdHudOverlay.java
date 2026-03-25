package com.muguett.veilreach.client;

import com.muguett.veilreach.entity.LittleBirdEntity;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class LittleBirdHudOverlay {
    private LittleBirdHudOverlay() {
    }

    public static void initialize() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> render(drawContext));
    }

    private static void render(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !(client.player.getVehicle() instanceof LittleBirdEntity helicopter) || helicopter.getControllingPassenger() != client.player) {
            return;
        }

        int width = drawContext.getScaledWindowWidth();
        int height = drawContext.getScaledWindowHeight();
        int cx = width / 2;
        int cy = height / 2;
        boolean firstPerson = client.options.getPerspective().isFirstPerson();

        if (firstPerson) {
            drawCockpitFrame(drawContext, width, height, helicopter);
            drawFlightSymbology(drawContext, cx, cy, helicopter);
        }
        drawReticle(drawContext, cx, cy);
        drawHealth(drawContext, client, helicopter, width, height);
        drawSystems(drawContext, client, helicopter, width, height);
    }

    private static void drawCockpitFrame(DrawContext drawContext, int width, int height, LittleBirdEntity helicopter) {
        int cx = width / 2;
        int cy = height / 2;
        int frame = 0x7C14202A;
        int glass = 0x180C1822;
        int accent = helicopter.isDamaged() ? 0xA8FF8A74 : 0xA0D8F1FF;
        int console = 0xB0101A24;
        int glow = 0x6CE5F5FF;

        drawContext.fill(0, 0, width, 18, glass);
        drawContext.fill(0, height - 84, width, height, 0x2A08121A);
        drawContext.fill(0, 0, 10, height, glass);
        drawContext.fill(width - 10, 0, width, height, glass);

        drawContext.fill(cx - 82, 0, cx + 82, 12, frame);
        drawContext.fill(cx - 6, 0, cx + 6, cy - 46, frame);

        drawContext.fill(cx - 182, 0, cx - 168, height, frame);
        drawContext.fill(cx + 168, 0, cx + 182, height, frame);

        for (int i = 0; i < 6; i++) {
            int leftX = cx - 158 + (i * 10);
            int rightX = cx + 148 - (i * 10);
            int y = cy - 74 + (i * 14);
            drawContext.fill(leftX, y, leftX + 14, y + 5, frame);
            drawContext.fill(rightX, y, rightX + 14, y + 5, frame);
        }

        int consoleTop = height - 72;
        drawContext.fill(cx - 172, consoleTop, cx + 172, height, console);
        drawContext.fill(cx - 116, consoleTop + 7, cx + 116, consoleTop + 10, glow);
        drawContext.fill(cx - 74, consoleTop + 21, cx + 74, consoleTop + 24, accent);
        drawContext.fill(cx - 136, consoleTop + 34, cx - 62, consoleTop + 36, glow);
        drawContext.fill(cx + 62, consoleTop + 34, cx + 136, consoleTop + 36, glow);
    }

    private static void drawFlightSymbology(DrawContext drawContext, int cx, int cy, LittleBirdEntity helicopter) {
        int ladderColor = helicopter.isDamaged() ? 0xC8FF9B7A : 0xC8D8F4FF;
        int pitchOffset = Math.round(helicopter.getVisualPitch() * 1.75F);
        int rollOffset = Math.round(helicopter.getVisualRoll() * 0.65F);
        int horizonY = cy - pitchOffset;

        drawContext.fill(cx - 78 + rollOffset, horizonY - 1, cx - 26 + rollOffset, horizonY + 1, ladderColor);
        drawContext.fill(cx + 26 + rollOffset, horizonY - 1, cx + 78 + rollOffset, horizonY + 1, ladderColor);

        for (int step = 1; step <= 3; step++) {
            int yAbove = horizonY - (step * 18);
            int yBelow = horizonY + (step * 18);
            int inset = 20 + (step * 6);
            drawContext.fill(cx - 62 + rollOffset, yAbove - 1, cx - inset + rollOffset, yAbove + 1, ladderColor);
            drawContext.fill(cx + inset + rollOffset, yAbove - 1, cx + 62 + rollOffset, yAbove + 1, ladderColor);
            drawContext.fill(cx - 62 + rollOffset, yBelow - 1, cx - inset + rollOffset, yBelow + 1, ladderColor);
            drawContext.fill(cx + inset + rollOffset, yBelow - 1, cx + 62 + rollOffset, yBelow + 1, ladderColor);
        }

        drawContext.fill(cx - 2, cy + 20, cx + 2, cy + 46, 0x88D8F4FF);
        drawContext.fill(cx - 10, cy + 28, cx - 2, cy + 30, 0x88D8F4FF);
        drawContext.fill(cx + 2, cy + 28, cx + 10, cy + 30, 0x88D8F4FF);
    }

    private static void drawReticle(DrawContext drawContext, int cx, int cy) {
        int color = 0xC8D7F3FF;
        int accent = 0xE8FFF4C4;
        drawContext.fill(cx - 1, cy - 14, cx + 1, cy - 4, color);
        drawContext.fill(cx - 1, cy + 4, cx + 1, cy + 14, color);
        drawContext.fill(cx - 14, cy - 1, cx - 4, cy + 1, color);
        drawContext.fill(cx + 4, cy - 1, cx + 14, cy + 1, color);
        drawContext.fill(cx - 1, cy - 1, cx + 1, cy + 1, accent);
        drawContext.fill(cx - 24, cy - 10, cx - 18, cy - 8, color);
        drawContext.fill(cx + 18, cy - 10, cx + 24, cy - 8, color);
        drawContext.fill(cx - 24, cy + 8, cx - 18, cy + 10, color);
        drawContext.fill(cx + 18, cy + 8, cx + 24, cy + 10, color);
    }

    private static void drawHealth(DrawContext drawContext, MinecraftClient client, LittleBirdEntity helicopter, int width, int height) {
        int x = 16;
        int y = height - 52;
        int barWidth = 128;
        float ratio = Math.max(0.0F, Math.min(1.0F, helicopter.getHealthAmount() / LittleBirdEntity.MAX_HEALTH));
        int fill = Math.round(barWidth * ratio);
        int fillColor = ratio <= 0.25F ? 0xFFE05D51 : 0xFF9AE3B8;
        drawContext.fill(x - 2, y - 14, x + barWidth + 2, y + 12, 0x7A08111B);
        drawContext.drawTextWithShadow(client.textRenderer, Text.translatable("hud.veilreach.little_bird.health").formatted(Formatting.AQUA), x, y - 12, 0xE7F6FF);
        drawContext.fill(x, y, x + barWidth, y + 8, 0xFF203846);
        drawContext.fill(x, y, x + fill, y + 8, fillColor);
        drawContext.drawTextWithShadow(client.textRenderer, Text.literal(Math.round(helicopter.getHealthAmount()) + " / " + Math.round(LittleBirdEntity.MAX_HEALTH)), x + 2, y + 10, 0xE7F6FF);
    }

    private static void drawSystems(DrawContext drawContext, MinecraftClient client, LittleBirdEntity helicopter, int width, int height) {
        int panelWidth = 150;
        int x = width - panelWidth - 16;
        int y = height - 72;
        drawContext.fill(x - 2, y - 14, x + panelWidth + 2, y + 36, 0x7A08111B);
        drawContext.drawTextWithShadow(client.textRenderer, Text.translatable("hud.veilreach.little_bird.ordnance").formatted(Formatting.GOLD), x, y - 12, 0xFFF7E4B1);

        Text rocketText = helicopter.getRocketReload() > 0
                ? Text.translatable("hud.veilreach.little_bird.rockets_reload", String.format("%.1f", helicopter.getRocketReload() / 20.0F))
                : helicopter.getRocketCooldown() > 0
                ? Text.translatable("hud.veilreach.little_bird.rockets_cooldown", String.format("%.1f", helicopter.getRocketCooldown() / 20.0F), helicopter.getRocketAmmo(), LittleBirdEntity.MAX_ROCKET_AMMO)
                : Text.translatable("hud.veilreach.little_bird.rockets", helicopter.getRocketAmmo(), LittleBirdEntity.MAX_ROCKET_AMMO);
        Text bombText = helicopter.getBombReload() > 0
                ? Text.translatable("hud.veilreach.little_bird.bombs_reload", String.format("%.1f", helicopter.getBombReload() / 20.0F))
                : helicopter.getBombCooldown() > 0
                ? Text.translatable("hud.veilreach.little_bird.bombs_cooldown", String.format("%.1f", helicopter.getBombCooldown() / 20.0F), helicopter.getBombAmmo(), LittleBirdEntity.MAX_BOMB_AMMO)
                : Text.translatable("hud.veilreach.little_bird.bombs", helicopter.getBombAmmo(), LittleBirdEntity.MAX_BOMB_AMMO);

        drawContext.drawTextWithShadow(client.textRenderer, rocketText, x, y + 2, 0xE7F6FF);
        drawContext.drawTextWithShadow(client.textRenderer, bombText, x, y + 14, 0xE7F6FF);
        drawContext.drawTextWithShadow(client.textRenderer, Text.translatable("hud.veilreach.little_bird.speed", String.format("%.1f", helicopter.getVelocity().horizontalLength() * 20.0D)), x, y + 26, 0xBBD8E8);
        drawContext.drawTextWithShadow(client.textRenderer, Text.translatable("hud.veilreach.little_bird.altitude", Math.round(helicopter.getY())), x + 74, y + 26, 0xBBD8E8);
        drawContext.drawTextWithShadow(client.textRenderer, Text.translatable("hud.veilreach.little_bird.throttle", Math.round(helicopter.getThrottle() * 100.0F)), x + 74, y + 2, 0xBBD8E8);
        drawContext.drawTextWithShadow(client.textRenderer, Text.translatable("hud.veilreach.little_bird.fire_hint"), x + 74, y + 14, 0xFFE0BE7A);
    }
}
