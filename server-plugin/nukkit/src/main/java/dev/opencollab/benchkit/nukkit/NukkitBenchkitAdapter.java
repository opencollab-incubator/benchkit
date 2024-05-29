package dev.opencollab.benchkit.nukkit;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import dev.opencollab.benchkit.BenchkitPlugin;
import dev.opencollab.benchkit.nukkit.event.ModelApplyEvent;
import dev.opencollab.benchkit.nukkit.event.SkinApplyEvent;

public class NukkitBenchkitAdapter implements BenchkitPlugin {
    private final NukkitBenchkitPlugin plugin;

    public NukkitBenchkitAdapter(NukkitBenchkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean playerExists(UUID uuid) {
        return plugin.getServer().getPlayer(uuid).isPresent();
    }

    @Override
    public Map<UUID, String> getOnlinePlayers() {
        Map<UUID, String> players = new HashMap<>();
        plugin.getServer().getOnlinePlayers().forEach((uuid, player) -> players.put(uuid, player.getName()));
        return players;
    }

    @Override
    public void applySkin(UUID playerUuid, BufferedImage image) {
        Optional<Player> playerOptional = plugin.getServer().getPlayer(playerUuid);
        if (!playerOptional.isPresent()) {
            throw new IllegalArgumentException("Player not found");
        }

        Player player = playerOptional.get();
        Skin skin = new Skin();
        Skin oldSkin = player.getSkin();

        String name = UUID.randomUUID().toString();

        // TODO: This only works witn classic skins at the moment
        skin.setGeometryData(oldSkin.getGeometryData());
        skin.setSkinResourcePatch(oldSkin.getSkinResourcePatch());
        skin.setSkinData(image);
        skin.setSkinId(name);
        skin.setPremium(true);

        plugin.getServer().getPluginManager().callEvent(new SkinApplyEvent(player, skin));

        player.setSkin(skin);

        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.skin = skin;
        packet.newSkinName = name;
        packet.oldSkinName = oldSkin.getSkinId();
        packet.uuid = player.getUniqueId();

        Server.broadcastPacket(plugin.getServer().getOnlinePlayers().values(), packet);
    }

    @Override
    public void applySkinWithModel(UUID playerUuid, String identifier, String geometryData, BufferedImage image) {
        Optional<Player> playerOptional = plugin.getServer().getPlayer(playerUuid);
        if (!playerOptional.isPresent()) {
            throw new IllegalArgumentException("Player not found");
        }

        Player player = playerOptional.get();
        Skin oldSkin = player.getSkin();
        Skin skin = new Skin();

        String name = UUID.randomUUID().toString();

        skin.setGeometryData(geometryData);
        skin.setGeometryName("geometry." + identifier);
        skin.setSkinData(image);
        skin.setSkinId(name);
        skin.setPremium(true);

        plugin.getServer().getPluginManager().callEvent(new ModelApplyEvent(player, skin));

        player.setSkin(skin);

        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.skin = skin;
        packet.newSkinName = name;
        packet.oldSkinName = oldSkin.getSkinId();
        packet.uuid = player.getUniqueId();

        Server.broadcastPacket(plugin.getServer().getOnlinePlayers().values(), packet);
    }

    @Override
    public void scheduleDelayedTask(Runnable task, int delay) {
        plugin.getServer().getScheduler().scheduleDelayedTask(plugin, task, delay);
    }

    @Override
    public int getAuthenticationTimeout() {
        return plugin.getConfig().getInt("authenticationTimeout", 0);
    }

    @Override
    public String getAuthenticationKey() {
        return plugin.getConfig().getString("key");
    }

    @Override
    public Path getDataPath() {
        return plugin.getDataFolder().toPath();
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return plugin.getAddress();
    }

    @Override
    public void logInfo(String message) {
        plugin.getLogger().info(message);
    }

    @Override
    public void logWarning(String message) {
        plugin.getLogger().warning(message);
    }

    @Override
    public void logError(String message) {
        plugin.getLogger().error(message);
    }
}
