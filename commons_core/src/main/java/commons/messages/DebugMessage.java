package commons.messages;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DebugMessage {

    private String getDebugPrefix(@NotNull Debugable debugable) {
        return "[" + Objects.requireNonNull(debugable.getName(), "debugable must not be null") + "] " + ChatColor.AQUA + "[DEBUG] " + ChatColor.LIGHT_PURPLE;
    }

    /**
     * Sends a debug-msg to the console like [Debugable-Name] [DEBUG] Msg
     * @param debugable the debugable
     * @param msg the msg to send
     */
    public DebugMessage(@NotNull final Debugable debugable, final String msg) {
        if (debugable.getDebug()) {
            Bukkit.getConsoleSender().sendMessage(getDebugPrefix(debugable) + msg);
        }
    }

    /**
     * Sends a debug-msg to the console if plugin is instance of debugable like [Plugin-Name] [DEBUG] Msg
     * @param plugin the JavaPlugin (must be instanceof Debugable)
     * @param msg the msg to send
     */
    public DebugMessage(@NotNull final JavaPlugin plugin, final String msg) {
        if (plugin instanceof Debugable) {
            new DebugMessage((Debugable) plugin, msg);
        }
    }

    /**
     *  Sends a debug-msg to the console like [Debugable-Name] [DEBUG] Class-Name: Msg
     * @param javaclass the java class (preferably which calls the constructor)
     * @param debugable the debugable
     * @param msg the msg to send
     */
    public DebugMessage(final Class javaclass, @NotNull final Debugable debugable, final String msg) {
        new DebugMessage(debugable, ChatColor.YELLOW + javaclass.getSimpleName() + ": " + ChatColor.LIGHT_PURPLE + msg);
    }


    /**
     * Sends a debug-msg to the console if plugin is instance of debugable like [Plugin-Name] [DEBUG] Msg
     * @param javaclass the java class (preferably which calls the constructor)
     * @param plugin the JavaPlugin (must be instanceof Debugable)
     * @param msg the msg to send
     */
    public DebugMessage(final Class javaclass, @NotNull final JavaPlugin plugin, final String msg) {
        if (plugin instanceof Debugable) {
            new DebugMessage(javaclass, (Debugable) plugin, msg);
        }
    }
}