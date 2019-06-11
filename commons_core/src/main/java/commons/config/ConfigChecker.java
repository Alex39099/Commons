package commons.config;

import commons.messages.ConsoleErrorMessage;
import commons.messages.ConsoleWarningMessage;
import commons.messages.DebugMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ConfigChecker {

    private final String noPathMsg = "path is not existent.";
    private final String booleanMsg = "value must be a boolean";
    private final String intMsg = "value must be an integer";
    private final String forceDoubleMsg = "value must be a double";
    private final String doubleMsg = forceDoubleMsg + " or integer";
    private final String forceLongMsg = "value must be a long";
    private final String longMsg = forceLongMsg + " alue must be a long (or integer)";

    private JavaPlugin plugin;

    public ConfigChecker(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
    }

    public ConfigChecker(JavaPlugin plugin) {
        this(plugin, plugin.getConfig());
    }

    private void attemptConsoleMsg(ConsoleErrorType errorType, String sectionName, String path, Object value, String msg) {
        if (errorType.equals(ConsoleErrorType.WARN)) {
            new ConsoleWarningMessage(plugin, sectionName, path, msg + " (used default value " + Objects.toString(value, "") + " instead)");
        } else if (errorType.equals(ConsoleErrorType.ERROR)) {
            new ConsoleErrorMessage(plugin, sectionName, path, msg);
        } else {
            new DebugMessage(this.getClass(), plugin, "Something is not quite right with section = " + sectionName + " -> " + path + "! Msg = " + msg);
        }
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly) or value
     */
    public boolean checkBoolean(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final boolean value) {
        if (section.contains(path)) {
            if (!section.isBoolean(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, value, booleanMsg);
                return value;
            }
            return section.getBoolean(path);
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, value, noPathMsg);
        return value;
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkBoolean(final ConfigurationSection section, final String path, final ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isBoolean(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, null, booleanMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, null, noPathMsg);
        return false;
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly) or value
     */
    public int checkInt(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value) {
        if (section.contains(path)) {
            if (!section.isInt(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, value, intMsg);
                return value;
            }
            return section.getInt(path);
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, value, noPathMsg);
        return value;
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkInt(final ConfigurationSection section, final String path, final ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isInt(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, null, intMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, null, noPathMsg);
        return false;
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly (int or double)) or value
     */
    public double checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final double value) {
        return this.checkDouble(section, path, errorType, value, false);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly (int or double)) or value
     */
    public double checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value) {
        return this.checkDouble(section, path, errorType, (double) value);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param forceDouble should config value be strictly double?
     * @return config value (if set correctly) or value
     */
    public double checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value, final boolean forceDouble) {
        return this.checkDouble(section, path, errorType, (double) value, forceDouble);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param forceDouble should config value be strictly double?
     * @return config value (if set correctly) or value
     */
    public double checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final double value, final boolean forceDouble) {
        String doubleMsg;
        if (forceDouble)
            doubleMsg = this.forceDoubleMsg;
        else
            doubleMsg = this.doubleMsg;

        if (section.contains(path)) {
            if (!(section.isDouble(path) || (section.isInt(path) && !forceDouble))) {
                this.attemptConsoleMsg(errorType, section.getName(),path, value, doubleMsg);
                return value;
            }
            return section.getDouble(path);
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, value, noPathMsg);
        return value;
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly (int or long)) or value
     */
    public long checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final long value) {
        return this.checkLong(section, path, errorType, value, false);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly (int or long)) or value
     */
    public long checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value) {
        return this.checkLong(section, path, errorType, (long) value);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param forceLong should config value be strictly long?
     * @return config value (if set correctly) or value
     */
    public long checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value, final boolean forceLong) {
        return checkLong(section, path, errorType, (long) value, forceLong);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param forceLong should config value be strictly long?
     * @return config value (if set correctly) or value
     */
    public long checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final long value, final boolean forceLong) {
        String longMsg;
        if (forceLong)
            longMsg = this.forceLongMsg;
        else
            longMsg = this.longMsg;

        if (section.contains(path)) {
            if (!(section.isLong(path) || (section.isInt(path) && !forceLong))) {
                this.attemptConsoleMsg(errorType, section.getName(), path, value, longMsg);
                return value;
            }
            return section.getLong(path);
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, value, noPathMsg);
        return value;
    }
}