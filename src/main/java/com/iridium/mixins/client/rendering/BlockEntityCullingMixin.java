package com.iridium.mixins.client.rendering;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.CullingHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityCullingMixin {

    @Shadow
    public abstract <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E blockEntity);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void iridium$skipIfOutsideFrustum(
            E blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, CallbackInfo ci) {
        if (blockEntity == null) {
            return;
        }
        if (!IridiumConfig.get().blockEntityCullingEnabled) {
            return;
        }
        BlockEntityRenderer<E> renderer = this.getRenderer(blockEntity);
        if (renderer == null) {
            return;
        }
        if (renderer.shouldRenderOffScreen(blockEntity)) {
            return;
        }
        BlockPos pos = blockEntity.getBlockPos();
        // Alocação inevitável: Frustum.isVisible exige AABB (imutável; sem primitivo público) — RULES §6.
        AABB box = new AABB(pos).inflate(0.5);
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            Entity cameraEntity = mc.getCameraEntity();
            if (cameraEntity != null && box.contains(cameraEntity.getX(), cameraEntity.getY(), cameraEntity.getZ())) {
                return;
            }
        }
        if (!CullingHelper.isBoxVisible(box)) {
            ci.cancel();
        }
    }
}
