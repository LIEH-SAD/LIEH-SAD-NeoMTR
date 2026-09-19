package com.lx862.jcm.mod.registry;

import com.lx862.jcm.loader.JCMRegistry;
import com.lx862.jcm.loader.JCMRegistryClient;
import com.lx862.jcm.mod.JCMClient;
import com.lx862.jcm.mod.data.JCMServerStats;
import com.lx862.jcm.mod.resources.JCMResourceManager;
import com.lx862.jcm.mod.scripting.jcm.JCMScripting;
import mtr.client.CustomResources;

public class Events {
    public static void register() {
        // Start Tick Event for counting tick
        JCMRegistry.registerTickEvent((server) -> {
            JCMServerStats.incrementGameTick();
        });
    }

    public static void registerClient() {
        CustomResources.registerReloadListener(resourceManager -> {
            JCMResourceManager.reload();
        });

        JCMRegistryClient.registerTickEvent((minecraft) -> {
            JCMClient.getMcMetaManager().tick();
            JCMScripting.tick();
        });
    }
}
