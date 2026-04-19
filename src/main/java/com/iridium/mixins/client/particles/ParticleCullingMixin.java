package com.iridium.mixins.client.particles;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.CullingHelper;
import com.iridium.helpers.PerformanceMonitor;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ParticleEngine.class)
public abstract class ParticleCullingMixin {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/Particle;render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V"
            )
    )
    private void iridium$cullParticleRender(Particle particle, VertexConsumer consumer, Camera camera, float partialTicks) {
        if (particle == null) {
            return;
        }
        if (!IridiumConfig.get().particleCullingEnabled) {
            particle.render(consumer, camera, partialTicks);
            return;
        }
        AABB box = particle.getBoundingBox();
        if (box != null && !CullingHelper.isBoxVisible(box)) {
            PerformanceMonitor.onParticleSkipped();
            return;
        }
        particle.render(consumer, camera, partialTicks);
    }
}
