package io.github.lukeeey.benchkit.nukkit.socket.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.nukkit.BenchkitPlugin;
import io.github.lukeeey.benchkit.nukkit.event.SkinApplyEvent;
import org.java_websocket.WebSocket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class ApplySkinTask extends SocketTask {

    public ApplySkinTask(BenchkitPlugin plugin) {
        super(plugin, "apply_skin");
    }

    @Override
    public void execute(WebSocket socket, JsonObject data) {
        UUID entityUuid = UUID.fromString(data.get("entityUuid").getAsString());
        String textureImage = data.get("texture").getAsString().split(",")[1];

        byte[] b64data = Base64.getDecoder().decode(textureImage);

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(b64data));
            Optional<Player> playerOptional = plugin.getServer().getPlayer(entityUuid);

            if (playerOptional.isPresent()) {
                Player player = playerOptional.get();
                Skin oldSkin = player.getSkin();

                Skin skin = new Skin();
                String name = UUID.randomUUID().toString();
                String geometry;

                Path skinGeometryPath = plugin.getDataFolder().toPath().resolve("geometry.json");

                try {
                    geometry = new String(Files.readAllBytes(skinGeometryPath), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException("Error loading data", e);
                }

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

                plugin.getLogger().warning("Image size: " + image.getWidth() + " h: " + image.getHeight());
                plugin.getLogger().info("changed player skin");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
