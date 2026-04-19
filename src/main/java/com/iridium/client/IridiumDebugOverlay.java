package com.iridium.client;

import com.iridium.Iridium;
import com.iridium.helpers.PerformanceMonitor;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class IridiumDebugOverlay {

    private static final KeyMapping TOGGLE = new KeyMapping(
            "iridium.keybind.overlay",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "iridium.keybind.category"
    );

    private static volatile boolean visible = false;
    private static volatile boolean installed = false;

    private IridiumDebugOverlay() {
    }

    public static void install() {
        if (installed) {
            return;
        }
        installed = true;

        KeyBindingHelper.registerKeyBinding(TOGGLE);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (TOGGLE.consumeClick()) {
                visible = !visible;
            }
        });

        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
            if (!visible) {
                return;
            }
            renderOverlay(graphics);
        });

        Iridium.LOGGER.info("Iridium debug overlay registrado");
    }

    public static boolean isVisible() {
        return visible;
    }

    private static void renderOverlay(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        Font font = mc.font;
        if (font == null) {
            return;
        }

        String[] lines = new String[] {
                Component.translatable("iridium.overlay.title").getString(),
                Component.translatable("iridium.overlay.mspt",
                        String.format("%.2f", PerformanceMonitor.getAverageMspt())).getString(),
                Component.translatable("iridium.overlay.tps",
                        String.format("%.2f", PerformanceMonitor.getCurrentTps())).getString(),
                Component.translatable("iridium.overlay.entitiesCulled",
                        PerformanceMonitor.getEntitiesCulled()).getString(),
                Component.translatable("iridium.overlay.blockEntitiesCulled",
                        PerformanceMonitor.getBlockEntitiesCulled()).getString(),
                Component.translatable("iridium.overlay.animationsSkipped",
                        PerformanceMonitor.getAnimationsSkipped()).getString(),
                Component.translatable("iridium.overlay.particlesSkipped",
                        PerformanceMonitor.getParticlesSkipped()).getString(),
                Component.translatable("iridium.overlay.hoppersThrottled",
                        PerformanceMonitor.getHoppersThrottled()).getString(),
                Component.translatable("iridium.overlay.randomTicksSkipped",
                        PerformanceMonitor.getRandomTicksSkipped()).getString(),
                Component.translatable("iridium.overlay.itemMergesThrottled",
                        PerformanceMonitor.getItemMergesThrottled()).getString(),
                Component.translatable("iridium.overlay.villagersThrottled",
                        PerformanceMonitor.getVillagerBrainsThrottled()).getString(),
                Component.translatable("iridium.overlay.fireSpreadThrottled",
                        PerformanceMonitor.getFireSpreadThrottled()).getString(),
                Component.translatable("iridium.overlay.projectilesThrottled",
                        PerformanceMonitor.getProjectileTicksThrottled()).getString(),
                Component.translatable("iridium.overlay.explosionRaysCached",
                        PerformanceMonitor.getExplosionRaysCached()).getString()
        };

        int padX = 4;
        int padY = 4;
        int lineHeight = font.lineHeight + 1;

        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, font.width(line));
        }

        int bgX1 = padX - 2;
        int bgY1 = padY - 2;
        int bgX2 = padX + maxWidth + 2;
        int bgY2 = padY + lineHeight * lines.length + 2;
        graphics.fill(bgX1, bgY1, bgX2, bgY2, 0xA0000000);

        int y = padY;
        boolean firstLine = true;
        for (String line : lines) {
            int color = firstLine ? 0xFFFFFF55 : 0xFFE0E0E0;
            graphics.drawString(font, line, padX, y, color, false);
            y += lineHeight;
            firstLine = false;
        }
    }
}
