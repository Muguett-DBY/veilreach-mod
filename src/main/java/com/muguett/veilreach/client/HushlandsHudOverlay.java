package com.muguett.veilreach.client;

import com.muguett.veilreach.VeilreachMod;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.world.HushlandsRegion;
import com.muguett.veilreach.world.HushlandsRegionResolver;
import com.muguett.veilreach.world.dimension.VeilreachDimensions;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
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
        HushlandsRegion region = HushlandsRegionResolver.getRegion(client.player);

        drawTexture(drawContext, FOG_STRIP, 0, Math.max(0, height - 34), width, 34, 256, 64);
        drawTexture(drawContext, CORNER_CREST, 12, 10, 28, 28, 64, 64);
        drawTexture(drawContext, HUD_FRAME, width - 48, 10, 36, 36, 256, 256);

        drawContext.drawTextWithShadow(client.textRenderer, Text.translatable(region.hudKey()), 46, 16, 0xD9F4FF);
        drawContext.drawTextWithShadow(client.textRenderer, getObjectiveText(client), 18, height - 26, 0xDCE9F4);

        Text warning = getRegionWarning(client, region);
        if (warning != null) {
            drawContext.drawTextWithShadow(client.textRenderer, warning, 18, height - 14, 0xFFB27A);
        }
    }

    private static void drawTexture(DrawContext drawContext, Identifier texture, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, width, height, textureWidth, textureHeight);
    }

    private static Text getObjectiveText(MinecraftClient client) {
        if (!hasItem(client, VeilreachItems.VEIL_SHARD)
                || !hasItem(client, VeilreachItems.HUSHBERRY)
                || !hasItem(client, VeilreachItems.LUNAR_FIBER)) {
            return Text.translatable("hud.veilreach.hushlands.objective.gather_outer");
        }
        if (!hasItem(client, VeilreachItems.HUSH_WARD_CHARM) && !HushlandsProgressClientState.defeatedSovereign()) {
            return Text.translatable("hud.veilreach.hushlands.objective.find_ward");
        }
        if (!HushlandsProgressClientState.defeatedWarden()) {
            return Text.translatable("hud.veilreach.hushlands.objective.defeat_warden");
        }
        if (!HushlandsProgressClientState.completedCathedral()) {
            return Text.translatable("hud.veilreach.hushlands.objective.find_cathedral");
        }
        if (!HushlandsProgressClientState.completedSanctum()) {
            return Text.translatable("hud.veilreach.hushlands.objective.find_sanctum");
        }
        if (!HushlandsProgressClientState.defeatedSovereign()) {
            return Text.translatable("hud.veilreach.hushlands.objective.awaken_altar");
        }
        return Text.translatable("hud.veilreach.hushlands.objective.deep_badlands");
    }

    private static Text getRegionWarning(MinecraftClient client, HushlandsRegion region) {
        if (region == HushlandsRegion.MID && !hasItem(client, VeilreachItems.HUSH_WARD_CHARM) && !hasItem(client, VeilreachItems.SOVEREIGN_WARD)) {
            return Text.translatable("hud.veilreach.hushlands.warning.mid");
        }
        if (region == HushlandsRegion.INNER && !hasItem(client, VeilreachItems.SOVEREIGN_WARD) && !HushlandsProgressClientState.defeatedSovereign()) {
            return Text.translatable("hud.veilreach.hushlands.warning.inner");
        }
        return null;
    }

    private static boolean hasItem(MinecraftClient client, Item item) {
        if (client.player == null) {
            return false;
        }
        for (int i = 0; i < client.player.getInventory().size(); i++) {
            if (client.player.getInventory().getStack(i).isOf(item)) {
                return true;
            }
        }
        return false;
    }
}
