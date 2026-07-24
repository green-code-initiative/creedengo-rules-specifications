package org.greencodeinitiative.mavenbuild.ruleexporter.infra;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    private static final String CONFIG_FILE_PATH = "/config.json";

    private static JsonObject config;

    private ConfigLoader() {
        // Private constructor to prevent instantiation
    }

    public static void load() {
        try (
                InputStream resourceStream = ConfigLoader.class.getResourceAsStream(CONFIG_FILE_PATH);
                JsonReader reader = Json.createReader(resourceStream)
        ) {
            config = reader.readObject();
        } catch (IOException ex) {
            throw new ProcessException("Failed to load configuration from " + CONFIG_FILE_PATH, ex);
        }
    }

    public static Map<String, String> getLanguageLabels() {
        Map<String, String> labels = new HashMap<>();
        if (config.containsKey("languages")) {
            JsonObject languages = config.getJsonObject("languages");
            for (String key : languages.keySet()) {
                labels.put(key, languages.getString(key));
            }
        }
        return labels;
    }

}
