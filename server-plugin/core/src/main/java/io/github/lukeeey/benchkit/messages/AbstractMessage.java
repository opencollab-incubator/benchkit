package io.github.lukeeey.benchkit.messages;

import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.BenchkitPlatform;
import org.java_websocket.WebSocket;

public interface AbstractMessage {

    void parse(WebSocket socket, JsonObject data);

    // im just being lazy, todo
    default void sendToSocket(WebSocket socket, String type, JsonObject data) {
        BenchkitPlatform.instance.socketServer.sendToSocket(socket, type, data);
    }
}
