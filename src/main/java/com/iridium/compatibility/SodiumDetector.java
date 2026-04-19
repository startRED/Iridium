package com.iridium.compatibility;

import net.fabricmc.loader.api.FabricLoader;

public final class SodiumDetector {

    private static final boolean SODIUM_LOADED =
            FabricLoader.getInstance().isModLoaded("sodium");

    private SodiumDetector() {
    }

    public static boolean isSodiumLoaded() {
        return SODIUM_LOADED;
    }
}
