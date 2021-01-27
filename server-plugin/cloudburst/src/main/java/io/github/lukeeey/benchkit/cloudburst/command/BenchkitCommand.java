package io.github.lukeeey.benchkit.cloudburst.command;

import org.cloudburstmc.server.command.data.CommandData;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.command.Command;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.command.data.CommandParameter;
import org.cloudburstmc.server.entity.Entity;
import org.cloudburstmc.server.utils.TextFormat;
import io.github.lukeeey.benchkit.cloudburst.BenchkitPlugin;

public class BenchkitCommand extends Command {
    private final BenchkitPlugin plugin;

    public BenchkitCommand(BenchkitPlugin plugin) {
        super(CommandData.builder("benchkit")
                .addParameters(new CommandParameter[] {
                        new CommandParameter("version"),
                }).build());

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("must be player");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("help todo");
            return true;
        }
        if (args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(TextFormat.GRAY + "benchkit by lukeeey");
            return true;
        }
        return true;
    }
}
