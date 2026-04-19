package com.iridium.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IridiumConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("Iridium/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("iridium.json");

    private static volatile IridiumConfig INSTANCE = new IridiumConfig();

    // ===== Client — culling / render =====
    public boolean entityCullingEnabled = true;
    public boolean blockEntityCullingEnabled = true;
    public boolean animationCullingEnabled = true;
    public boolean nametagCacheEnabled = true;
    public boolean beaconBeamCullingEnabled = true;
    public boolean heldItemCullingEnabled = true;
    public boolean mapTextureCacheEnabled = true;

    // ===== Client — particles =====
    public boolean particleCullingEnabled = true;
    public boolean particleManagerCapEnabled = true;
    public int particleMax = 2000;
    public boolean fireworkParticleCapEnabled = true;
    public int fireworkParticleMax = 200;

    // ===== Client — Dynamic FPS =====
    public boolean dynamicFpsEnabled = true;
    public int dynamicFpsUnfocusedFps = 15;
    public int dynamicFpsMinimizedFps = 1;

    // ===== Server — tick throttles =====
    public boolean hopperThrottleEnabled = true;
    public int hopperIdleCooldownTicks = 8;
    public boolean explosionCacheEnabled = true;

    public boolean randomTickThrottleEnabled = true;
    public int randomTickActiveRadius = 64;

    public boolean itemEntityMergeThrottleEnabled = true;
    public int itemEntityMergeInterval = 8;

    public boolean villagerAiThrottleEnabled = true;
    public int villagerAiActiveRadius = 32;

    public boolean fireSpreadThrottleEnabled = true;
    public int fireSpreadActiveRadius = 32;

    public boolean projectileTickThrottleEnabled = true;
    public double projectileFarTickDistance = 48.0;

    public static IridiumConfig get() {
        return INSTANCE;
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            IridiumConfig parsed = GSON.fromJson(reader, IridiumConfig.class);
            if (parsed != null) {
                INSTANCE = parsed;
            }
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.warn("Falha ao ler {}; usando defaults", CONFIG_PATH, e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            LOGGER.warn("Falha ao gravar {}", CONFIG_PATH, e);
        }
    }
}
