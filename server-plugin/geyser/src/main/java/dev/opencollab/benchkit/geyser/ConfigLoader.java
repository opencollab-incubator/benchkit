package dev.opencollab.benchkit.geyser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class ConfigLoader {

    public static BenchkitConfiguration loadConfig(GeyserBenchkitExtension extension) {
        createDefaultConfiguration(extension, "config.yml");

        try {
            return new ObjectMapper(new YAMLFactory())
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(new File(extension.dataFolder().toFile(), "config.yml"), BenchkitConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static void createDefaultConfiguration(GeyserBenchkitExtension extension, String name) {
        Path path = extension.dataFolder().resolve(name);
        if (Files.notExists(path)) {
            try (InputStream stream = GeyserBenchkitExtension.class.getClassLoader().getResourceAsStream(name)) {
                if (stream == null) {
                    Files.createDirectories(path.getParent());
                    Files.createFile(path);
                }
                copyDefaultConfig(extension, stream, path, name);
            } catch (IOException e) {
                extension.logger().warning("Unable to read default configuration: " + name);
            }
        }
    }

    private static void copyDefaultConfig(GeyserBenchkitExtension extension, InputStream input, Path actual, String name) {
        try {
            Files.copy(input, actual, StandardCopyOption.REPLACE_EXISTING);
            extension.logger().info("Default configuration file written: " + name);
        } catch (IOException e) {
            extension.logger().warning("Failed to write default config file: " + e.getMessage());
        }
    }
}