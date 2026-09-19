package com.lx862.jcm.mod.config;

import com.google.gson.JsonObject;
import com.lx862.jcm.mod.util.JCMLogger;

public class JCMConfig extends Config {
    public boolean debug;

    @Override
    public void fromJson(JsonObject jsonConfig) {
        JCMLogger.info("Loading config...");
        this.debug = jsonConfig.get("debug_mode").getAsBoolean();
    }

    @Override
    public JsonObject toJson() {
        JCMLogger.info("Writing config...");
        final JsonObject jsonConfig = new JsonObject();
        jsonConfig.addProperty("debug_mode", debug);
        return jsonConfig;
    }
}
