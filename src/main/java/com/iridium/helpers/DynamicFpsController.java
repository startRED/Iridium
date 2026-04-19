package com.iridium.helpers;

import com.iridium.config.IridiumConfig;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class DynamicFpsController {

    private static volatile boolean installed;
    private static long lastFrameNanos;

    private DynamicFpsController() {
    }

    public static void install() {
        if (installed) {
            return;
        }
        installed = true;
        WorldRenderEvents.END.register(ctx -> tick());
    }

    private static void tick() {
        IridiumConfig cfg = IridiumConfig.get();
        if (!cfg.dynamicFpsEnabled) {
            lastFrameNanos = System.nanoTime();
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            lastFrameNanos = System.nanoTime();
            return;
        }
        Window window = mc.getWindow();
        if (window == null) {
            lastFrameNanos = System.nanoTime();
            return;
        }
        long handle = window.getWindow();
        if (handle == 0L) {
            lastFrameNanos = System.nanoTime();
            return;
        }
        boolean iconified = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_ICONIFIED) == GLFW.GLFW_TRUE;
        boolean focused = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;

        int targetFps;
        if (iconified) {
            targetFps = cfg.dynamicFpsMinimizedFps;
        } else if (!focused) {
            targetFps = cfg.dynamicFpsUnfocusedFps;
        } else {
            lastFrameNanos = System.nanoTime();
            return;
        }

        if (targetFps <= 0) {
            targetFps = 1;
        }

        long frameNanos = 1_000_000_000L / targetFps;
        long now = System.nanoTime();
        long elapsed = now - lastFrameNanos;
        long waitNanos = frameNanos - elapsed;
        if (waitNanos > 0L) {
            try {
                Thread.sleep(waitNanos / 1_000_000L, (int) (waitNanos % 1_000_000L));
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        lastFrameNanos = System.nanoTime();
    }
}
