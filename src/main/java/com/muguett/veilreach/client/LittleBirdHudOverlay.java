package com.muguett.veilreach.client;

import com.muguett.veilreach.entity.vehicle.AbstractArmedVehicleEntity;
import com.muguett.veilreach.entity.vehicle.VehicleHudState;
import com.muguett.veilreach.entity.vehicle.VehicleWeaponMount;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public final class LittleBirdHudOverlay {
    private LittleBirdHudOverlay() {
    }

    public static void initialize() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> render(drawContext));
    }

    private static void render(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !(client.player.getVehicle() instanceof AbstractArmedVehicleEntity vehicle) || vehicle.getControllingPassenger() != client.player) {
            return;
        }

        VehicleHudState hudState = vehicle.createHudState(client.player);
        int width = drawContext.getScaledWindowWidth();
        int height = drawContext.getScaledWindowHeight();
        int cx = width / 2;
        int cy = height / 2;
        boolean firstPerson = client.options.getPerspective().isFirstPerson();

        if (firstPerson) {
            drawCockpitFrame(drawContext, width, height, hudState);
            drawFlightSymbology(drawContext, cx, cy, vehicle, hudState);
        }
        drawReticle(drawContext, cx, cy, hudState.lockActive(), hudState.accentColor(), hudState.warningColor());
        drawHealth(drawContext, client, hudState, width, height);
        drawSystems(drawContext, client, hudState, width, height);
    }

    private static void drawCockpitFrame(DrawContext drawContext, int width, int height, VehicleHudState state) {
        int cx = width / 2;
        int cy = height / 2;
        int frame = 0x7C14202A;
        int glass = 0x160C1822;
        int accent = state.critical() ? state.warningColor() : state.accentColor();
        int console = 0xB0101A24;
        int glow = (state.accentColor() & 0x00FFFFFF) | 0x66000000;

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
        drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, state.callsign(), cx, consoleTop + 45, 0xE7F6FF);
    }

    private static void drawFlightSymbology(DrawContext drawContext, int cx, int cy, AbstractArmedVehicleEntity vehicle, VehicleHudState state) {
        int ladderColor = state.critical() ? state.warningColor() : state.accentColor();
        int pitchOffset = Math.round(vehicle.getVisualPitch() * 1.75F);
        int rollOffset = Math.round(vehicle.getVisualRoll() * 0.65F);
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

        drawContext.fill(cx - 2, cy + 20, cx + 2, cy + 46, ladderColor);
        drawContext.fill(cx - 10, cy + 28, cx - 2, cy + 30, ladderColor);
        drawContext.fill(cx + 2, cy + 28, cx + 10, cy + 30, ladderColor);
    }

    private static void drawReticle(DrawContext drawContext, int cx, int cy, boolean hasLock, int accentColor, int warningColor) {
        int color = hasLock ? warningColor : accentColor;
        int accent = hasLock ? 0xFFF2C59F : 0xFFEFF9FF;
        drawContext.fill(cx - 1, cy - 14, cx + 1, cy - 4, color);
        drawContext.fill(cx - 1, cy + 4, cx + 1, cy + 14, color);
        drawContext.fill(cx - 14, cy - 1, cx - 4, cy + 1, color);
        drawContext.fill(cx + 4, cy - 1, cx + 14, cy + 1, color);
        drawContext.fill(cx - 1, cy - 1, cx + 1, cy + 1, accent);
        if (hasLock) {
            drawContext.fill(cx - 34, cy - 18, cx - 30, cy - 2, accent);
            drawContext.fill(cx - 34, cy - 18, cx - 18, cy - 14, accent);
            drawContext.fill(cx + 30, cy - 18, cx + 34, cy - 2, accent);
            drawContext.fill(cx + 18, cy - 18, cx + 34, cy - 14, accent);
            drawContext.fill(cx - 34, cy + 2, cx - 30, cy + 18, accent);
            drawContext.fill(cx - 34, cy + 14, cx - 18, cy + 18, accent);
            drawContext.fill(cx + 30, cy + 2, cx + 34, cy + 18, accent);
            drawContext.fill(cx + 18, cy + 14, cx + 34, cy + 18, accent);
        }
    }

    private static void drawHealth(DrawContext drawContext, MinecraftClient client, VehicleHudState state, int width, int height) {
        int x = 16;
        int y = height - 52;
        int barWidth = 138;
        float ratio = Math.max(0.0F, Math.min(1.0F, state.health() / state.maxHealth()));
        int fill = Math.round(barWidth * ratio);
        int fillColor = state.critical() ? 0xFFE05D51 : state.damaged() ? 0xFFE9B36F : 0xFF9AE3B8;
        drawContext.fill(x - 2, y - 14, x + barWidth + 2, y + 12, 0x7A08111B);
        drawContext.drawTextWithShadow(client.textRenderer, state.callsign(), x, y - 12, 0xE7F6FF);
        drawContext.fill(x, y, x + barWidth, y + 8, 0xFF203846);
        drawContext.fill(x, y, x + fill, y + 8, fillColor);
        drawContext.drawTextWithShadow(client.textRenderer, Text.literal(Math.round(state.health()) + " / " + Math.round(state.maxHealth())), x + 2, y + 10, 0xE7F6FF);
    }

    private static void drawSystems(DrawContext drawContext, MinecraftClient client, VehicleHudState state, int width, int height) {
        int panelWidth = 178;
        int x = width - panelWidth - 16;
        int y = height - 108;
        drawContext.fill(x - 2, y - 14, x + panelWidth + 2, y + 70, 0x7A08111B);
        drawContext.drawTextWithShadow(client.textRenderer, Text.translatable("hud.veilreach.vehicle.systems"), x, y - 12, 0xFFF7E4B1);

        int row = 0;
        for (VehicleWeaponMount mount : state.mounts()) {
            drawContext.drawTextWithShadow(client.textRenderer, formatMountStatus(mount), x, y + (row * 12), mount.tintColor());
            row++;
        }

        drawContext.drawTextWithShadow(client.textRenderer, state.targetText(), x, y + 40, state.lockActive() ? state.warningColor() : 0xBBD8E8);
        drawContext.drawTextWithShadow(client.textRenderer, Text.literal(String.format("SPD %.1f", state.speed())), x, y + 52, 0xBBD8E8);
        drawContext.drawTextWithShadow(client.textRenderer, Text.literal("ALT " + state.altitude()), x + 76, y + 52, 0xBBD8E8);
        drawContext.drawTextWithShadow(client.textRenderer, Text.literal("THR " + Math.round(state.throttle() * 100.0F) + "%"), x + 76, y + 40, 0xBBD8E8);
        drawContext.drawTextWithShadow(client.textRenderer, state.hintText(), x, y + 64, 0xFFE0BE7A);
    }

    private static Text formatMountStatus(VehicleWeaponMount mount) {
        if (mount.isReloading()) {
            return mount.label().copy().append(Text.literal(": reloading %.1fs".formatted(mount.reloadTicks() / 20.0F)));
        }
        if (mount.isCoolingDown()) {
            return mount.label().copy().append(Text.literal(": cooldown %.1fs (%s/%s)".formatted(mount.cooldownTicks() / 20.0F, mount.ammo(), mount.maxAmmo())));
        }
        return mount.label().copy().append(Text.literal(": %s / %s".formatted(mount.ammo(), mount.maxAmmo())));
    }
}
