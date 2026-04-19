package com.iridium.mixins.server.tick;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.PerformanceMonitor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(Explosion.class)
public abstract class ExplosionCacheMixin {

    @Unique
    private static long iridium$cacheGameTime = Long.MIN_VALUE;

    @Unique
    private static final IdentityHashMap<Entity, Map<Vec3, Float>> iridium$cache = new IdentityHashMap<>();

    @Unique
    private static final ArrayDeque<Map<Vec3, Float>> iridium$mapPool = new ArrayDeque<>();

    @Unique
    private static void iridium$rolloverIfNewTick(long now) {
        if (now == iridium$cacheGameTime) {
            return;
        }
        // Devolve mapas internos ao pool (clear + reuse) — evita alocação em hot path (RULES §6).
        for (Map<Vec3, Float> m : iridium$cache.values()) {
            m.clear();
            iridium$mapPool.push(m);
        }
        iridium$cache.clear();
        iridium$cacheGameTime = now;
    }

    @Inject(method = "getSeenPercent", at = @At("HEAD"), cancellable = true)
    private static void iridium$lookupCache(Vec3 source, Entity entity, CallbackInfoReturnable<Float> cir) {
        if (!IridiumConfig.get().explosionCacheEnabled) {
            return;
        }
        if (entity == null || source == null) {
            return;
        }
        Level level = entity.level();
        if (level == null || level.isClientSide) {
            return;
        }
        iridium$rolloverIfNewTick(level.getGameTime());
        Map<Vec3, Float> perEntity = iridium$cache.get(entity);
        if (perEntity == null) {
            return;
        }
        Float cached = perEntity.get(source);
        if (cached != null) {
            PerformanceMonitor.onExplosionRayCached();
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "getSeenPercent", at = @At("RETURN"))
    private static void iridium$storeCache(Vec3 source, Entity entity, CallbackInfoReturnable<Float> cir) {
        if (!IridiumConfig.get().explosionCacheEnabled) {
            return;
        }
        if (entity == null || source == null) {
            return;
        }
        Level level = entity.level();
        if (level == null || level.isClientSide) {
            return;
        }
        Float value = cir.getReturnValue();
        if (value == null) {
            return;
        }
        iridium$rolloverIfNewTick(level.getGameTime());
        Map<Vec3, Float> perEntity = iridium$cache.get(entity);
        if (perEntity == null) {
            perEntity = iridium$mapPool.isEmpty() ? new HashMap<>(4) : iridium$mapPool.pop();
            iridium$cache.put(entity, perEntity);
        }
        perEntity.put(source, value);
    }
}
