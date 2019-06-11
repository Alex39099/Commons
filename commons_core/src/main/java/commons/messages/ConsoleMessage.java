package commons.messages;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

class ConsoleMessage {

    ConsoleMessage(@NotNull final JavaPlugin plugin, final String msg) {
        this(Objects.requireNonNull(plugin.getName(), "plugin must not be null"), msg);
    }

    ConsoleMessage(final String prefix, final String msg) {
        Bukkit.getConsoleSender().sendMessage("[" + prefix + "] " + msg);
    }
}
