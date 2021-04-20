package dev.opencollab.benchkit.cloudburst.event;

import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import lombok.Data;
import org.cloudburstmc.server.event.Cancellable;
import org.cloudburstmc.server.event.Event;
import org.cloudburstmc.server.player.Player;

@Data
public class SkinApplyEvent extends Event implements Cancellable {
    private final Player player;
    private final SerializedSkin skin;
}
