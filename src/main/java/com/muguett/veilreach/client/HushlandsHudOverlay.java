package com.muguett.veilreach.client;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.world.dimension.VeilreachDimensions;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class HushlandsHudOverlay {
    private static final Identifier HUD_FRAME = Identifier.of(VeilreachMod.MOD_ID, "textures/gui/hushlands_hud_frame.png");
    private static final Identifier CORNER_CREST = Identifier.of(VeilreachMod.MOD_ID, "textures/gui/hushlands_corner_crest.png");
    private static final Identifier FOG_STRIP = Identifier.of(VeilreachMod.MOD_ID, "textures/gui/hushlands_fog_strip.png");

    private HushlandsHudOverlay() {
    }

    public static void initialize() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> render(drawContext));
    }

    private static void render(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || !client.world.getRegistryKey().equals(VeilreachDimensions.HUSHLANDS_WORLD)) {
            return;
        }

        int width = drawContext.getScaledWindowWidth();
        int height = drawContext.getScaledWindowHeight();
        float pulse = 0.5F + (0.5F * (float) Math.sin((client.player.age + client.getRenderTickCounter().getTickProgress(false)) * 0.045F));

        drawAlphaTexture(drawContext, FOG_STRIP, 0, Math.max(0, height - 34), width, 34, 0.16F + pulse * 0.06F, 256, 64);
        drawAlphaTexture(drawContext, CORNER_CREST, 12, 10, 28, 28, 0.30F + pulse * 0.1F, 64, 64);
        drawAlphaTexture(drawContext, HUD_FRAME, width - 48, 10, 36, 36, 0.14F + pulse * 0.06F, 256, 256);
    }

    private static void drawAlphaTexture(DrawContext drawContext, Identifier texture, int x, int y, int width, int height, float alpha, int textureWidth, int textureHeight) {
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, width, height, textureWidth, textureHeight);
    }
}
