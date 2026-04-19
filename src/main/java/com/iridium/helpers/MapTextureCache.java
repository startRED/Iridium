package com.iridium.helpers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Environment(EnvType.CLIENT)
public final class MapTextureCache {

    private static final int MAX_ENTRIES = 64;

    private static final ConcurrentMap<MapId, Long> FINGERPRINTS = new ConcurrentHashMap<>();

    private MapTextureCache() {
    }

    public static boolean matchesAndUpdate(MapId id, MapItemSavedData data) {
        if (id == null || data == null) {
            return false;
        }
        long fp = fingerprint(data);
        Long prev = FINGERPRINTS.get(id);
        if (prev != null && prev.longValue() == fp) {
            return true;
        }
        if (FINGERPRINTS.size() >= MAX_ENTRIES) {
            FINGERPRINTS.clear();
        }
        FINGERPRINTS.put(id, fp);
        return false;
    }

    public static void clear() {
        FINGERPRINTS.clear();
    }

    private static long fingerprint(MapItemSavedData data) {
        byte[] colors = data.colors;
        if (colors == null) {
            return 0L;
        }
        long h = 1125899906842597L;
        for (int i = 0; i < colors.length; i++) {
            h = h * 31L + colors[i];
        }
        return h;
    }
}
