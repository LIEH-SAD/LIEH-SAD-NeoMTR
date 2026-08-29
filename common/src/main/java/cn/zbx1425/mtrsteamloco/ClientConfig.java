package cn.zbx1425.mtrsteamloco;

import cn.zbx1425.mtrsteamloco.render.ShadersModHandler;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientConfig {

    private static Path path;

    public static boolean enableOptimization = true;
    public static boolean enableBbModelPreload = false;
    public static boolean translucentSort = false;

    public static boolean enableScriptDebugOverlay = false;

    public static boolean enableRail3D = true;
    public static boolean enableRailRender = true;
    public static boolean enableTrainRender = true;
    public static boolean enableTrainSound = true;
    public static boolean enableSmoke = true;

    public static boolean hideRidingTrain = false;

    // Ranges for the Eye Candy adjustment sliders (translation in blocks, rotation in degrees, scale multiplier)
    public static final EyeCandyScreenGroupConfig eyecandyScreenGroup = new EyeCandyScreenGroupConfig();

    public static class Entry {
        public float min, max, step;
        public int[] modes = {0, 0, 0}; // Per-axis input mode (0 = slider, 1 = text field)

        public Entry(float min, float max, float step) {
            this.min = min;
            this.max = max;
            this.step = step;
        }
    }

    public static class EyeCandyScreenGroupConfig {
        public Entry[] entries = {
                new Entry(-100, 100, 0.01f), // Translation: blocks
                new Entry(0, 360, 1),        // Rotation: degrees
                new Entry(0, 10, 0.01f)      // Scale: multiplier
        };

        public void getListEntries(List<AbstractConfigListEntry> list, ConfigEntryBuilder entryBuilder, Supplier<Screen> parentScreenSupplier) {
            // Placeholder implementation
            // In a real scenario, this would add actual config entries to the list.
        }
    }

    public static void load(Path path) {
        ClientConfig.path = path;
        if (!Files.exists(path)) {
            save();
        }
        try {
            JsonObject configObject = Main.JSON_PARSER.parse(Files.readString(path)).getAsJsonObject();
            enableOptimization = !getOrDefault(configObject, "shaderCompatMode", JsonElement::getAsBoolean, false);
            enableBbModelPreload = getOrDefault(configObject, "enableBbModelPreload", JsonElement::getAsBoolean, false);
            translucentSort = getOrDefault(configObject, "translucentSort", JsonElement::getAsBoolean, false);
            enableScriptDebugOverlay = getOrDefault(configObject, "enableScriptDebugOverlay", JsonElement::getAsBoolean, false);
            enableRail3D = getOrDefault(configObject, "enableRail3D", JsonElement::getAsBoolean, true);
            enableRailRender = getOrDefault(configObject, "enableRailRender", JsonElement::getAsBoolean, true);
            enableTrainRender = getOrDefault(configObject, "enableTrainRender", JsonElement::getAsBoolean, true);
            enableTrainSound = getOrDefault(configObject, "enableTrainSound", JsonElement::getAsBoolean, true);
            enableSmoke = getOrDefault(configObject, "enableSmoke", JsonElement::getAsBoolean, true);
            hideRidingTrain = getOrDefault(configObject, "hideRidingTrain", JsonElement::getAsBoolean, false);
        } catch (Exception ex) {
            Main.LOGGER.warn("Failed loading client config:", ex);
            save();
        }
    }

    private static <T> T getOrDefault(JsonObject jsonObject, String key, Function<JsonElement, T> getter, T defaultValue) {
        if (jsonObject.has(key)) {
            return getter.apply(jsonObject.get(key));
        } else {
            return defaultValue;
        }
    }

    public static int getRailRenderLevel() {
        if (!useRenderOptimization()) {
            return enableRailRender ? 1 : 0;
        } else {
            return enableRailRender
                    ? (enableRail3D ? (ShadersModHandler.canInstance() ? 3 : 2) : 1)
                    : 0;
        }
    }

    public static boolean useRenderOptimization() {
        return enableOptimization && ShadersModHandler.canDrawWithBuffer();
    }

    public static void save() {
        try {
            if (path == null) return;
            JsonObject configObject = new JsonObject();
            configObject.addProperty("shaderCompatMode", !enableOptimization);
            configObject.addProperty("enableBbModelPreload", enableBbModelPreload);
            configObject.addProperty("translucentSort", translucentSort);
            configObject.addProperty("enableScriptDebugOverlay", enableScriptDebugOverlay);
            configObject.addProperty("enableRail3D", enableRail3D);
            configObject.addProperty("enableRailRender", enableRailRender);
            configObject.addProperty("enableTrainRender", enableTrainRender);
            configObject.addProperty("enableTrainSound", enableTrainSound);
            configObject.addProperty("enableSmoke", enableSmoke);
            configObject.addProperty("hideRidingTrain", hideRidingTrain);
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(configObject));
        } catch (Exception ex) {
            Main.LOGGER.warn("Failed loading client config:", ex);
        }
    }

    public static void load() {
        load(Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("mtrsteamloco.json"));
    }

}