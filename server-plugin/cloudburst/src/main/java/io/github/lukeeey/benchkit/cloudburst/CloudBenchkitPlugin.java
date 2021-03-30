package io.github.lukeeey.benchkit.cloudburst;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.Inject;
import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import com.nukkitx.protocol.bedrock.packet.PlayerSkinPacket;
import io.github.lukeeey.benchkit.BenchkitPlatform;
import io.github.lukeeey.benchkit.BenchkitPlugin;
import io.github.lukeeey.benchkit.cloudburst.command.BenchkitCommand;
import io.github.lukeeey.benchkit.cloudburst.event.ModelApplyEvent;
import io.github.lukeeey.benchkit.cloudburst.event.SkinApplyEvent;
import lombok.Getter;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.server.ServerInitializationEvent;
import org.cloudburstmc.server.event.server.ServerShutdownEvent;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.plugin.Plugin;
import org.cloudburstmc.server.plugin.PluginContainer;
import org.cloudburstmc.server.plugin.PluginDescription;
import org.slf4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
@Plugin(
        id = "benchkit",
        name = "Benchkit",
        version = "1.0.1",
        authors = {"lukeeey"},
        url = "https://github.com/opencollab-incubator/benchkit"
)
public class CloudBenchkitPlugin implements BenchkitPlugin {
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

        platform = new BenchkitPlatform(this);
        platform.enable();

        server.getCommandRegistry().register(container, new BenchkitCommand(this));
        server.getEventManager().registerListeners(this, this);
    }

    @Listener
    public void onShutdown(ServerShutdownEvent event) {
       platform.disable();
    }

    @Override
    public boolean playerExists(UUID uuid) {
        return server.getPlayer(uuid).isPresent();
    }

    @Override
    public Map<UUID, String> getOnlinePlayers() {
        Map<UUID, String> players = new HashMap<>();
        server.getOnlinePlayers().forEach((uuid, player) -> players.put(uuid, player.getName()));
        return players;
    }

    @Override
    public void applySkin(UUID playerUuid, BufferedImage image) {
        Optional<Player> playerOptional = server.getPlayer(playerUuid);
        if (!playerOptional.isPresent()) {
            // TODO: Throw exception
            return;
        }

        Player player = playerOptional.get();
        SerializedSkin oldSkin = player.getSkin();
        String name = UUID.randomUUID().toString();

        SerializedSkin skin = SerializedSkin.builder()
                .geometryData(oldSkin.getGeometryData())
                .skinResourcePatch(oldSkin.getSkinResourcePatch())
                .skinData(ImageData.from(image))
                .skinId(name)
                .premium(true)
                .build();

        server.getEventManager().fire(new SkinApplyEvent(player, skin));

        player.setSkin(skin);

        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.setSkin(skin);
        packet.setNewSkinName(name);
        packet.setOldSkinName(oldSkin.getSkinId());
        packet.setUuid(player.getServerId());

        Server.broadcastPacket(getServer().getOnlinePlayers().values(), packet);
    }

    @Override
    public void applySkinWithModel(UUID playerUuid, String identifier, String geometryData, BufferedImage image) {
        Optional<Player> playerOptional = server.getPlayer(playerUuid);
        if (!playerOptional.isPresent()) {
            // TODO: Throw exception
            return;
        }

        Player player = playerOptional.get();
        SerializedSkin oldSkin = player.getSkin();
        String name = UUID.randomUUID().toString();

        SerializedSkin skin = SerializedSkin.builder()
                .geometryData(geometryData)
                .geometryName("geometry." + identifier)
                .skinData(ImageData.from(image))
                .skinId(name)
                .premium(true)
                .build();

        server.getEventManager().fire(new ModelApplyEvent(player, skin));

        player.setSkin(skin);

        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.setSkin(skin);
        packet.setNewSkinName(name);
        packet.setOldSkinName(oldSkin.getSkinId());
        packet.setUuid(player.getServerId());

        Server.broadcastPacket(getServer().getOnlinePlayers().values(), packet);
    }

    @Override
    public void scheduleDelayedTask(Runnable task, int delay) {
        server.getScheduler().scheduleDelayedTask(this, task, delay);
    }

    @Override
    public int getAuthenticationTimeout() {
        return config.getAuthenticationTimeout();
    }

    @Override
    public String getAuthenticationKey() {
        return config.getKey();
    }

    @Override
    public Path getDataPath() {
        return dataFolder;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return address;
    }

    @Override
    public void logInfo(String message) {
        logger.info(message);
    }

    @Override
    public void logWarning(String message) {
        logger.warn(message);
    }

    @Override
    public void logError(String message) {
        logger.error(message);
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
