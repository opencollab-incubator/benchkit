package io.github.lukeeey.benchkit.cloudburst.socket.tasks;

import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.Server;
import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.cloudburst.BenchkitPlugin;
import io.github.lukeeey.benchkit.cloudburst.event.SkinApplyEvent;
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
                SerializedSkin oldSkin = player.getSkin();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
