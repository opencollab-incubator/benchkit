package io.github.lukeeey.skin2server.event;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.Data;
import lombok.Getter;

@Data
public class SkinApplyEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Skin skin;
}
