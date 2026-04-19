package com.iridium.client;

import com.iridium.Iridium;
import com.iridium.config.IridiumConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class FeatureToggleKeybinds {

    private static final KeyMapping TOGGLE_ENTITY_CULLING = new KeyMapping(
            "iridium.keybind.toggleEntityCulling",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "iridium.keybind.category"
    );

    private static final KeyMapping TOGGLE_PARTICLE_CULLING = new KeyMapping(
            "iridium.keybind.toggleParticleCulling",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "iridium.keybind.category"
    );

    private static final KeyMapping TOGGLE_DYNAMIC_FPS = new KeyMapping(
            "iridium.keybind.toggleDynamicFps",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "iridium.keybind.category"
    );

    private static volatile boolean installed = false;

    private FeatureToggleKeybinds() {
    }

    public static void install() {
        if (installed) {
            return;
        }
        installed = true;

        KeyBindingHelper.registerKeyBinding(TOGGLE_ENTITY_CULLING);
        KeyBindingHelper.registerKeyBinding(TOGGLE_PARTICLE_CULLING);
        KeyBindingHelper.registerKeyBinding(TOGGLE_DYNAMIC_FPS);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            IridiumConfig c = IridiumConfig.get();
            boolean changed = false;

            while (TOGGLE_ENTITY_CULLING.consumeClick()) {
                c.entityCullingEnabled = !c.entityCullingEnabled;
                notifyToggle("iridium.toggle.entityCulling", c.entityCullingEnabled);
                changed = true;
            }
            while (TOGGLE_PARTICLE_CULLING.consumeClick()) {
                c.particleCullingEnabled = !c.particleCullingEnabled;
                notifyToggle("iridium.toggle.particleCulling", c.particleCullingEnabled);
                changed = true;
            }
            while (TOGGLE_DYNAMIC_FPS.consumeClick()) {
                c.dynamicFpsEnabled = !c.dynamicFpsEnabled;
                notifyToggle("iridium.toggle.dynamicFps", c.dynamicFpsEnabled);
                changed = true;
            }

            if (changed) {
                IridiumConfig.save();
            }
        });

        Iridium.LOGGER.info("Iridium feature toggle keybinds registrados");
    }

    private static void notifyToggle(String featureKey, boolean enabled) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        String statusKey = enabled ? "iridium.toggle.on" : "iridium.toggle.off";
        player.displayClientMessage(
                Component.translatable(statusKey, Component.translatable(featureKey)),
                true);
    }
}
