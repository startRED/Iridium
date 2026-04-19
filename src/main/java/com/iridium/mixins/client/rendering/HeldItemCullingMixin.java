package com.iridium.mixins.client.rendering;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.CullingHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class HeldItemCullingMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void iridium$skipIfOutsideFrustum(PoseStack poseStack, MultiBufferSource bufferSource,
                                              int packedLight, LivingEntity livingEntity,
                                              float limbSwing, float limbSwingAmount, float partialTicks,
                                              float ageInTicks, float netHeadYaw, float headPitch,
                                              CallbackInfo ci) {
        if (livingEntity == null) {
            return;
        }
        if (!IridiumConfig.get().heldItemCullingEnabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && livingEntity == mc.getCameraEntity()) {
            return;
        }
        if (!CullingHelper.isEntityVisible(livingEntity)) {
            ci.cancel();
        }
    }
}
