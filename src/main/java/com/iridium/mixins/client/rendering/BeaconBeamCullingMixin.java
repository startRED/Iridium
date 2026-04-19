package com.iridium.mixins.client.rendering;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.CullingHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconRenderer.class)
public abstract class BeaconBeamCullingMixin {

    private static final int BEAM_HEIGHT = 1024;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void iridium$cullBeamColumn(BeaconBlockEntity blockEntity, float partialTick,
                                        PoseStack poseStack, MultiBufferSource bufferSource,
                                        int packedLight, int packedOverlay, CallbackInfo ci) {
        if (blockEntity == null) {
            return;
        }
        if (!IridiumConfig.get().beaconBeamCullingEnabled) {
            return;
        }
        BlockPos pos = blockEntity.getBlockPos();
        AABB column = new AABB(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + BEAM_HEIGHT, pos.getZ() + 1
        );
        if (!CullingHelper.isBoxVisible(column)) {
            ci.cancel();
        }
    }
}
