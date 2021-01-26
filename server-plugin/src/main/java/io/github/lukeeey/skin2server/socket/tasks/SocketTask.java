package io.github.lukeeey.skin2server.socket.tasks;

import com.google.gson.JsonObject;
import io.github.lukeeey.skin2server.Skin2ServerPlugin;
import lombok.RequiredArgsConstructor;
import org.java_websocket.WebSocket;

@RequiredArgsConstructor
public abstract class SocketTask {
    protected final Skin2ServerPlugin plugin;
    private final String name;

    public abstract void execute(WebSocket socket, JsonObject data);

    protected void sendToSocket(WebSocket socket, JsonObject data) {
        plugin.sendToSocket(socket, name, data);
    }
}
