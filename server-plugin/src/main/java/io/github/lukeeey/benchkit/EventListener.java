package io.github.lukeeey.benchkit;

import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.item.Item;

public class EventListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getItem().getId() == Item.STICK && event.getEntity() instanceof EntityHuman) {
            EntityHuman human = (EntityHuman) event.getEntity();
            Skin skin = human.getSkin();
        }
    }
}
