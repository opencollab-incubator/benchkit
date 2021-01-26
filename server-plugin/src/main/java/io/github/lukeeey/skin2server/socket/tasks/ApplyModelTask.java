package io.github.lukeeey.skin2server.socket.tasks;

import com.google.gson.JsonObject;
import io.github.lukeeey.skin2server.Skin2ServerPlugin;
import org.java_websocket.WebSocket;

public class ApplyModelTask extends SocketTask {

    public ApplyModelTask(Skin2ServerPlugin plugin) {
        super(plugin, "apply_model");
    }

    @Override
    public void execute(WebSocket socket, JsonObject data) {

    }
}
