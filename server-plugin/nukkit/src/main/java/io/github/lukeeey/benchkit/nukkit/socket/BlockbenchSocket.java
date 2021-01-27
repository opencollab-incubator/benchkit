package io.github.lukeeey.benchkit.nukkit.socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.lukeeey.benchkit.nukkit.BenchkitPlugin;
import io.github.lukeeey.benchkit.nukkit.socket.tasks.ApplyModelTask;
import io.github.lukeeey.benchkit.nukkit.socket.tasks.ApplySkinTask;
import io.github.lukeeey.benchkit.nukkit.socket.tasks.FetchPlayerListTask;
import io.github.lukeeey.benchkit.nukkit.socket.tasks.SocketTask;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockbenchSocket extends WebSocketServer {
    private final List<WebSocket> authenticatedSockets = new ObjectArrayList<>();
    private final Map<String, SocketTask> tasks = new HashMap<>();

    private final BenchkitPlugin plugin;

    public BlockbenchSocket(BenchkitPlugin plugin, InetSocketAddress address) {
        super(address);
        this.plugin = plugin;

        tasks.put("apply_skin", new ApplySkinTask(plugin));
        tasks.put("apply_model", new ApplyModelTask(plugin));
        tasks.put("fetch_player_list", new FetchPlayerListTask(plugin));
    }

    @Override
    public void onOpen(WebSocket socket, ClientHandshake handshake) {
        plugin.getLogger().info("Socket opened");

        plugin.getServer().getScheduler().scheduleDelayedTask(plugin, () -> {
            if (!authenticatedSockets.contains(socket)) {
                socket.close(1000, "Failed to authenticate in time");
            }
        }, plugin.getAuthenticationTimeout() * 20);
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
                plugin.sendToSocket(socket, "authenticate", null);
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

        if (tasks.containsKey(type)) {
            tasks.get(type).execute(socket, data);
            return;
        }
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
