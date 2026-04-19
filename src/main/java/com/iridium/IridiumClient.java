package com.iridium;

import com.iridium.client.FeatureToggleKeybinds;
import com.iridium.client.IridiumDebugOverlay;
import com.iridium.helpers.DynamicFpsController;
import com.iridium.helpers.FrustumCache;
import com.iridium.helpers.NametagCache;
import net.fabricmc.api.ClientModInitializer;

public final class IridiumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FrustumCache.install();
        DynamicFpsController.install();
        NametagCache.install();
        IridiumDebugOverlay.install();
        FeatureToggleKeybinds.install();
        Iridium.LOGGER.info("Iridium client iniciado");
    }
}
