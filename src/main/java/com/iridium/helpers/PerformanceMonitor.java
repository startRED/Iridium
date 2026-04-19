package com.iridium.helpers;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

public final class PerformanceMonitor {

    private static final int SAMPLE_COUNT = 100;

    private static final long[] TICK_DURATIONS_NS = new long[SAMPLE_COUNT];
    private static volatile int tickSampleIndex = 0;
    private static volatile int tickSamplesFilled = 0;
    private static volatile long tickStartNs = 0L;

    private static final LongAdder HOPPERS_THROTTLED = new LongAdder();
    private static final LongAdder RANDOM_TICKS_SKIPPED = new LongAdder();
    private static final LongAdder ITEM_MERGES_THROTTLED = new LongAdder();
    private static final LongAdder VILLAGER_BRAINS_THROTTLED = new LongAdder();
    private static final LongAdder FIRE_SPREAD_THROTTLED = new LongAdder();
    private static final LongAdder PROJECTILE_TICKS_THROTTLED = new LongAdder();
    private static final LongAdder EXPLOSION_RAYS_CACHED = new LongAdder();

    private static final LongAdder ENTITIES_CULLED = new LongAdder();
    private static final LongAdder PARTICLES_SKIPPED = new LongAdder();
    private static final LongAdder BLOCK_ENTITIES_CULLED = new LongAdder();
    private static final LongAdder ANIMATIONS_SKIPPED = new LongAdder();

    private static final AtomicBoolean INSTALLED = new AtomicBoolean(false);

    private PerformanceMonitor() {
    }

    public static void install() {
        if (!INSTALLED.compareAndSet(false, true)) {
            return;
        }
        ServerTickEvents.START_SERVER_TICK.register(server -> tickStartNs = System.nanoTime());
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long start = tickStartNs;
            if (start == 0L) {
                return;
            }
            long duration = System.nanoTime() - start;
            int idx = tickSampleIndex;
            TICK_DURATIONS_NS[idx] = duration;
            tickSampleIndex = (idx + 1) % SAMPLE_COUNT;
            if (tickSamplesFilled < SAMPLE_COUNT) {
                tickSamplesFilled++;
            }
        });
    }

    public static double getAverageMspt() {
        int filled = tickSamplesFilled;
        if (filled == 0) {
            return 0.0;
        }
        long total = 0L;
        for (int i = 0; i < filled; i++) {
            total += TICK_DURATIONS_NS[i];
        }
        return (total / (double) filled) / 1_000_000.0;
    }

    public static double getCurrentTps() {
        double mspt = getAverageMspt();
        if (mspt <= 50.0) {
            return 20.0;
        }
        return 1000.0 / mspt;
    }

    public static void onHopperThrottled() {
        HOPPERS_THROTTLED.increment();
    }

    public static long getHoppersThrottled() {
        return HOPPERS_THROTTLED.sum();
    }

    public static void onRandomTickSkipped() {
        RANDOM_TICKS_SKIPPED.increment();
    }

    public static long getRandomTicksSkipped() {
        return RANDOM_TICKS_SKIPPED.sum();
    }

    public static void onItemMergeThrottled() {
        ITEM_MERGES_THROTTLED.increment();
    }

    public static long getItemMergesThrottled() {
        return ITEM_MERGES_THROTTLED.sum();
    }

    public static void onVillagerBrainThrottled() {
        VILLAGER_BRAINS_THROTTLED.increment();
    }

    public static long getVillagerBrainsThrottled() {
        return VILLAGER_BRAINS_THROTTLED.sum();
    }

    public static void onFireSpreadThrottled() {
        FIRE_SPREAD_THROTTLED.increment();
    }

    public static long getFireSpreadThrottled() {
        return FIRE_SPREAD_THROTTLED.sum();
    }

    public static void onProjectileTickThrottled() {
        PROJECTILE_TICKS_THROTTLED.increment();
    }

    public static long getProjectileTicksThrottled() {
        return PROJECTILE_TICKS_THROTTLED.sum();
    }

    public static void onExplosionRayCached() {
        EXPLOSION_RAYS_CACHED.increment();
    }

    public static long getExplosionRaysCached() {
        return EXPLOSION_RAYS_CACHED.sum();
    }

    public static void onEntityCulled() {
        ENTITIES_CULLED.increment();
    }

    public static long getEntitiesCulled() {
        return ENTITIES_CULLED.sum();
    }

    public static void onParticleSkipped() {
        PARTICLES_SKIPPED.increment();
    }

    public static long getParticlesSkipped() {
        return PARTICLES_SKIPPED.sum();
    }

    public static void onBlockEntityCulled() {
        BLOCK_ENTITIES_CULLED.increment();
    }

    public static long getBlockEntitiesCulled() {
        return BLOCK_ENTITIES_CULLED.sum();
    }

    public static void onAnimationSkipped() {
        ANIMATIONS_SKIPPED.increment();
    }

    public static long getAnimationsSkipped() {
        return ANIMATIONS_SKIPPED.sum();
    }

    public static void resetCounters() {
        HOPPERS_THROTTLED.reset();
        RANDOM_TICKS_SKIPPED.reset();
        ITEM_MERGES_THROTTLED.reset();
        VILLAGER_BRAINS_THROTTLED.reset();
        FIRE_SPREAD_THROTTLED.reset();
        PROJECTILE_TICKS_THROTTLED.reset();
        EXPLOSION_RAYS_CACHED.reset();
        ENTITIES_CULLED.reset();
        PARTICLES_SKIPPED.reset();
        BLOCK_ENTITIES_CULLED.reset();
        ANIMATIONS_SKIPPED.reset();
    }
}
