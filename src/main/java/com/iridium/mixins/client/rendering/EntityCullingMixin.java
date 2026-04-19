package com.iridium.mixins.client.rendering;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.CullingHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityCullingMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void iridium$skipIfOutsideFrustum(
            E entity, double x, double y, double z, float yaw, float tickDelta,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            CallbackInfo ci) {
        if (entity == null) {
            return;
        }
        if (!IridiumConfig.get().entityCullingEnabled) {
            return;
        }
        if (entity.noCulling) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && entity == mc.getCameraEntity()) {
            return;
        }
        if (!CullingHelper.isEntityVisible(entity)) {
            ci.cancel();
        }
    }
}
