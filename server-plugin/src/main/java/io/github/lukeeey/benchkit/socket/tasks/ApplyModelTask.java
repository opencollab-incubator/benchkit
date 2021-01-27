package io.github.lukeeey.benchkit.socket.tasks;

import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.BenchkitPlugin;
import org.java_websocket.WebSocket;

public class ApplyModelTask extends SocketTask {

    public ApplyModelTask(BenchkitPlugin plugin) {
        super(plugin, "apply_model");
    }

    @Override
    public void execute(WebSocket socket, JsonObject data) {

    }
}
