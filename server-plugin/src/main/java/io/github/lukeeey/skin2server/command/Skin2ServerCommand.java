package io.github.lukeeey.skin2server.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import io.github.lukeeey.skin2server.Skin2ServerPlugin;

public class Skin2ServerCommand extends Command {
    private final Skin2ServerPlugin plugin;

    public Skin2ServerCommand(Skin2ServerPlugin plugin) {
        super("skin2server", "Skin2Server Commands");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(TextFormat.GRAY + "skin2server by lukeeey");
            return true;
        }
        if (args[0].equalsIgnoreCase("createentity")) {

        }
        return true;
    }
}
