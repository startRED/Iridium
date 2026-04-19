package com.iridium.mixins.client.particles;

import com.iridium.config.IridiumConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
public abstract class ParticleManagerMixin {

    @Shadow @Final private Queue<Particle> particlesToAdd;
    @Shadow @Final private Map<ParticleRenderType, Queue<Particle>> particles;

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void iridium$enforceCap(Particle particle, CallbackInfo ci) {
        if (particle == null) {
            return;
        }
        IridiumConfig cfg = IridiumConfig.get();
        if (!cfg.particleManagerCapEnabled) {
            return;
        }
        int cap = cfg.particleMax;
        if (cap <= 0) {
            return;
        }
        int active = this.particlesToAdd.size();
        if (active >= cap) {
            ci.cancel();
            return;
        }
        for (Queue<Particle> queue : this.particles.values()) {
            active += queue.size();
            if (active >= cap) {
                ci.cancel();
                return;
            }
        }
    }
}
