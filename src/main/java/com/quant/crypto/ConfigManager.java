package com.quant.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class responsible for loading and managing application configuration settings.
 * It reads key-value pairs from the 'application.properties' file located in the classpath.
 * <p>
 * This class follows the Singleton pattern approach using a static block for initialization.
 */
public class ConfigManager {

    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "application.properties";

    /*
     * Static initialization block to load configuration when the class is first accessed.
     */
    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("ERROR: Unable to find " + CONFIG_FILE);
                System.err.println("Please make sure the file exists in 'src/main/resources'");
            } else {
                properties.load(input);
                System.out.println("Configuration loaded successfully.");
            }
        } catch (IOException ex) {
            System.err.println("ERROR: Failed to load configuration file.");
            ex.printStackTrace();
        }
    }

    // Private constructor to prevent instantiation
    private ConfigManager() {}

    /**
     * Retrieves a string value for the specified configuration key.
     *
     * @param key The key of the property to retrieve.
     * @return The value associated with the key, or null if not found.
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves an integer value for the specified configuration key.
     * Useful for fetching numeric thresholds (e.g., limits, timeouts).
     *
     * @param key The key of the property to retrieve.
     * @return The integer value of the key.
     * @throws NumberFormatException if the value cannot be parsed as an integer.
     */
    public static int getInt(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Key not found in config: " + key);
        }
        return Integer.parseInt(value);
    }
}