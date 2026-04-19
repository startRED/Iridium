package com.iridium.mixins.client.particles;

import com.iridium.config.IridiumConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkParticles.SparkProvider.class)
public abstract class FireworkParticleCapMixin {

    @Unique
    private static long iridium$lastTick = Long.MIN_VALUE;

    @Unique
    private static int iridium$spawnedThisTick;

    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void iridium$cap(SimpleParticleType particleType, ClientLevel level,
                             double x, double y, double z,
                             double xSpeed, double ySpeed, double zSpeed,
                             CallbackInfoReturnable<Particle> cir) {
        IridiumConfig cfg = IridiumConfig.get();
        if (!cfg.fireworkParticleCapEnabled) {
            return;
        }
        int cap = cfg.fireworkParticleMax;
        if (cap <= 0) {
            return;
        }
        if (level == null) {
            return;
        }
        long now = level.getGameTime();
        if (now != iridium$lastTick) {
            iridium$lastTick = now;
            iridium$spawnedThisTick = 0;
        }
        if (iridium$spawnedThisTick >= cap) {
            cir.setReturnValue(null);
            return;
        }
        iridium$spawnedThisTick++;
    }
}
