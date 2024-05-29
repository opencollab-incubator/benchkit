package dev.opencollab.benchkit.cloudburst;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.player.Player;
import org.slf4j.Logger;

import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import com.nukkitx.protocol.bedrock.packet.PlayerSkinPacket;

import dev.opencollab.benchkit.BenchkitPlugin;
import dev.opencollab.benchkit.cloudburst.event.ModelApplyEvent;
import dev.opencollab.benchkit.cloudburst.event.SkinApplyEvent;

public class CloudBenchkitAdapter implements BenchkitPlugin {
    private final CloudBenchkitPlugin plugin;
    private final Server server;
    private final Logger logger;

    public CloudBenchkitAdapter(CloudBenchkitPlugin plugin, Server server, Logger logger) {
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;
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
            throw new IllegalArgumentException("Player not found");
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

        Server.broadcastPacket(server.getOnlinePlayers().values(), packet);
    }

    @Override
    public void applySkinWithModel(UUID playerUuid, String identifier, String geometryData, BufferedImage image) {
        Optional<Player> playerOptional = server.getPlayer(playerUuid);
        if (!playerOptional.isPresent()) {
            throw new IllegalArgumentException("Player not found");
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

        Server.broadcastPacket(server.getOnlinePlayers().values(), packet);
    }

    @Override
    public void scheduleDelayedTask(Runnable task, int delay) {
        server.getScheduler().scheduleDelayedTask(plugin.getContainer(), task, delay);
    }

    @Override
    public int getAuthenticationTimeout() {
        return plugin.getConfig().getAuthenticationTimeout();
    }

    @Override
    public String getAuthenticationKey() {
        return plugin.getConfig().getKey();
    }

    @Override
    public Path getDataPath() {
        return plugin.getDataFolder();
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return plugin.getAddress();
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
}
