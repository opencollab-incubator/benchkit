package io.github.lukeeey.benchkit.socket.tasks;

import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.BenchkitPlugin;
import lombok.RequiredArgsConstructor;
import org.java_websocket.WebSocket;

@RequiredArgsConstructor
public abstract class SocketTask {
    protected final BenchkitPlugin plugin;
    private final String name;

    public abstract void execute(WebSocket socket, JsonObject data);

    protected void sendToSocket(WebSocket socket, JsonObject data) {
        plugin.sendToSocket(socket, name, data);
    }
}
