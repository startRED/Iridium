package com.iridium.helpers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public final class NametagCache {

    private static final int TTL_TICKS = 20;
    private static final int MAX_ENTRIES = 512;

    private static final ConcurrentMap<Integer, Entry> CACHE = new ConcurrentHashMap<>();
    private static final AtomicBoolean INSTALLED = new AtomicBoolean(false);

    private NametagCache() {
    }

    public static void install() {
        if (!INSTALLED.compareAndSet(false, true)) {
            return;
        }
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clear());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> clear());
    }

    public static Component get(Entity entity) {
        if (entity == null) {
            return null;
        }
        int id = entity.getId();
        int tick = entity.tickCount;
        Entry cached = CACHE.get(id);
        if (cached != null && tick >= cached.tickCount && tick - cached.tickCount < TTL_TICKS) {
            return cached.component;
        }
        Component fresh = entity.getDisplayName();
        if (CACHE.size() >= MAX_ENTRIES) {
            CACHE.clear();
        }
        CACHE.put(id, new Entry(fresh, tick));
        return fresh;
    }

    public static void clear() {
        CACHE.clear();
    }

    private static final class Entry {
        final Component component;
        final int tickCount;

        Entry(Component component, int tickCount) {
            this.component = component;
            this.tickCount = tickCount;
        }
    }
}
