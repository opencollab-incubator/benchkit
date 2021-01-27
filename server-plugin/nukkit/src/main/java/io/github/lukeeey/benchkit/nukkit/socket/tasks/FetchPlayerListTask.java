package io.github.lukeeey.benchkit.nukkit.socket.tasks;

import cn.nukkit.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.nukkit.BenchkitPlugin;
import org.java_websocket.WebSocket;

import java.util.Collection;

public class FetchPlayerListTask extends SocketTask {

    public FetchPlayerListTask(BenchkitPlugin plugin) {
        super(plugin, "fetch_player_list");
    }

    @Override
    public void execute(WebSocket socket, JsonObject data) {
        Collection<Player> players = plugin.getServer().getOnlinePlayers().values();
        JsonArray array = new JsonArray();

        players.forEach(player -> {
            JsonObject playerObject = new JsonObject();
            playerObject.addProperty("uuid", player.getUniqueId().toString());
            playerObject.addProperty("name", player.getName());
            array.add(playerObject);
        });

        JsonObject object = new JsonObject();
        object.add("players", array);

        plugin.getLogger().warning("Sending player list to socket: " + players.size() + " (jsonarr: " + array.size() + ")");

        sendToSocket(socket, object);
    }
}
