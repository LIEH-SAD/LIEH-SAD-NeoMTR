package com.lx862.jcm.mod.resources;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.JCMClient;
import com.lx862.jcm.mod.data.pids.PIDSManager;
import com.lx862.jcm.mod.render.text.TextRenderingManager;
import com.lx862.jcm.mod.scripting.jcm.JCMScripting;
import com.lx862.jcm.mod.util.JCMLogger;
import mtr.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class JCMResourceManager {
    private static final Identifier CUSTOM_RESOURCE_PATH = Constants.id("joban_custom_resources.json");

    public static void reload() {
        JCMClient.getMcMetaManager().reset();
        TextRenderingManager.initialize();
        PIDSManager.reset();
        // Drop all script instances so the newly parsed scripts are used instead of the stale ones
        JCMScripting.reset();
        parseCustomResources();
    }

    private static void parseCustomResources() {
        Minecraft.getInstance().getResourceManager().getResourceStack(CUSTOM_RESOURCE_PATH).forEach((resource -> {
            try(InputStream is = resource.open()) {
                String str = IOUtils.toString(is, Charsets.UTF_8);
                JsonObject jsonObject = JsonParser.parseString(str).getAsJsonObject();
                PIDSManager.loadJson(jsonObject);
            } catch (Exception e) {
                JCMLogger.error("Failed to parse custom resource file!", e);
            }
        }));
    }
}
