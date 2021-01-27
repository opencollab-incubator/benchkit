package io.github.lukeeey.benchkit.cloudburst.socket.tasks;

import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.Server;
import com.google.gson.JsonObject;
import io.github.lukeeey.benchkit.cloudburst.BenchkitPlugin;
import io.github.lukeeey.benchkit.cloudburst.event.ModelApplyEvent;
import org.java_websocket.WebSocket;

import java.util.Optional;
import java.util.UUID;

public class ApplyModelTask extends SocketTask {

    public ApplyModelTask(BenchkitPlugin plugin) {
        super(plugin, "apply_model");
    }

    @Override
    public void execute(WebSocket socket, JsonObject data) {
        UUID entityUuid = UUID.fromString(data.get("entityUuid").getAsString());
        String modelData = data.get("model").getAsString();

        Optional<Player> playerOptional = plugin.getServer().getPlayer(entityUuid);

        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            SerializedSkin oldSkin = player.getSkin();


        }
    }
}
