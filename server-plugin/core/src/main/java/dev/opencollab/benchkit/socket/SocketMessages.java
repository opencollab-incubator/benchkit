package dev.opencollab.benchkit.socket;

import com.google.gson.JsonObject;
import dev.opencollab.benchkit.messages.ApplyModelMessage;
import dev.opencollab.benchkit.BenchkitPlugin;
import dev.opencollab.benchkit.messages.AbstractMessage;
import dev.opencollab.benchkit.messages.ApplySkinMessage;
import dev.opencollab.benchkit.messages.FetchPlayerListMessage;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;

public class SocketMessages {
    private static SocketMessages instance;
    private final Map<String, AbstractMessage> messages = new HashMap<>();

    public SocketMessages(BenchkitPlugin plugin) {
        instance = this;

        messages.put("apply_skin", new ApplySkinMessage(plugin));
        messages.put("apply_model", new ApplyModelMessage(plugin));
        messages.put("fetch_player_list", new FetchPlayerListMessage(plugin));
    }

    public static AbstractMessage getMessage(String name) {
        return instance.messages.get(name);
    }

    public static void parseMessage(String name, WebSocket socket, JsonObject data) {
        if (instance.messages.containsKey(name)) {
            instance.messages.get(name).parse(socket, data);
        }
    }
}
