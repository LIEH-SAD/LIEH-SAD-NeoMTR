package com.lx862.jcm.mod.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx862.jcm.mod.util.JCMLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public abstract class Config {
    public void read(Path path) {
        if(!Files.exists(path)) {
            write(path);
            read(path);
        } else {
            try {
                JsonObject jsonObject = JsonParser.parseString(String.join("", Files.readAllLines(path))).getAsJsonObject();
                fromJson(jsonObject);
            } catch (Exception e) {
                JCMLogger.error("Error reading the config file: ", e);
                write(path);
                JCMLogger.warn("Failed to read config file, config may be left at it's default state.");
            }
        }
    }

    public void write(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, Collections.singleton(new GsonBuilder().setPrettyPrinting().create().toJson(toJson())));
        } catch (IOException e) {
            JCMLogger.error("", e);
        }
    }

    protected abstract void fromJson(JsonObject jsonObject);

    protected abstract JsonObject toJson();
}
