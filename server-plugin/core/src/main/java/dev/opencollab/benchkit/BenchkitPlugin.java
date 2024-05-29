package dev.opencollab.benchkit;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public interface BenchkitPlugin {

    /**
     * Returns whether or not a player is online with the given UUID.
     *
     * @param uuid the players uuid
     * @return true if the player is online, otherwise false
     */
    boolean playerExists(UUID uuid);

    /**
     * Returns a map containing uuids and usernames of online players.
     *
     * @return map of uuids and names
     */
    Map<UUID, String> getOnlinePlayers();

    /**
     * Apply a skin to the given player.
     *
     * @param playerUuid the players uuid
     * @param image the skin texture
     */
    void applySkin(UUID playerUuisd, BufferedImage image);

    /**
     * Apply a skin to the given player as well as the given model.
     *
     * @param playerUuid the players uuid
     * @param identifier the unique skin id
     * @param geometryData the skins geometry
     * @param image the skin texture
     */
    void applySkinWithModel(UUID playerUuid, String identifier, String geometryData, BufferedImage image);

    /**
     * Schedule a task to be run at a later date.
     *
     * @param task a callback that is run when the task is run
     * @param delay the amount of time to wait (in ticks) to run the task
     */
    void scheduleDelayedTask(Runnable task, int delay);

    /**
     * Returns the amount of time (in seconds) to authenticate before the
     * connection is closed.
     *
     * @return the authentication timeout, in seconds
     */
    int getAuthenticationTimeout();

    /**
     * Returns the key used to authenticate.
     *
     * @return the authentication key
     */
    String getAuthenticationKey();

    /**
     * Returns the plugins data folder.
     *
     * @return the data folder
     */
    Path getDataPath();

    /**
     * Returns the address and port that the socket server should listen
     * for connections on.
     *
     * @return the socket address
     */
    InetSocketAddress getSocketAddress();

    /**
     * Log an informational message to the console.
     *
     * @param message the message
     */
    void logInfo(String message);

    /**
     * Log a warning message to the console.
     *
     * @param message the message
     */
    void logWarning(String message);

    /**
     * Log a critical error message to the console.
     *
     * @param message the message
     */
    void logError(String message);
}
