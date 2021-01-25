package io.github.lukeeey.skin2server.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.Plugin;
import io.github.lukeeey.skin2server.Skin2ServerPlugin;
import io.github.lukeeey.skin2server.http.Session;
import retrofit2.Response;

import java.io.IOException;

public class Skin2ServerCommand extends Command {
    private final Skin2ServerPlugin plugin;

    public Skin2ServerCommand(Skin2ServerPlugin plugin) {
        super("skin2server", "Skin2Server Commands");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args[0].equalsIgnoreCase("connect")) {
            String sessionId = args[1];
            try {
                Response<Session> response = plugin.getApiService().joinSession(sessionId).execute();
                if (!response.isSuccessful() && response.errorBody() != null) {
                    sender.sendMessage("Error: " + response.errorBody().string());
                    return true;
                }
                Session session = response.body();
                if (session == null) {
                    sender.sendMessage("Session is null");
                    return true;
                }
                sender.sendMessage("Joined session " + session.getSessionId() + "!!!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
