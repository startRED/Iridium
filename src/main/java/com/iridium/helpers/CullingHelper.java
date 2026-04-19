package com.iridium.helpers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

@Environment(EnvType.CLIENT)
public final class CullingHelper {

    private CullingHelper() {
    }

    public static boolean isBoxVisible(AABB box) {
        if (box == null) {
            return true;
        }
        Frustum frustum = FrustumCache.getFrustum();
        if (frustum == null) {
            return true;
        }
        return frustum.isVisible(box);
    }

    public static boolean isEntityVisible(Entity entity) {
        if (entity == null) {
            return true;
        }
        return isBoxVisible(entity.getBoundingBoxForCulling());
    }
}
