package io.github.lukeeey.benchkit.nukkit.socket.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.nukkit.BenchkitPlugin;
import io.github.lukeeey.benchkit.nukkit.event.ModelApplyEvent;
import org.java_websocket.WebSocket;

import java.util.Optional;
import java.util.UUID;

public class ApplyModelTask extends SocketTask {

    public ApplyModelTask(BenchkitPlugin plugin) {
        super(plugin, "apply_model");
    }

    @Override
    public void execute(WebSocket socket, JsonObject data) {
        UUID entityUuid = UUID.fromString(data.get("entityUuid").getAsString());
        String modelData = data.get("model").getAsString();

        Optional<Player> playerOptional = plugin.getServer().getPlayer(entityUuid);

        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            Skin oldSkin = player.getSkin();

            Skin skin = new Skin();
            String name = UUID.randomUUID().toString();

            skin.setGeometryData(modelData);
            skin.setGeometryName("geometry.humanoid.customSlim");//setSkinResourcePatch(oldSkin.getSkinResourcePatch());
            skin.setSkinData(oldSkin.getSkinData());
            skin.setSkinId(name);
            skin.setPremium(true);

            plugin.getLogger().warning(oldSkin.getSkinResourcePatch());

            plugin.getServer().getPluginManager().callEvent(new ModelApplyEvent(player, skin));

            player.setSkin(skin);

            PlayerSkinPacket packet = new PlayerSkinPacket();
            packet.skin = skin;
            packet.newSkinName = name;
            packet.oldSkinName = oldSkin.getSkinId();
            packet.uuid = player.getUniqueId();

            Server.broadcastPacket(plugin.getServer().getOnlinePlayers().values(), packet);

            plugin.getLogger().info("changed player model");
        }
    }
}
