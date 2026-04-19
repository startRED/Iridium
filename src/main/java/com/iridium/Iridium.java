package com.iridium;

import com.iridium.commands.IridiumReloadCommand;
import com.iridium.compatibility.SodiumDetector;
import com.iridium.config.IridiumConfig;
import com.iridium.helpers.PerformanceMonitor;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Iridium implements ModInitializer {

    public static final String MOD_ID = "iridium";
    public static final Logger LOGGER = LoggerFactory.getLogger("Iridium");

    @Override
    public void onInitialize() {
        IridiumConfig.load();
        PerformanceMonitor.install();
        IridiumReloadCommand.register();
        LOGGER.info("Iridium iniciado (Sodium presente: {})", SodiumDetector.isSodiumLoaded());
    }
}
