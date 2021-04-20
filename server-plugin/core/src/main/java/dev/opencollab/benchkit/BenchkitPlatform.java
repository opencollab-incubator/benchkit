package dev.opencollab.benchkit;

import dev.opencollab.benchkit.socket.BlockbenchSocket;
import dev.opencollab.benchkit.socket.SocketMessages;

import java.io.IOException;

public class BenchkitPlatform {
    public static BenchkitPlatform instance; // im just being lazy here, todo
    private final BenchkitPlugin plugin;

    public BlockbenchSocket socketServer;

    public BenchkitPlatform(BenchkitPlugin plugin) {
        instance = this;
        this.plugin = plugin;

        // Register message handlers
        new SocketMessages(plugin);
    }

    public void enable() {
        socketServer = new BlockbenchSocket(plugin, plugin.getSocketAddress());
        socketServer.start();
    }

    public void disable() {
        try {
            socketServer.stop();
        } catch (InterruptedException | IOException e) {
            plugin.logWarning("Failed to stop socket server: " + e.getMessage());
        }
    }
}