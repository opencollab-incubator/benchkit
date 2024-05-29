package dev.opencollab.benchkit.cloudburst;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.server.ServerInitializationEvent;
import org.cloudburstmc.server.event.server.ServerShutdownEvent;
import org.cloudburstmc.server.plugin.Plugin;
import org.cloudburstmc.server.plugin.PluginContainer;
import org.cloudburstmc.server.plugin.PluginDescription;
import org.slf4j.Logger;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.Inject;

import dev.opencollab.benchkit.BenchkitPlatform;
import dev.opencollab.benchkit.cloudburst.command.BenchkitCommand;
import lombok.Getter;

@Getter
@Plugin(
        id = "benchkit",
        name = "Benchkit",
        version = "1.0.0",
        authors = {"lukeeey"},
        url = "https://github.com/opencollab-incubator/benchkit"
)
public class CloudBenchkitPlugin {
    public static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    private final Server server;
    private final Logger logger;
    private final PluginDescription description;
    private final Path dataFolder;

    private PluginContainer container;

    private InetSocketAddress address;

    private BenchkitConfiguration config;
    private BenchkitPlatform platform;

    @Inject
    public CloudBenchkitPlugin(Server server, Logger logger, PluginDescription description, Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.description = description;
        this.dataFolder = dataDirectory;
    }

    @Listener
    public void onInitialization(ServerInitializationEvent event) {
        this.container = server.getPluginManager().fromInstance(this).orElseThrow(() ->
                new RuntimeException("Failed to get plugin container instance"));

        loadConfig();

        address = new InetSocketAddress(config.getAddress(), config.getPort());

        platform = new BenchkitPlatform(new CloudBenchkitAdapter(this, server, logger));
        platform.enable();

        server.getCommandRegistry().register(container, new BenchkitCommand(this));
        server.getEventManager().registerListeners(this, this);
    }

    @Listener
    public void onShutdown(ServerShutdownEvent event) {
       platform.disable();
    }

    /// CONFIG STUFF
    private void loadConfig() {
        createDefaultConfiguration("config.yml"); // Create the default configuration file

        try {
            config = YAML_MAPPER.readValue(new File(dataFolder.toFile(), "config.yml"), BenchkitConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void createDefaultConfiguration(String name) {
        Path path = this.dataFolder.resolve(name);
        if (Files.notExists(path)) {
            try (InputStream stream = CloudBenchkitPlugin.class.getClassLoader().getResourceAsStream(name)) {
                if (stream == null) {
                    Files.createDirectories(path.getParent());
                    Files.createFile(path);
                }
                copyDefaultConfig(stream, path, name);
            } catch (IOException e) {
                logger.warn("Unable to read default configuration: " + name);
            }
        }
    }

    private void copyDefaultConfig(InputStream input, Path actual, String name) {
        try {
            Files.copy(input, actual, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Default configuration file written: " + name);
        } catch (IOException e) {
            logger.warn("Failed to write default config file", e);
        }
    }
}
