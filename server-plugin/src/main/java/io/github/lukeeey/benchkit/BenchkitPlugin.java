package io.github.lukeeey.benchkit;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.command.BenchkitCommand;
import io.github.lukeeey.benchkit.socket.BlockbenchSocket;
import lombok.Getter;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;

@Getter
public class BenchkitPlugin extends PluginBase {
    private String key;
    private InetSocketAddress address;
    private int authenticationTimeout;

    private BlockbenchSocket socketServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("geometry.json");

        key = getConfig().getString("key");
        address = new InetSocketAddress(getConfig().getString("address"), getConfig().getInt("port"));
        authenticationTimeout = getConfig().getInt("authenticationTimeout");

        socketServer = new BlockbenchSocket(this, address);
        socketServer.start();

        getServer().getCommandMap().register("benchkit", new BenchkitCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            socketServer.stop();
        } catch (InterruptedException | IOException e) {
            getLogger().warning("Failed to stop socket server: " + e.getMessage());
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

        getLogger().notice(TextFormat.AQUA + object.toString());
    }
}
