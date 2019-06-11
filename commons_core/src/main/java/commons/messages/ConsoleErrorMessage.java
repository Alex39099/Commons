package commons.messages;

import com.sun.istack.internal.NotNull;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ConsoleErrorMessage extends ConsoleMessage {

    private static final String colorPrefix = "" + ChatColor.BOLD + ChatColor.RED;

    /**
     *
     * @param plugin the JavaPlugin
     * @param msg the error msg (without color prefix)
     */
    public ConsoleErrorMessage(@NotNull final JavaPlugin plugin, final String msg) {
        super(plugin, colorPrefix + msg);
    }

    /**
     *
     * @param prefix the prefix (will be set in [])
     * @param msg the error msg (without color prefix)
     */
    public ConsoleErrorMessage(final String prefix, final String msg) {
        super(prefix, colorPrefix + msg);
    }

    /**
     *
     * @param plugin the JavaPlugin
     * @param section the sectionName (Error for 'section')
     * @param errorPath the errorPath (Please check 'errorPath')
     */
    public ConsoleErrorMessage(@NotNull final JavaPlugin plugin, final String section, final String errorPath) {
        this(plugin, "Error for " + highlight(section)
                + ". Please check " + highlight(errorPath));
    }

    /**
     *
     * @param prefix the prefix (will be set in [])
     * @param section the sectionName (Error for 'section')
     * @param errorPath the errorPath (Please check 'errorPath')
     */
    public ConsoleErrorMessage(final String prefix, final String section, final String errorPath) {
        this(prefix, "Error for " + highlight(section)
                + ". Please check " + highlight(errorPath));
    }

    /**
     *
     * @param plugin the JavaPlugin
     * @param section the sectionName (Error for 'section')
     * @param errorPath the errorPath (Please check 'errorPath')
     * @param errorMsg the errorMsg (Specific Error: 'errorMsg')
     */
    public ConsoleErrorMessage(@NotNull final JavaPlugin plugin, final String section, final String errorPath, final String errorMsg) {
        this(plugin, "Error for " + highlight(section)
                + ". Please check " + highlight(errorPath));
        new ConsoleErrorMessage(plugin, "Specific Error: " + errorMsg);
    }

    /**
     *
     * @param prefix the prefix (will be set in [])
     * @param section the sectionName (Error for 'section')
     * @param errorPath the errorPath (Please check 'errorPath')
     * @param errorMsg the errorMsg (Specific Error: 'errorMsg')
     */
    public ConsoleErrorMessage(final String prefix, final String section, final String errorPath, final String errorMsg) {
        this(prefix, "Error for " + highlight(section)
                + ". Please check " + highlight(errorPath));
        new ConsoleErrorMessage(prefix, "Specific Error: " + errorMsg);
    }



    private static String highlight(String msg) {
        return ChatColor.DARK_RED + msg + ChatColor.RED;
    }
}
