package com.iridium.mixins.server.tick;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.PerformanceMonitor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Projectile.class)
public abstract class ProjectileTickThrottleMixin {

    @Unique
    private static final double IRIDIUM$IMMINENT_LOOKAHEAD_TICKS = 3.0;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void iridium$throttleWhenFar(CallbackInfo ci) {
        if (!IridiumConfig.get().projectileTickThrottleEnabled) {
            return;
        }
        Projectile self = (Projectile) (Object) this;
        Level level = self.level();
        if (level == null || level.isClientSide) {
            return;
        }
        double threshold = IridiumConfig.get().projectileFarTickDistance;
        if (threshold <= 0.0) {
            return;
        }
        if (iridium$hasPlayerWithin(level, self, threshold * threshold)) {
            return;
        }
        if ((self.tickCount % 3) == 0) {
            return;
        }
        // Guarda contra perda de hit em projéteis rápidos: raycast curto na direção
        // do movimento atual; se algo bloquear, mantém tick completo (RULES §6).
        if (iridium$isCollisionImminent(level, self)) {
            return;
        }
        PerformanceMonitor.onProjectileTickThrottled();
        ci.cancel();
    }

    @Unique
    private static boolean iridium$hasPlayerWithin(Level level, Projectile projectile, double distanceSq) {
        List<? extends Player> players = level.players();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p == null) {
                continue;
            }
            if (projectile.distanceToSqr(p) <= distanceSq) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean iridium$isCollisionImminent(Level level, Projectile projectile) {
        Vec3 vel = projectile.getDeltaMovement();
        double speedSq = vel.lengthSqr();
        if (speedSq < 1.0e-6) {
            return false;
        }
        Vec3 from = projectile.position();
        Vec3 to = from.add(vel.scale(IRIDIUM$IMMINENT_LOOKAHEAD_TICKS));
        BlockHitResult hit = level.clip(new ClipContext(
                from, to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                projectile));
        return hit.getType() != HitResult.Type.MISS;
    }
}
