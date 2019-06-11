package commons.messages;

import org.jetbrains.annotations.NotNull;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ConsoleWarningMessage extends ConsoleMessage {

    private static final String colorPrefix = "" + ChatColor.BOLD + ChatColor.GOLD;


    /**
     *
     * @param plugin the JavaPlugin
     * @param msg the warning msg (without color prefix)
     */
    public ConsoleWarningMessage(@NotNull final JavaPlugin plugin, final String msg) {
        super(plugin, colorPrefix + msg);
    }

    /**
     *
     * @param prefix the prefix (will be set in [])
     * @param msg the warning msg (without color prefix)
     */
    public ConsoleWarningMessage(final String prefix, final String msg) {
        super(prefix, colorPrefix + msg);
    }

    /**
     *
     * @param plugin the JavaPlugin
     * @param section the sectionName (Warning for 'section')
     * @param warnPath the warnPath (Please check 'warnPath')
     */
    public ConsoleWarningMessage(@NotNull final JavaPlugin plugin, final String section, final String warnPath) {
        this(plugin, "Warning for " + highlight(section)
            + ". Please check " + highlight(warnPath));
    }

    /**
     *
     * @param prefix the prefix (will be set in [])
     * @param section the sectionName (Warning for 'section')
     * @param warnPath the warnPath (Please check 'warnPath')
     */
    public ConsoleWarningMessage(final String prefix, final String section, final String warnPath) {
        this(prefix, "Warning for " + highlight(section)
                + ". Please check " + highlight(warnPath));
    }

    /**
     *
     * @param plugin the JavaPlugin
     * @param section the sectionName (Warning for 'section')
     * @param warnPath the warnPath (Please check 'warnPath')
     * @param warnMsg the warnMsg (Specific Warning: 'warnMsg')
     */
    public ConsoleWarningMessage(@NotNull final JavaPlugin plugin, final String section, final String warnPath, final String warnMsg) {
        this(plugin, section, warnPath);
        new ConsoleWarningMessage(plugin, "Specific Warning: " + warnMsg);
    }

    /**
     *
     * @param prefix the prefix (will be set in [])
     * @param section the sectionName (Warning for 'section')
     * @param warnPath the warnPath (Please check 'warnPath')
     * @param warnMsg the warnMsg (Specific Warning: 'warnMsg')
     */
    public ConsoleWarningMessage(final String prefix, final String section, final String warnPath, final String warnMsg) {
        this(prefix, section, warnPath);
        new ConsoleWarningMessage(prefix, "Specific Warning: " + warnMsg);
    }


    private static String highlight(String msg) {
        return ChatColor.DARK_RED + msg + ChatColor.GOLD;
    }

}
