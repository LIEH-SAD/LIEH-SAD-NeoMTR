package com.lx862.jcm.mod.config;

import com.google.gson.JsonObject;
import com.lx862.jcm.loader.JCMRegistry;
import com.lx862.jcm.mod.util.JCMLogger;

import java.nio.file.Path;

public class JCMClientConfig extends Config {

    public boolean disableRendering;
    public boolean debug;
    public boolean enableScripting = true;
    public boolean scriptDebug;

    /**
     * Resolved on demand instead of in a static initializer: this class is loaded while the mods are
     * still being constructed, where {@code Minecraft.getInstance()} is still null.
     */
    private static Path configPath() {
        return JCMRegistry.getConfigPath().resolve("jsblock_client.json");
    }

    public void read() {
        read(configPath());
    }

    public void write() {
        write(configPath());
    }

    public final void reset() {
        fromJson(new JsonObject());
        write(configPath());
    }

    @Override
    public void fromJson(JsonObject jsonConfig) {
        JCMLogger.info("Loading client config...");
        this.disableRendering = jsonConfig.has("disable_rendering") && jsonConfig.get("disable_rendering").getAsBoolean();
        this.debug = jsonConfig.has("debug") && jsonConfig.get("debug").getAsBoolean();
        this.enableScripting = !jsonConfig.has("enable_scripting") || jsonConfig.get("enable_scripting").getAsBoolean();
        this.scriptDebug = jsonConfig.has("script_debug") && jsonConfig.get("script_debug").getAsBoolean();
    }

    @Override
    public JsonObject toJson() {
        JCMLogger.info("Writing client config...");
        final JsonObject jsonConfig = new JsonObject();
        jsonConfig.addProperty("disable_rendering", disableRendering);
        jsonConfig.addProperty("debug", debug);
        jsonConfig.addProperty("enable_scripting", enableScripting);
        jsonConfig.addProperty("script_debug", scriptDebug);
        return jsonConfig;
    }
}
