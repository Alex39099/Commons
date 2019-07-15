package commons.config;

import com.google.common.collect.Range;
import commons.messages.ConsoleErrorMessage;
import commons.messages.ConsoleWarningMessage;
import commons.messages.DebugMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public class ConfigChecker {

    public final String noPathMsg = "path is not existent.";
    public final String booleanMsg = "value must be a boolean";
    public final String intMsg = "value must be an integer";
    public final String forceDoubleMsg = "value must be a double";
    public final String doubleMsg = forceDoubleMsg + " or integer";
    public final String forceLongMsg = "value must be a long";
    public final String longMsg = forceLongMsg + " or integer";
    public final String stringMsg = "value must be a string";
    public final String configSectionMsg = "value must be a configurationSection";
    public final String vectorMsg = "value must be a vector";

    private JavaPlugin plugin;
    private String configFileName;

    public ConfigChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ConfigChecker(JavaPlugin plugin, FileConfiguration configFile) {
        this(plugin);
        this.configFileName = configFile.getName();
    }

    /**
     * Attempts to send a console msg.
     * @param errorType the errorType (controls console msg)
     * @param sectionName the sectionName
     * @param path the path
     * @param value the defValue (can be null)
     * @param msg the specific error msg
     */
    public void attemptConsoleMsg(ConsoleErrorType errorType, String sectionName, String path, Object value, String msg) {
        if (errorType.equals(ConsoleErrorType.WARN)) {
            new ConsoleWarningMessage(plugin, this.getSaveSectionName(sectionName), path, msg + " (used default value " + Objects.toString(value, "") + " instead)");
        } else if (errorType.equals(ConsoleErrorType.ERROR)) {
            new ConsoleErrorMessage(plugin, this.getSaveSectionName(sectionName), path, msg);
        } else {
            new DebugMessage(this.getClass(), plugin, "Something is not quite right with section = " + this.getSaveSectionName(sectionName) + " -> " + path + "! Msg = " + msg);
        }
    }

    private String getRangeMsg(Range<?> range) {
        return "value must be element of " + range.toString();
    }

    private String getSaveSectionName(String sectionName) {
        if (sectionName.isEmpty()) {
            if (configFileName != null && !configFileName.isEmpty())
                return configFileName;
            return plugin.getName();
        }
        return sectionName;
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
     * @param range range config value or value must be element of
     * @return config value if set correctly and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public int checkInt(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value, final Range<Integer> range)
        throws IllegalArgumentException {
        if (!range.contains(value))
            throw new IllegalArgumentException(this.getRangeMsg(range));

        int testValue = this.checkInt(section, path, errorType, value);

        if (range.contains(testValue)) {
            return testValue;
        } else {
            this.attemptConsoleMsg(errorType, section.getName(), path, value, this.getRangeMsg(range));
            return value;
        }
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param range range config value or value must be element of
     * @return true if set correctly and in range, false otherwise
     */
    public boolean checkInt(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final Range<Integer> range) {
        boolean configStatus = this.checkInt(section, path, errorType);
        if (configStatus && !range.contains(section.getInt(path))) {
            this.attemptConsoleMsg(errorType, section.getName(), path, null, this.getRangeMsg(range));
            return false;
        }
        return configStatus;
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
     * @param forceDouble should config value be strictly double?
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final boolean forceDouble) {
        String doubleMsg;
        if (forceDouble)
            doubleMsg = this.forceDoubleMsg;
        else
            doubleMsg = this.doubleMsg;

        if (section.contains(path)) {
            if (!(section.isDouble(path) || (section.isInt(path) && !forceDouble))) {
                this.attemptConsoleMsg(errorType, section.getName(),path, null, doubleMsg);
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
     * @return true if config value is set correctly (int or double), false otherwise
     */
    public boolean checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType) {
        return this.checkDouble(section, path, errorType, false);
    }


    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @param forceDouble should config value be strictly double?
     * @return config value if set correctly and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public double checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final double value, final Range<Double> range, final boolean forceDouble)
        throws IllegalArgumentException {
        if (!range.contains(value))
            throw new IllegalArgumentException(this.getRangeMsg(range));

        double testValue = this.checkDouble(section, path, errorType, value, forceDouble);
        if (range.contains(testValue)) {
            return testValue;
        } else {
            this.attemptConsoleMsg(errorType, section.getName(), path, value, this.getRangeMsg(range));
            return value;
        }
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @return config value if set correctly (int or double) and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public double checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final double value, final Range<Double> range)
            throws IllegalArgumentException {
        return this.checkDouble(section, path, errorType, value, range, false);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @param forceDouble should config value be strictly double?
     * @return config value if set correctly and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public double checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value, final Range<Double> range, final boolean forceDouble)
        throws IllegalArgumentException {
        return this.checkDouble(section, path, errorType, (double) value, range, forceDouble);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @return config value if set correctly (int or double) and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public double checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value, final Range<Double> range)
        throws IllegalArgumentException {
        return this.checkDouble(section, path, errorType, (double) value, range, false);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param range range config value or value must be element of
     * @param forceDouble should config value be strictly double?
     * @return true if set correctly and in range, false otherwise
     */
    public boolean checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final Range<Double> range, final boolean forceDouble) {
        boolean configStatus = this.checkDouble(section, path, errorType, forceDouble);
        if (configStatus && !range.contains(section.getDouble(path))) {
            this.attemptConsoleMsg(errorType, section.getName(), path, null, this.getRangeMsg(range));
            return false;
        }
        return configStatus;
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param range range config value or value must be element of
     * @return true if set correctly (int or double) and in range, false otherwise
     */
    public boolean checkDouble(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final Range<Double> range) {
        return this.checkDouble(section, path, errorType, range, false);
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
     * @param forceLong should config value be strictly double?
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final boolean forceLong) {
        String longMsg;
        if (forceLong)
            longMsg = this.forceLongMsg;
        else
            longMsg = this.longMsg;

        if (section.contains(path)) {
            if (!(section.isLong(path) || (section.isInt(path) && !forceLong))) {
                this.attemptConsoleMsg(errorType, section.getName(), path, null, longMsg);
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
     * @return true if config value is set correctly (int or long), false otherwise
     */
    public boolean checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType) {
        return this.checkLong(section, path, errorType, false);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @param forceLong should config value be strictly long?
     * @return config value if set correctly and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public long checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final long value, final Range<Long> range, final boolean forceLong)
        throws IllegalArgumentException {
        if (!range.contains(value))
            throw new IllegalArgumentException(this.getRangeMsg(range));

        long testValue = this.checkLong(section, path, errorType, value, forceLong);
        if (range.contains(testValue)) {
            return testValue;
        } else {
            this.attemptConsoleMsg(errorType, section.getName(), path, value, this.getRangeMsg(range));
            return value;
        }
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @return config value if set correctly (int or long) and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public long checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final long value, final Range<Long> range)
        throws IllegalArgumentException {
        return this.checkLong(section, path, errorType, value, range, false);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @param forceLong should config value be strictly long?
     * @return config value if set correctly and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public long checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value, final Range<Long> range, final boolean forceLong)
        throws IllegalArgumentException {
        return this.checkLong(section, path, errorType, (long) value, forceLong);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @return config value if set correctly (int or long) and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public long checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final int value, final Range<Long> range)
        throws IllegalArgumentException {
        return this.checkLong(section, path, errorType, (long) value, range, false);
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param range range config value or value must be element of
     * @param forceLong should config value be strictly long?
     * @return true if set correctly and in range, false otherwise
     */
    public boolean checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final Range<Long> range, final boolean forceLong) {
        boolean configStatus = this.checkDouble(section, path, errorType, forceLong);
        if (configStatus && !range.contains(section.getLong(path))) {
            this.attemptConsoleMsg(errorType, section.getName(), path, null, this.getRangeMsg(range));
            return false;
        }
        return configStatus;
    }

    /**
     *
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param range range config value or value must be element of
     * @return true if set correctly (int or long) and in range, false otherwise
     */
    public boolean checkLong(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final Range<Long> range) {
        return this.checkLong(section, path, errorType, range, false);
    }


    /**
     * Checks if the specified path is a string.
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly) or value
     */
    public String checkString(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, String value) {
        if (section.contains(path)) {
            if (!section.isString(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, value, stringMsg);
                return value;
            }
            return section.getString(path);
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, value, noPathMsg);
        return value;
    }

    /**
     * Checks if the specified path is a string.
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkString(final ConfigurationSection section, final String path, final ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isString(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, null, stringMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, null, noPathMsg);
        return false;
    }

    /**
     * Gets the requested section by path.
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return ConfigurationSection (if set correctly) or null
     */
    @Nullable public ConfigurationSection checkConfigSection(final ConfigurationSection section, final String path, final ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isConfigurationSection(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, null, configSectionMsg);
                return null;
            }
            return section.getConfigurationSection((path));
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, null, noPathMsg);
        return null;
    }

    /**
     * Checks a ConfigurationSerializableCheckable. Note: This will suppress stackTraces of catch exceptions.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param checkableClass the ConfigurationSerializableCheckable
     * @return false if section does not contain path, value of checkableClassInstance.checkConfigSection otherwise
     */
    public boolean checkConfigurationSerializable(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final Class<? extends ConfigurationSerializableCheckable> checkableClass) {
        return this.checkConfigurationSerializable(section, path, errorType, checkableClass, false);
    }

    /**
     * Checks a ConfigurationSerializableCheckable.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param checkableClass the ConfigurationSerializableCheckable
     * @param printStackTrace should a stack trace be printed? (in case of exception, may use this for development)
     * @return false if section does not contain path, value of checkableClassInstance.checkConfigSection otherwise
     */
    public boolean checkConfigurationSerializable(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final Class<? extends ConfigurationSerializableCheckable> checkableClass, boolean printStackTrace) {
        try {
            if (!section.contains(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, null, noPathMsg);
                return false;
            }
            return checkableClass.newInstance().checkConfigSection(this, section, path, errorType);
        } catch (Exception e) {
            new DebugMessage(this.getClass(), plugin, e.getClass().getSimpleName() + " for checkConfigurationSerializable, checkableClass = " + checkableClass.getSimpleName());
            if (printStackTrace)
                e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the requested ConfigurationSerializableCheckable by path or value.
     * Note: In case of an error-msg this will print the simpleClassName of T instead of value.toString()
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param <T> the type of ConfigurationSerializableCheckable
     * @return config value (if set correctly) or value
     */
    public <T extends ConfigurationSerializableCheckable> T checkConfigurationSerializable(final ConfigurationSection section, final String path, final ConsoleErrorType errorType, final T value) {
        if (!section.contains(path)) {
            this.attemptConsoleMsg(errorType, section.getName(), path, " of " + value.getClass().getSimpleName(), noPathMsg);
            return value;
        }
        if (value.checkConfigSection(this, section, path, errorType)) {
            @SuppressWarnings("unchecked")
            T retValue = (T) section.get(path);
            return retValue;
        }
        return value;
    }

    public Vector checkVector(final ConfigurationSection section, final String path, ConsoleErrorType errorType, Vector value) {
        if (section.contains(path)) {
            if (!section.isVector(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, value, vectorMsg);
                return value;
            }
            return section.getVector(path);
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, value, noPathMsg);
        return value;
    }

    public boolean checkVector(final ConfigurationSection section, final String path, ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isVector(path)) {
                this.attemptConsoleMsg(errorType, section.getName(), path, null, intMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section.getName(), path, null, noPathMsg);
        return false;
    }
}