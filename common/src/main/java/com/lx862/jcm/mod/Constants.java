package com.lx862.jcm.mod;

import net.minecraft.resources.Identifier;

public class Constants {
    public static final String MOD_NAME = "NeoJCM";
    public static final String MOD_ID = "jsblock";
    public static final String LOGGING_PREFIX = "[NeoJCM] ";
    public static final String MOD_VERSION = "2.3.0-beta.1";
    public static final int MC_TICK_PER_SECOND = 20;

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }
}
