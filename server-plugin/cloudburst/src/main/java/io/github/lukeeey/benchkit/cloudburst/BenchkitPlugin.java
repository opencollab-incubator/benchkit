package io.github.lukeeey.benchkit.cloudburst;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.Inject;
import io.github.lukeeey.benchkit.cloudburst.BenchkitConfiguration;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.player.PlayerJoinEvent;
import org.cloudburstmc.server.event.server.ServerInitializationEvent;
import org.cloudburstmc.server.event.server.ServerShutdownEvent;
import org.cloudburstmc.server.plugin.Plugin;
import org.cloudburstmc.server.plugin.PluginContainer;
import org.cloudburstmc.server.utils.TextFormat;
import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.cloudburst.command.BenchkitCommand;
import io.github.lukeeey.benchkit.cloudburst.socket.BlockbenchSocket;
import lombok.Getter;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Getter
@Plugin(id = "benchkit", name = "Benchkit", version = "1.0.0")
public class BenchkitPlugin {
    public static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    private String key;
    private InetSocketAddress address;
    private int authenticationTimeout;

    private BlockbenchSocket socketServer;

    private Logger logger;
    private PluginContainer container;
    private Path dataFolder;

    private BenchkitConfiguration config;

    @Inject
    public BenchkitPlugin(Logger logger, PluginContainer container) {
        this.logger = logger;
        this.container = container;
        this.dataFolder = container.getDataDirectory();
    }

    @Listener
    public void onInitialization(ServerInitializationEvent event) {
        loadConfig();

        key = getConfig().getKey();
        address = new InetSocketAddress(getConfig().getAddress(), getConfig().getPort());
        authenticationTimeout = getConfig().getAuthenticationTimeout();

        socketServer = new BlockbenchSocket(this, address);
        socketServer.start();

        getServer().getCommandRegistry().register(container, new BenchkitCommand(this));
        getServer().getEventManager().registerListeners(this, this);
    }

    @Listener
    public void onJoin(PlayerJoinEvent event) {
        getLogger().info("geo: " + event.getPlayer().getSkin().getGeometryData());
    }

    @Listener
    public void onShutdown(ServerShutdownEvent event) {
        try {
            socketServer.stop();
        } catch (InterruptedException | IOException e) {
            getLogger().warn("Failed to stop socket server: " + e.getMessage());
        }
    }

    public void sendToSocket(WebSocket socket, String type, JsonObject data) {
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.addProperty("key", key);

        if (data != null) {
            object.add("data", data);
        }
        socket.send(object.toString());

        getLogger().info(TextFormat.AQUA + object.toString());
    }

    public Server getServer() {
        return Server.getInstance();
    }

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
            try (InputStream stream = BenchkitPlugin.class.getClassLoader().getResourceAsStream(name)) {
                if (stream == null) {
                    Files.createDirectories(path.getParent());
                    Files.createFile(path);
                }
                copyDefaultConfig(stream, path, name);
            } catch (IOException e) {
                getLogger().warn("Unable to read default configuration: " + name);
            }
        }
    }

    private void copyDefaultConfig(InputStream input, Path actual, String name) {
        try {
            Files.copy(input, actual, StandardCopyOption.REPLACE_EXISTING);

            getLogger().info("Default configuration file written: " + name);
        } catch (IOException e) {
            getLogger().warn("Failed to write default config file", e);
        }
    }
}
