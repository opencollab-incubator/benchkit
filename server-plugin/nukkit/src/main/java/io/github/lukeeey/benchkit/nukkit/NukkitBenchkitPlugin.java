package io.github.lukeeey.benchkit.nukkit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.plugin.PluginBase;
import io.github.lukeeey.benchkit.BenchkitPlatform;
import io.github.lukeeey.benchkit.BenchkitPlugin;
import io.github.lukeeey.benchkit.nukkit.command.BenchkitCommand;
import io.github.lukeeey.benchkit.nukkit.event.ModelApplyEvent;
import io.github.lukeeey.benchkit.nukkit.event.SkinApplyEvent;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NukkitBenchkitPlugin extends PluginBase implements BenchkitPlugin {
    private BenchkitPlatform platform;
    private InetSocketAddress address;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("geometry.json");

        address = new InetSocketAddress(getConfig().getString("address"), getConfig().getInt("port"));

        platform = new BenchkitPlatform(this);
        platform.enable();

        getServer().getCommandMap().register("benchkit", new BenchkitCommand(this));
    }

    @Override
    public void onDisable() {
        platform.disable();
    }

    @Override
    public boolean playerExists(UUID uuid) {
        return getServer().getPlayer(uuid).isPresent();
    }

    @Override
    public Map<UUID, String> getOnlinePlayers() {
        Map<UUID, String> players = new HashMap<>();
        getServer().getOnlinePlayers().forEach((uuid, player) -> players.put(uuid, player.getName()));
        return players;
    }

    @Override
    public void applySkin(UUID playerUuid, BufferedImage image) {
        Optional<Player> playerOptional = getServer().getPlayer(playerUuid);
        if (!playerOptional.isPresent()) {
            // TODO: Throw exception
            return;
        }

        Player player = playerOptional.get();
        Skin skin = new Skin();
        Skin oldSkin = player.getSkin();

        String name = UUID.randomUUID().toString();

        skin.setGeometryData(oldSkin.getGeometryData());
        skin.setSkinResourcePatch(oldSkin.getSkinResourcePatch());
        skin.setSkinData(image);
        skin.setSkinId(name);
        skin.setPremium(true);

        getServer().getPluginManager().callEvent(new SkinApplyEvent(player, skin));

        player.setSkin(skin);

        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.skin = skin;
        packet.newSkinName = name;
        packet.oldSkinName = oldSkin.getSkinId();
        packet.uuid = player.getUniqueId();

        Server.broadcastPacket(getServer().getOnlinePlayers().values(), packet);
    }

    @Override
    public void applySkinWithModel(UUID playerUuid, String identifier, String geometryData, BufferedImage image) {
        Optional<Player> playerOptional = getServer().getPlayer(playerUuid);
        if (!playerOptional.isPresent()) {
            // TODO: Throw exception
            return;
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

        getServer().getPluginManager().callEvent(new ModelApplyEvent(player, skin));

        player.setSkin(skin);

        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.skin = skin;
        packet.newSkinName = name;
        packet.oldSkinName = oldSkin.getSkinId();
        packet.uuid = player.getUniqueId();

        Server.broadcastPacket(getServer().getOnlinePlayers().values(), packet);
    }

    @Override
    public void scheduleDelayedTask(Runnable task, int delay) {
        getServer().getScheduler().scheduleDelayedTask(this, task, delay);
    }

    @Override
    public int getAuthenticationTimeout() {
        return getConfig().getInt("authenticationTimeout", 0);
    }

    @Override
    public String getAuthenticationKey() {
        return getConfig().getString("key");
    }

    @Override
    public Path getDataPath() {
        return getDataFolder().toPath();
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return address;
    }

    @Override
    public void logInfo(String message) {
        getLogger().info(message);
    }

    @Override
    public void logWarning(String message) {
        getLogger().warning(message);
    }

    @Override
    public void logError(String message) {
        getLogger().error(message);
    }
}
