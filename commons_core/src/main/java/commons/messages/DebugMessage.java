package commons.messages;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DebugMessage {

    private String getDebugPrefix(@NotNull Debugable debugable) {
        return "[" + Objects.requireNonNull(debugable.getName(), "debugable must not be null") + "] " + ChatColor.AQUA + "[DEBUG] " + ChatColor.LIGHT_PURPLE;
    }

    public DebugMessage(@NotNull final Debugable debugable, final String msg) {
        if (debugable.getDebug()) {
            Bukkit.getConsoleSender().sendMessage(getDebugPrefix(debugable) + msg);
        }
    }

    public DebugMessage(@NotNull final JavaPlugin plugin, final String msg) {
        if (plugin instanceof Debugable) {
            new DebugMessage((Debugable) plugin, msg);
        }
    }

    public DebugMessage(final Class javaclass, @NotNull final Debugable debugable, final String msg) {
        new DebugMessage(debugable, ChatColor.YELLOW + javaclass.getSimpleName() + ": " + ChatColor.LIGHT_PURPLE + msg);
    }

    public DebugMessage(final Class javaclass, @NotNull final JavaPlugin plugin, final String msg) {
        if (plugin instanceof Debugable) {
            new DebugMessage(javaclass, (Debugable) plugin, msg);
        }
    }
}