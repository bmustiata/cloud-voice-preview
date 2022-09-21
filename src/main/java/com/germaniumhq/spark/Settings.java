package com.germaniumhq.spark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class Settings {
    private String ibmEndpoint;
    private String ibmToken;
    private String azureEndpoint;
    private String azureToken;

    private static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    public static final String SETTINGS_FILE_NAME =
            System.getenv("SPARK_VOICE_CONFIG") != null ?
            System.getenv("SPARK_VOICE_CONFIG") :
            "C:/Temp/spark-voice.yml";
    public static Settings INSTANCE = Settings.loadFromFile(SETTINGS_FILE_NAME);

    public String getIbmEndpoint() {
        return ibmEndpoint;
    }

    public void setIbmEndpoint(String ibmEndpoint) {
        this.ibmEndpoint = ibmEndpoint;
    }

    public String getIbmToken() {
        return ibmToken;
    }

    public void setIbmToken(String ibmToken) {
        this.ibmToken = ibmToken;
    }

    public String getAzureEndpoint() {
        return azureEndpoint;
    }

    public void setAzureEndpoint(String azureEndpoint) {
        this.azureEndpoint = azureEndpoint;
    }

    public String getAzureToken() {
        return azureToken;
    }

    public void setAzureToken(String azureToken) {
        this.azureToken = azureToken;
    }

    public static Settings loadFromFile(String fileName) {
        try {
            return mapper.readValue(new File(fileName), Settings.class);
        } catch (IOException e) {
            new IllegalArgumentException(String.format(
                    "Unable to load: %s",
                    fileName
            ), e).printStackTrace();
        }

        // we return empty settings in the case of the initial load
        return new Settings();
    }

    public static void saveToFile(String fileName, Settings settings) {
        try {
            mapper.writeValue(new File(fileName), settings);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(
                    "Unable to save: %s",
                    fileName
            ), e);
        }
    }
}
