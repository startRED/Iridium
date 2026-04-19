package com.iridium.helpers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public final class FrustumCache {

    private static volatile Frustum frustum;
    private static volatile double cameraX;
    private static volatile double cameraY;
    private static volatile double cameraZ;
    private static volatile boolean installed;

    private FrustumCache() {
    }

    public static void install() {
        if (installed) {
            return;
        }
        installed = true;
        WorldRenderEvents.AFTER_SETUP.register(ctx -> {
            frustum = ctx.frustum();
            Camera cam = ctx.camera();
            if (cam == null) {
                return;
            }
            Vec3 pos = cam.getPosition();
            cameraX = pos.x;
            cameraY = pos.y;
            cameraZ = pos.z;
        });
    }

    public static Frustum getFrustum() {
        return frustum;
    }

    public static double cameraX() {
        return cameraX;
    }

    public static double cameraY() {
        return cameraY;
    }

    public static double cameraZ() {
        return cameraZ;
    }
}
