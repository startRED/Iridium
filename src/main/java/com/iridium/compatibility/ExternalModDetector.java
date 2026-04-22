package com.iridium.compatibility;

import net.fabricmc.loader.api.FabricLoader;

public final class ExternalModDetector {

    private static final FabricLoader LOADER = FabricLoader.getInstance();

    private static final boolean ENTITY_CULLING_LOADED = LOADER.isModLoaded("entityculling");
    private static final boolean MORE_CULLING_LOADED = LOADER.isModLoaded("moreculling");
    private static final boolean ENHANCED_BLOCK_ENTITIES_LOADED = LOADER.isModLoaded("enhancedblockentities");
    private static final boolean DYNAMIC_FPS_LOADED = LOADER.isModLoaded("dynamic_fps");
    private static final boolean IMMEDIATELY_FAST_LOADED = LOADER.isModLoaded("immediatelyfast");
    private static final boolean SODIUM_EXTRA_LOADED = LOADER.isModLoaded("sodium-extra");

    private ExternalModDetector() {
    }

    public static boolean isEntityCullingLoaded() {
        return ENTITY_CULLING_LOADED;
    }

    public static boolean isMoreCullingLoaded() {
        return MORE_CULLING_LOADED;
    }

    public static boolean isEnhancedBlockEntitiesLoaded() {
        return ENHANCED_BLOCK_ENTITIES_LOADED;
    }

    public static boolean isDynamicFpsLoaded() {
        return DYNAMIC_FPS_LOADED;
    }

    public static boolean isImmediatelyFastLoaded() {
        return IMMEDIATELY_FAST_LOADED;
    }

    public static boolean isSodiumExtraLoaded() {
        return SODIUM_EXTRA_LOADED;
    }
}
