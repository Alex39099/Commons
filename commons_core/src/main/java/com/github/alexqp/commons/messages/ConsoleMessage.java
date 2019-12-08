package com.github.alexqp.commons.messages;

import com.github.alexqp.commons.config.ConsoleErrorType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ConsoleMessage {

    /**
     * @param plugin the plugin
     * @param msg the actual message
     * @deprecated Use the static methods instead.
     */
    ConsoleMessage(@NotNull final JavaPlugin plugin, final String msg) {
        this(Objects.requireNonNull(plugin.getName(), "plugin must not be null"), msg);
    }

    /**
     *
     * @param prefix the prefix (i. e. pluginName)
     * @param msg the actual message
     * @deprecated Use the static methods instead.
     */
    ConsoleMessage(final String prefix, final String msg) {
        Bukkit.getConsoleSender().sendMessage("[" + prefix + "] " + msg);
    }

    /**
     * Sends a console message by name.
     * <p>Messages look like: "[PluginName] {errorType#ChatColor} Msg</p>
     * @param errorType the error type (controls color)
     * @param pluginName the plugin's name
     * @param msg the actual message
     */
    public static void send(@NotNull final ConsoleErrorType errorType, @NotNull final String pluginName, @Nullable final String msg) {
        Objects.requireNonNull(pluginName, "pluginName must not be null");
        if (msg != null) {
            String prefix = "[" + pluginName + "] ";

            if (errorType.equals(ConsoleErrorType.ERROR)) {
                Bukkit.getLogger().severe(prefix + msg);
            } else if (errorType.equals(ConsoleErrorType.WARN)) {
                Bukkit.getLogger().warning(prefix + msg);
            } else if (!errorType.equals(ConsoleErrorType.NONE)) {
                Bukkit.getLogger().info(prefix + msg);
            }
        }
    }

    /**
     * Sends a console message for a plugin.
     * @see ConsoleMessage#send(ConsoleErrorType, String, String)
     */
    public static void send(@NotNull final ConsoleErrorType errorType, @NotNull final JavaPlugin plugin, @Nullable final String msg) {
        send(errorType, plugin.getName(), msg);
    }

    /**
     * Sends a console warning or error message by name.
     * <p>Messages look like: "[PluginName] {errorType#ChatColor} Error/Warning for section. Please check path.</p>
     * @param errorType the error type (controls color)
     * @param pluginName the plugin's name
     * @param section the configSection's name or full path
     * @param path the name of the specific errorPath
     * @throws IllegalArgumentException if ConsoleErrorType is (currently) not defined, i. e. not ERROR, WARN or NONE.
     */
    public static void send(@NotNull final ConsoleErrorType errorType, @NotNull final String pluginName, @Nullable final String section, @Nullable final String path)
                throws IllegalArgumentException {
        if (errorType.equals(ConsoleErrorType.ERROR)) {
            send(errorType, pluginName, errorType.getColor() + "Error for " + highlight(errorType, section) + ". Please check " + highlight(errorType, path));
            return;
        } else if (errorType.equals(ConsoleErrorType.WARN)) {
            send(errorType, pluginName, errorType.getColor() + "Warning for " + highlight(errorType, section) + ". Please check " + highlight(errorType, path));
            return;
        } else if (errorType.equals(ConsoleErrorType.NONE)) {
            return;
        }
        throw new IllegalArgumentException("errorType must be either ERROR or WARN");
    }

    /**
     * Sends a console warning or error message for a plugin.
     * @see ConsoleMessage#send(ConsoleErrorType, String, String, String) 
     */
    public static void send(@NotNull final ConsoleErrorType errorType, @NotNull final JavaPlugin plugin, @Nullable final String section, @Nullable final String path)
                throws IllegalArgumentException {
        send(errorType, plugin.getName(), section, path);
    }

    /**
     * Sends a console warning or error message by name followed by an extra message.
     * <p>The extra message will be sent in an extra line like "[PluginName] {errorType#ChatColor} Specific Error/Warning: Msg</p>
     * @param errorType the error type (controls color)
     * @param pluginName the plugin's name
     * @param section the configSection's name or full path
     * @param path the name of the specific errorPath
     * @param msg the specific error/warning message
     * @throws IllegalArgumentException if ConsoleErrorType is (currently) not defined, i. e. not ERROR, WARN or NONE.
     * @see ConsoleMessage#send(ConsoleErrorType, String, String, String)
     */
    public static void send(@NotNull final ConsoleErrorType errorType, @NotNull final String pluginName, @Nullable final String section, @Nullable final String path, @Nullable String msg)
                throws IllegalArgumentException {
        send(errorType, pluginName, section, path); // already throws IllegalArgumentException
        if (msg != null) {
            if (errorType.equals(ConsoleErrorType.ERROR)) {
                send(errorType, pluginName, errorType.getColor() + "Specific Error: " + msg);
            } else if (errorType.equals(ConsoleErrorType.WARN)) {
                send(errorType, pluginName, errorType.getColor() + "Specific Warning: " + msg);
            }
        }
    }

    /**
     * Sends a console warning or error message for a plugin followed by an extra message.
     * @see ConsoleMessage#send(ConsoleErrorType, String, String, String, String) 
     */
    public static void send(@NotNull final ConsoleErrorType errorType, @NotNull final JavaPlugin plugin, @Nullable final String section, @Nullable final String path, @Nullable String msg)
            throws IllegalArgumentException {
        send(errorType, plugin.getName(), section, path, msg);
    }

    private static String highlight(@NotNull final ConsoleErrorType errorType, String string) {
        return ChatColor.DARK_RED + string + errorType.getColor();
    }

    // -----------------------------------------------------------------------
    // DEBUGGING
    // -----------------------------------------------------------------------

    private static ChatColor debugColor = ChatColor.LIGHT_PURPLE;

    /**
     * Sends a debug message.
     * <p>No messages are sent if debug mode is disabled for the given debugable. Messages look like: [DebugableName] [DEBUG] Msg</p>
     * @param debugable the debugable
     * @param msg the actual message
     */
    public static void debug(@NotNull Debugable debugable, String msg) {
        if (debugable.getDebug()) {
            Bukkit.getLogger().info(getDebugPrefix(debugable) + msg);
        }
    }

    /**
     * Sends a debug message.
     * <p>The JavaPlugin must be instance of Debugable</p>
     * @see ConsoleMessage#debug(Debugable, String)
     */
    public static void debug(@NotNull JavaPlugin plugin, String msg) {
        if (plugin instanceof Debugable) {
            debug((Debugable) plugin, msg);
        }
    }

    /**
     * Sends a debug message with a class reference.
     * @param clazz the class
     * @param debugable the debugable
     * @param msg the actual message
     */
    public static void debug(@NotNull Class clazz, @NotNull Debugable debugable, String msg) {
        debug(debugable, ChatColor.YELLOW + clazz.getSimpleName() + ": " + debugColor + msg);
    }

    /**
     * Sends a debug message with a class reference.
     * <p>The JavaPlugin must be instance of Debugable</p>
     * @see ConsoleMessage#debug(Class, Debugable, String)
     */
    public static void debug(@NotNull Class clazz, @NotNull JavaPlugin plugin, String msg) {
        if (plugin instanceof Debugable) {
            debug(clazz, (Debugable) plugin, msg);
        }
    }

    private static String getDebugPrefix(@NotNull Debugable debugable) {
        return "[" + Objects.requireNonNull(debugable.getName(), "Debugable must not be null") + "] " + ChatColor.AQUA + "[DEBUG] " + debugColor;
    }

    // -----------------------------------------------------------------------
    // CONVENIENT METHODS
    // -----------------------------------------------------------------------

    /**
     * Creates a so called PlayerString.
     * @param p the player
     * @return a string like "PlayerName (UUID)"
     */
    public static String getPlayerString(@NotNull Player p) {
        return p.getName() + " (" + p.getUniqueId() + ")";
    }
}