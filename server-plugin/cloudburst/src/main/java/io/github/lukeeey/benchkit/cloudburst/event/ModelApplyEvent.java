package io.github.lukeeey.benchkit.cloudburst.event;

import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.server.event.Cancellable;
import org.cloudburstmc.server.event.Event;
import lombok.Data;
import org.cloudburstmc.server.player.Player;

@Data
public class ModelApplyEvent extends Event implements Cancellable {
    private final Player player;
    private final SerializedSkin skin;
}
