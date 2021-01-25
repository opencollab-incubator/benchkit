package io.github.lukeeey.skin2server.socket;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.SerializedImage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.lukeeey.skin2server.Skin2ServerPlugin;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.List;

public class BlockbenchSocket extends WebSocketServer {
    private final List<WebSocket> authenticatedSockets = new ArrayList<>();
    private final Skin2ServerPlugin plugin;

    public BlockbenchSocket(Skin2ServerPlugin plugin, InetSocketAddress address) {
        super(address);
        this.plugin = plugin;
    }

    @Override
    public void onOpen(WebSocket socket, ClientHandshake handshake) {
        plugin.getLogger().warning("onOpen!!");
        plugin.getServer().getScheduler().scheduleDelayedTask(plugin, () -> {
            if (!authenticatedSockets.contains(socket)) {
                socket.close(1000, "Failed to authenticate in time");
            }
        }, 20 * 5);
    }

    @Override
    public void onClose(WebSocket socket, int code, String reason, boolean remote) {
        plugin.getLogger().warning(code + " remote: " + remote + " - " + reason);
        authenticatedSockets.remove(socket);
    }

    @Override
    public void onMessage(WebSocket socket, String message) {
        plugin.getLogger().warning(message);
        JsonObject object = new JsonParser().parse(message).getAsJsonObject();
        String type = object.get("type").getAsString();

        if (type.equalsIgnoreCase("authenticate")) {
            String key = object.get("key").getAsString();
            if (key.equalsIgnoreCase(plugin.getKey())) {
                sendToSocket(socket, "authenticate", null);
                authenticatedSockets.add(socket);
                plugin.getLogger().warning("Authenticated!");
            } else {
                socket.close(1000, "Failed to authenticate");
            }
            return;
        }

        if (!authenticatedSockets.contains(socket)) {
            socket.close(1000, "Not authenticated");
            return;
        }

        JsonObject data = object.get("data").getAsJsonObject();

        switch (type) {
            case "apply_skin":
                UUID entityUuid = UUID.fromString(data.get("entityUuid").getAsString());
                String textureImage = data.get("texture").getAsString().split(",")[1];
                byte[] b64data = Base64.getDecoder().decode(textureImage);

                try {
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(b64data));
                    Optional<Player> playerOptional = plugin.getServer().getPlayer(entityUuid);

                    if (playerOptional.isPresent()) {
                        Player player = playerOptional.get();
                        Skin skin = player.getSkin();
                        skin.setSkinId(UUID.randomUUID().toString());
                        skin.setSkinData(image);

                        player.setSkin(skin);

                        plugin.getLogger().warning("Image size: " + image.getWidth() + " h: " + image.getHeight());
                        plugin.getLogger().info("changed player skin");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void sendToSocket(WebSocket socket, String type, JsonObject data) {
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.addProperty("key", plugin.getKey());

        if (data != null) {
            object.addProperty("data", data.toString());
        }
        socket.send(object.toString());
    }

    @Override
    public void onError(WebSocket socket, Exception e) {
        plugin.getLogger().warning("onError: " + e.getMessage());
    }

    @Override
    public void onStart() {
        plugin.getLogger().warning("onStart");
    }
}
