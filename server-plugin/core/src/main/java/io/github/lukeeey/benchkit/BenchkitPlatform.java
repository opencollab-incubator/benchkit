package io.github.lukeeey.benchkit;

import io.github.lukeeey.benchkit.socket.BlockbenchSocket;
import io.github.lukeeey.benchkit.socket.SocketMessages;

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