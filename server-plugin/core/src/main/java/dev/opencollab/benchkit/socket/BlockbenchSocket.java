package dev.opencollab.benchkit.socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.opencollab.benchkit.BenchkitPlugin;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The socket connection between the server and the Blockbench plugin.
 */
public class BlockbenchSocket extends WebSocketServer {
    private final IntList authenticatedSockets = new IntArrayList();
    private final BenchkitPlugin plugin;

    public BlockbenchSocket(BenchkitPlugin plugin, InetSocketAddress address) {
        super(address);
        this.plugin = plugin;
    }

    public void sendToSocket(WebSocket socket, String type, JsonObject data) {
        JsonObject object = new JsonObject();

        object.addProperty("type", type);
        object.addProperty("key", createSHA256Hash(plugin.getAuthenticationKey()));

        if (data != null) {
            object.add("data", data);
        }
        socket.send(object.toString());
    }

    @Override
    public void onOpen(WebSocket socket, ClientHandshake handshake) {
        plugin.scheduleDelayedTask(() -> {
            if (!authenticatedSockets.contains(socket.hashCode())) {
                socket.close(1000, "Failed to authenticate in time");
            }
        }, plugin.getAuthenticationTimeout() * 20);
    }

    @Override
    public void onClose(WebSocket socket, int code, String reason, boolean remote) {
        authenticatedSockets.removeInt(socket.hashCode());
    }

    @Override
    public void onMessage(WebSocket socket, String message) {
        JsonObject object = new JsonParser().parse(message).getAsJsonObject();
        String type = object.get("type").getAsString();

        int socketHash = socket.hashCode();

        if (type.equalsIgnoreCase("authenticate")) {
            String key = object.get("key").getAsString();
            String serverKey = createSHA256Hash(plugin.getAuthenticationKey());

            if (key.equalsIgnoreCase(serverKey)) {
                sendToSocket(socket, "authenticate", null);
                authenticatedSockets.add(socketHash);

                plugin.logInfo("Successfully authenticated with Blockbench client!");
            } else {
                socket.close(1000, "Failed to authenticate");
            }
            return;
        }

        if (!authenticatedSockets.contains(socketHash)) {
            socket.close(1000, "Not authenticated");
            return;
        }

        JsonObject data = object.get("data").getAsJsonObject();
        SocketMessages.parseMessage(type, socket, data);
    }

    @Override
    public void onError(WebSocket socket, Exception e) {
        plugin.logWarning("An error has occurred with the socket server: " + e.getMessage());
    }

    @Override
    public void onStart() {
        plugin.logInfo("Socket server started!");
    }

    private String createSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            plugin.logError("Failed to create SHA-256 hash: " + ex.getMessage());
            return null;
        }
    }
}