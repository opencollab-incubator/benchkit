package dev.opencollab.benchkit.messages;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.opencollab.benchkit.BenchkitPlugin;
import lombok.RequiredArgsConstructor;
import org.java_websocket.WebSocket;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class FetchPlayerListMessage implements AbstractMessage {
    private final BenchkitPlugin plugin;

    @Override
    public void parse(WebSocket socket, JsonObject data) {
        Map<UUID, String> players = plugin.getOnlinePlayers();

        JsonArray array = new JsonArray();
        players.forEach(this::buildPlayerObject);

        JsonObject object = new JsonObject();
        object.add("players", array);

        sendToSocket(socket, "fetch_player_list", object);
    }

    private JsonObject buildPlayerObject(UUID uuid, String name) {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("name", name);
        return object;
    }
}