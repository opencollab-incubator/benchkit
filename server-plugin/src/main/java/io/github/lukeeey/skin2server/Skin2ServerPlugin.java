package io.github.lukeeey.skin2server;

import cn.nukkit.plugin.PluginBase;
import io.github.lukeeey.skin2server.command.Skin2ServerCommand;
import io.github.lukeeey.skin2server.socket.BlockbenchSocket;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public class Skin2ServerPlugin extends PluginBase {
    private String key;
    private InetSocketAddress address;

    private BlockbenchSocket socketServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        key = getConfig().getString("key");
        address = new InetSocketAddress(getConfig().getString("address"), getConfig().getInt("port"));

        socketServer = new BlockbenchSocket(this, address);
        socketServer.start();

        getServer().getCommandMap().register("skin2server", new Skin2ServerCommand(this));
    }
}
