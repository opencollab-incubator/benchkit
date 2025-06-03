package dev.opencollab.benchkit.geyser;

import dev.opencollab.benchkit.BenchkitPlugin;
import lombok.RequiredArgsConstructor;
import org.geysermc.geyser.api.command.CommandSource;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.skin.Skin;
import org.geysermc.geyser.api.skin.SkinData;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.skin.SkinManager;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GeyserBenchkitAdapter implements BenchkitPlugin {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final GeyserBenchkitExtension extension;
    private final BenchkitConfiguration config;

    @Override
    public boolean playerExists(UUID uuid) {
        return extension.geyserApi().connectionByUuid(uuid) != null;
    }

    @Override
    public Map<UUID, String> getOnlinePlayers() {
        return extension.geyserApi().onlineConnections()
                .stream()
                .collect(Collectors.toMap(CommandSource::playerUuid, CommandSource::name));
    }

    @Override
    public void applySkin(UUID playerUuid, BufferedImage image) {
        GeyserSession session = (GeyserSession) extension.geyserApi().connectionByUuid(playerUuid);
        // TODO
    }

    @Override
    public void applySkinWithModel(UUID playerUuid, String identifier, String geometryData, BufferedImage image) {

    }

    @Override
    public void scheduleDelayedTask(Runnable task, int delay) {
        // TODO: not sure if the geyser api has a way to do this
        executor.schedule(task, delay * 50L, TimeUnit.MILLISECONDS);
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
        return extension.dataFolder();
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return extension.getAddress();
    }

    @Override
    public void logInfo(String message) {
        extension.logger().info(message);
    }

    @Override
    public void logWarning(String message) {
        extension.logger().warning(message);
    }

    @Override
    public void logError(String message) {
        extension.logger().error(message);
    }
}
