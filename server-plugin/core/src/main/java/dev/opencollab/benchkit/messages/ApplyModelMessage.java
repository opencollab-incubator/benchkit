package dev.opencollab.benchkit.messages;

import com.google.gson.JsonObject;
import dev.opencollab.benchkit.BenchkitPlugin;
import lombok.RequiredArgsConstructor;
import org.java_websocket.WebSocket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class ApplyModelMessage implements AbstractMessage {
    private final BenchkitPlugin plugin;

    @Override
    public void parse(WebSocket socket, JsonObject data) {
        UUID entityUuid = UUID.fromString(data.get("entityUuid").getAsString());
        String identifier = data.get("identifier").getAsString();
        String modelData = data.get("model").getAsString();

        try {
            BufferedImage image = ImageIO.read(new File(plugin.getDataPath().toFile(), "skin.png"));
            plugin.applySkinWithModel(entityUuid, identifier, modelData, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}