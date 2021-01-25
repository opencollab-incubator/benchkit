package io.github.lukeeey.skin2server.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import io.github.lukeeey.skin2server.Skin2ServerPlugin;

import java.io.IOException;

public class Skin2ServerCommand extends Command {
    private final Skin2ServerPlugin plugin;

    public Skin2ServerCommand(Skin2ServerPlugin plugin) {
        super("skin2server", "Skin2Server Commands");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return true;
    }
}
