package com.lx862.jcm.mod;

import com.lx862.jcm.loader.JCMRegistry;
import com.lx862.jcm.mod.config.JCMConfig;
import com.lx862.jcm.mod.registry.*;
import com.lx862.jcm.mod.util.JCMLogger;
import mtr.Keys;

public class JCM {
    private static final JCMConfig config = new JCMConfig();

    public static void init() {
        try {
            JCMLogger.info("{} v{} @ NeoMTR {}", Constants.MOD_NAME, Constants.MOD_VERSION, Keys.class.getField("MOD_VERSION").get(null));
        } catch (Exception e) {
            JCMLogger.warn("Cannot obtain NeoMTR Version, countdown to disaster...");
        }
        config.read(JCMRegistry.getConfigPath().resolve("config").resolve("jsblock.json"));
        Blocks.register();
        BlockEntities.register();
        Items.register();
        Networking.register();
        Events.register();
    }

    public static JCMConfig getConfig() {
        return config;
    }
}