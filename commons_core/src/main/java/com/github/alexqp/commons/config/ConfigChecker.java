/*
 * Copyright (C) 2019-2021 Alexander Schmid
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.alexqp.commons.config;

import com.github.alexqp.commons.messages.ConsoleMessage;
import com.google.common.collect.Range;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "WeakerAccess"})
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

    /**
     * Constructs a ConfigChecker.
     * <p>If the section's name is empty in any check, this will replace its name with the plugin's name.
     * @param plugin the plugin.
     * @see ConfigChecker#ConfigChecker(JavaPlugin, FileConfiguration)
     */
    public ConfigChecker(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Constructs a ConfigChecker.
     * <p>If the section's name is empty in any check, this will replace its name with the configFile's name (or if this is also empty with the plugin's name)
     * @param plugin the plugin.
     * @param configFile the configFile.
     * @see ConfigChecker#ConfigChecker(JavaPlugin)
     */
    public ConfigChecker(@NotNull JavaPlugin plugin, @NotNull FileConfiguration configFile) {
        this(plugin);
        this.configFileName = configFile.getName();
    }

    /**
     * Attempts to send a console msg.
     * <p>ConsoleErrorType must be either ERROR, WARN or NONE. This will add an "(used default value ... instead)" ending if value is not null.
     * @param errorType the errorType (controls console msg)
     * @param sectionPath the section's current path
     * @param path the path
     * @param value the defValue (can be null)
     * @param msg the specific error msg
     * @see ConsoleMessage#send(ConsoleErrorType, String, String, String, String)
     */
    public void attemptConsoleMsg(@NotNull ConsoleErrorType errorType, @Nullable String sectionPath, @Nullable String path, @Nullable Object value, @Nullable String msg) {
        if (value != null) {
            ConsoleMessage.send(errorType, this.getSaveSectionName(sectionPath), path, msg + " (used default value " + value.toString() + " instead)");
            return;
        }
        ConsoleMessage.send(errorType, this.getSaveSectionName(sectionPath), path, msg);
    }

    /**
     * Attempts to send a console msg.
     * <p>This will pass null for Object value to the see also method.
     * @see ConfigChecker#attemptConsoleMsg(ConsoleErrorType, String, String, Object, String)
     */
    public void attemptConsoleMsg(@NotNull ConsoleErrorType errorType, @Nullable String sectionPath, @Nullable String path, @Nullable String msg) {
        this.attemptConsoleMsg(errorType, sectionPath, path, null, msg);
    }

    /**
     * Attempts to send a console msg.
     * <p>The section's current path will be used in the see also method.
     * @see ConfigChecker#attemptConsoleMsg(ConsoleErrorType, String, String, Object, String)
     */
    public void attemptConsoleMsg(@NotNull ConsoleErrorType errorType, @NotNull ConfigurationSection section,  @Nullable String path,  @Nullable String msg) {
        this.attemptConsoleMsg(errorType, section.getCurrentPath(), path, null, msg);
    }

    /**
     * Attempts to send a console msg.
     * <p>The section's current path will be used in the see also method.
     * @see ConfigChecker#attemptConsoleMsg(ConsoleErrorType, String, String, Object, String)
     */
    public void attemptConsoleMsg(@NotNull ConsoleErrorType errorType, @NotNull ConfigurationSection section, @Nullable String path, @Nullable Object value, @Nullable String msg) {
        this.attemptConsoleMsg(errorType, section.getCurrentPath(), path, value, msg);
    }

    /**
     * Gets the default range message of configChecker.
     * @param range the range
     * @return value must be element of range.toString()
     */
    @NotNull
    public static String getRangeMsg(Range<?> range) {
        return "value must be element of " + range.toString();
    }

    private String getSaveSectionName(String sectionName) {
        if (sectionName == null || sectionName.isEmpty()) {
            if (configFileName != null && !configFileName.isEmpty())
                return configFileName;
            return plugin.getName();
        }
        return sectionName;
    }

    /**
     * Checks a boolean, returning a default value if not found.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly) or value
     */
    public boolean checkBoolean(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final boolean value) {
        if (section.contains(path)) {
            if (!section.isBoolean(path)) {
                this.attemptConsoleMsg(errorType, section, path, value, booleanMsg);
                return value;
            }
            return section.getBoolean(path);
        }
        this.attemptConsoleMsg(errorType, section, path, value, noPathMsg);
        return value;
    }

    /**
     * Checks a boolean.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkBoolean(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isBoolean(path)) {
                this.attemptConsoleMsg(errorType, section, path, null, booleanMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section, path, null, noPathMsg);
        return false;
    }

    /**
     * Checks an integer, returning a default value if not found.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly) or value
     */
    public int checkInt(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value) {
        if (section.contains(path)) {
            if (!section.isInt(path)) {
                this.attemptConsoleMsg(errorType, section, path, value, intMsg);
                return value;
            }
            return section.getInt(path);
        }
        this.attemptConsoleMsg(errorType, section, path, value, noPathMsg);
        return value;
    }

    /**
     * Checks an integer.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkInt(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isInt(path)) {
                this.attemptConsoleMsg(errorType, section, path, null, intMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section, path, null, noPathMsg);
        return false;
    }

    /**
     * Checks an integer, returning a default value if not found or within the given range.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @return config value if set correctly and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public int checkInt(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value, @NotNull final Range<Integer> range)
        throws IllegalArgumentException {
        if (!range.contains(value))
            throw new IllegalArgumentException(getRangeMsg(range));

        int testValue = this.checkInt(section, path, errorType, value);

        if (range.contains(testValue)) {
            return testValue;
        } else {
            this.attemptConsoleMsg(errorType, section, path, value, getRangeMsg(range));
            return value;
        }
    }

    /**
     * Checks an integer.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param range range config value or value must be element of
     * @return true if set correctly and in range, false otherwise
     */
    public boolean checkInt(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, @NotNull final Range<Integer> range) {
        boolean configStatus = this.checkInt(section, path, errorType);
        if (configStatus && !range.contains(section.getInt(path))) {
            this.attemptConsoleMsg(errorType, section, path, null, getRangeMsg(range));
            return false;
        }
        return configStatus;
    }

    /**
     * Checks a double, returning a default value if not found.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param forceDouble should config value be strictly double?
     * @return config value (if set correctly) or value
     */
    public double checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final double value, final boolean forceDouble) {
        String doubleMsg;
        if (forceDouble)
            doubleMsg = this.forceDoubleMsg;
        else
            doubleMsg = this.doubleMsg;

        if (section.contains(path)) {
            if (!(section.isDouble(path) || (section.isInt(path) && !forceDouble))) {
                this.attemptConsoleMsg(errorType, section,path, value, doubleMsg);
                return value;
            }
            return section.getDouble(path);
        }
        this.attemptConsoleMsg(errorType, section, path, value, noPathMsg);
        return value;
    }

    /**
     * Checks a double, returning a default value if not found.
     * <p>This method does not check for a strict double value (forceDouble = false).
     * @see ConfigChecker#checkDouble(ConfigurationSection, String, ConsoleErrorType, double, boolean)
     */
    public double checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final double value) {
        return this.checkDouble(section, path, errorType, value, false);
    }

    /**
     * Checks a double, returning a default value if not found.
     * @see ConfigChecker#checkDouble(ConfigurationSection, String, ConsoleErrorType, double, boolean)
     */
    public double checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value, final boolean forceDouble) {
        return this.checkDouble(section, path, errorType, (double) value, forceDouble);
    }

    /**
     * Checks a double, returning a default value if not found.
     * @see ConfigChecker#checkDouble(ConfigurationSection, String, ConsoleErrorType, double)
     */
    public double checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value) {
        return this.checkDouble(section, path, errorType, (double) value);
    }

    /**
     * Checks a double.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param forceDouble should config value be strictly double?
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final boolean forceDouble) {
        String doubleMsg;
        if (forceDouble)
            doubleMsg = this.forceDoubleMsg;
        else
            doubleMsg = this.doubleMsg;

        if (section.contains(path)) {
            if (!(section.isDouble(path) || (section.isInt(path) && !forceDouble))) {
                this.attemptConsoleMsg(errorType, section,path, null, doubleMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section, path, null, noPathMsg);
        return false;
    }

    /**
     * Checks a double.
     * <p>This method does not check for a strict double value (forceDouble = false).
     * @see ConfigChecker#checkDouble(ConfigurationSection, String, ConsoleErrorType, boolean)
     */
    public boolean checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType) {
        return this.checkDouble(section, path, errorType, false);
    }


    /**
     * Checks a double, returning a default value if not found or within the given range.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @param forceDouble should config value be strictly double?
     * @return config value if set correctly and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public double checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final double value, @NotNull final Range<Double> range, final boolean forceDouble)
        throws IllegalArgumentException {
        if (!range.contains(value))
            throw new IllegalArgumentException(getRangeMsg(range));

        double testValue = this.checkDouble(section, path, errorType, value, forceDouble);
        if (range.contains(testValue)) {
            return testValue;
        } else {
            this.attemptConsoleMsg(errorType, section, path, value, getRangeMsg(range));
            return value;
        }
    }

    /**
     * Checks a double, returning a default value if not found or within the given range.
     * <p>This method does not check for a strict double value (forceDouble = false).
     * @see ConfigChecker#checkDouble(ConfigurationSection, String, ConsoleErrorType, double, Range, boolean)
     */
    public double checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final double value, @NotNull final Range<Double> range)
            throws IllegalArgumentException {
        return this.checkDouble(section, path, errorType, value, range, false);
    }

    /**
     * Checks a double, returning a default value if not found or within the given range.
     * @see ConfigChecker#checkDouble(ConfigurationSection, String, ConsoleErrorType, double, Range, boolean)
     */
    public double checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value, @NotNull final Range<Double> range, final boolean forceDouble)
        throws IllegalArgumentException {
        return this.checkDouble(section, path, errorType, (double) value, range, forceDouble);
    }

    /**
     * Checks a double, returning a default value if not found or within the given range.
     * <p>This method does not check for a strict double value (forceDouble = false).
     * @see ConfigChecker#checkDouble(ConfigurationSection, String, ConsoleErrorType, double, Range, boolean)
     */
    public double checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value, @NotNull final Range<Double> range)
        throws IllegalArgumentException {
        return this.checkDouble(section, path, errorType, (double) value, range, false);
    }

    /**
     * Checks a double.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param range range config value or value must be element of
     * @param forceDouble should config value be strictly double?
     * @return true if set correctly and in range, false otherwise
     */
    public boolean checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, @NotNull final Range<Double> range, final boolean forceDouble) {
        boolean configStatus = this.checkDouble(section, path, errorType, forceDouble);
        if (configStatus && !range.contains(section.getDouble(path))) {
            this.attemptConsoleMsg(errorType, section, path, null, getRangeMsg(range));
            return false;
        }
        return configStatus;
    }

    /**
     * Checks a double.
     * <p>This method does not check for a strict double value (forceDouble = false).
     * @see ConfigChecker#checkDouble(ConfigurationSection, String, ConsoleErrorType, Range, boolean)
     */
    public boolean checkDouble(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, @NotNull final Range<Double> range) {
        return this.checkDouble(section, path, errorType, range, false);
    }

    /**
     * Checks a long, returning a default value if not found.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param forceLong should config value be strictly long?
     * @return config value (if set correctly) or value
     */
    public long checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final long value, final boolean forceLong) {
        String longMsg;
        if (forceLong)
            longMsg = this.forceLongMsg;
        else
            longMsg = this.longMsg;

        if (section.contains(path)) {
            if (!(section.isLong(path) || (section.isInt(path) && !forceLong))) {
                this.attemptConsoleMsg(errorType, section, path, value, longMsg);
                return value;
            }
            return section.getLong(path);
        }
        this.attemptConsoleMsg(errorType, section, path, value, noPathMsg);
        return value;
    }

    /**
     * Checks a long, returning a default value if not found.
     * <p>This method does not check for a strict long value (forceLong = false).
     * @see ConfigChecker#checkLong(ConfigurationSection, String, ConsoleErrorType, long, boolean)
     */
    public long checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final long value) {
        return this.checkLong(section, path, errorType, value, false);
    }

    /**
     * Checks a long, returning a default value if not found.
     * @see ConfigChecker#checkLong(ConfigurationSection, String, ConsoleErrorType, long, boolean)
     */
    public long checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value, final boolean forceLong) {
        return checkLong(section, path, errorType, (long) value, forceLong);
    }

    /**
     * Checks a long, returning a default value if not found.
     * <p>This method does not check for a strict long value (forceLong = false).
     * @see ConfigChecker#checkLong(ConfigurationSection, String, ConsoleErrorType, long, boolean)
     */
    public long checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value) {
        return this.checkLong(section, path, errorType, (long) value);
    }

    /**
     * Checks a long.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param forceLong should config value be strictly double?
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final boolean forceLong) {
        String longMsg;
        if (forceLong)
            longMsg = this.forceLongMsg;
        else
            longMsg = this.longMsg;

        if (section.contains(path)) {
            if (!(section.isLong(path) || (section.isInt(path) && !forceLong))) {
                this.attemptConsoleMsg(errorType, section, path, null, longMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section, path, null, noPathMsg);
        return false;
    }

    /**
     * Checks a long.
     * <p>This method does not check for a strict long value (forceLong = false).
     * @see ConfigChecker#checkLong(ConfigurationSection, String, ConsoleErrorType, boolean)
     */
    public boolean checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType) {
        return this.checkLong(section, path, errorType, false);
    }

    /**
     * Checks a long, returning a default value if not found or within the given range.
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range range config value or value must be element of
     * @param forceLong should config value be strictly long?
     * @return config value if set correctly and in range, value otherwise
     * @throws IllegalArgumentException if value is not within the given range
     */
    public long checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final long value, @NotNull final Range<Long> range, final boolean forceLong)
        throws IllegalArgumentException {
        if (!range.contains(value))
            throw new IllegalArgumentException(getRangeMsg(range));

        long testValue = this.checkLong(section, path, errorType, value, forceLong);
        if (range.contains(testValue)) {
            return testValue;
        } else {
            this.attemptConsoleMsg(errorType, section, path, value, getRangeMsg(range));
            return value;
        }
    }

    /**
     * Checks a long, returning a default value if not found or within the given range.
     * <p>This method does not check for a strict long value (forceLong = false).
     * @see ConfigChecker#checkLong(ConfigurationSection, String, ConsoleErrorType, long, Range, boolean)
     */
    public long checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final long value, @NotNull final Range<Long> range)
        throws IllegalArgumentException {
        return this.checkLong(section, path, errorType, value, range, false);
    }

    /**
     * Checks a long, returning a default value if not found or within the given range.
     * @see ConfigChecker#checkLong(ConfigurationSection, String, ConsoleErrorType, long, Range, boolean)
     */
    public long checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value, @NotNull final Range<Long> range, final boolean forceLong)
        throws IllegalArgumentException {
        return this.checkLong(section, path, errorType, (long) value, forceLong);
    }

    /**
     * Checks a long, returning a default value if not found or within the given range.
     * <p>This method does not check for a strict long value (forceLong = false).
     * @see ConfigChecker#checkLong(ConfigurationSection, String, ConsoleErrorType, long, Range, boolean)
     */
    public long checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, final int value, @NotNull final Range<Long> range)
        throws IllegalArgumentException {
        return this.checkLong(section, path, errorType, (long) value, range, false);
    }

    /**
     * Checks a long.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param range range config value or value must be element of
     * @param forceLong should config value be strictly long?
     * @return true if set correctly and in range, false otherwise
     */
    public boolean checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, @NotNull final Range<Long> range, final boolean forceLong) {
        boolean configStatus = this.checkDouble(section, path, errorType, forceLong);
        if (configStatus && !range.contains(section.getLong(path))) {
            this.attemptConsoleMsg(errorType, section, path, null, getRangeMsg(range));
            return false;
        }
        return configStatus;
    }

    /**
     * Checks a long.
     * <p>This method does not check for a strict long value (forceLong = false).
     * @see ConfigChecker#checkLong(ConfigurationSection, String, ConsoleErrorType, Range, boolean)
     */
    public boolean checkLong(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, @NotNull final Range<Long> range) {
        return this.checkLong(section, path, errorType, range, false);
    }


    /**
     * Checks a string, returning a default value if not found.
     * <p>Note: This method might be null if config value is not set correctly and the default value is null.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly) or value
     */
    @Nullable
    public String checkString(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, @Nullable String value) {
        if (section.contains(path)) {
            if (!section.isString(path)) {
                this.attemptConsoleMsg(errorType, section, path, value, stringMsg);
                return value;
            }
            return section.getString(path);
        }
        this.attemptConsoleMsg(errorType, section, path, value, noPathMsg);
        return value;
    }

    /**
     * Checks a string.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkString(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isString(path)) {
                this.attemptConsoleMsg(errorType, section, path, null, stringMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section, path, null, noPathMsg);
        return false;
    }

    /**
     * Gets a configSection by path.
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return ConfigurationSection (if set correctly) or null
     */
    @Nullable
    public ConfigurationSection checkConfigSection(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isConfigurationSection(path)) {
                this.attemptConsoleMsg(errorType, section, path, null, configSectionMsg);
                return null;
            }
            return section.getConfigurationSection((path));
        }
        this.attemptConsoleMsg(errorType, section, path, null, noPathMsg);
        return null;
    }

    /**
     * Gets the requested ConfigurationSerializableCheckable.
     * <p>Note: In case of an error-msg this will print the simpleClassName of T instead of value.toString()
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param clazz the serializable class
     * @param overwriteValues should values get overwritten?
     * @param <T> the type of ConfigurationSerializableCheckable
     * @return config value (if data-types are set correctly) or null
     */
    @Nullable
    public <T extends ConfigurationSerializableCheckable> T checkSerializable(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, @NotNull final Class<T> clazz, final boolean overwriteValues) {
        if (!section.contains(path)) {
            this.attemptConsoleMsg(errorType, section, path, " of " + clazz.getSimpleName(), noPathMsg);
            return null;
        }
        T retValue = section.getSerializable(path, clazz);
        if (retValue == null) {
            this.attemptConsoleMsg(errorType, section, path, " of " + clazz.getSimpleName(), "values are seriously incorrect (data types are wrong).");
            return null;
        } else {
            if (!retValue.checkValues(this, section, path, errorType, overwriteValues) && overwriteValues) {
                this.attemptConsoleMsg(errorType, section, path, null, "At least one value got overwritten.");
            }
            return retValue;
        }
    }

    /**
     * Gets the requested ConfigurationSerializableCheckable by path or value.
     * <p>Note: In case of an error-msg this will print the simpleClassName of T instead of value.toString()
     * @param section the section to check
     * @param path the path within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param overwriteValues should values get overwritten?
     * @param <T> the type of ConfigurationSerializableCheckable
     * @return config value (if data-types are set correctly) or value
     */
    @NotNull
    public <T extends ConfigurationSerializableCheckable> T checkSerializable(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final ConsoleErrorType errorType, @NotNull final T value, final boolean overwriteValues) {
        if (!section.contains(path)) {
            this.attemptConsoleMsg(errorType, section, path, " of " + value.getClass().getSimpleName(), noPathMsg);
            return value;
        }
        @SuppressWarnings("unchecked")
        T retValue = (T) section.getSerializable(path, value.getClass());
        if (retValue == null) {
            this.attemptConsoleMsg(errorType, section, path, " of " + value.getClass().getSimpleName(), "values are seriously incorrect (data types are wrong).");
            return value;
        } else {
            if (!retValue.checkValues(this, section, path, errorType, overwriteValues) && overwriteValues) {
                this.attemptConsoleMsg(errorType, section, path, null, "At least one value got overwritten.");
            }
            return retValue;
        }
    }

    /**
     * Checks a vector, returning a default value if not found.
     * <p>Note: This method might be null if config value is not set correctly and the default value is null.
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @return config value (if set correctly) or value
     */
    @Nullable
    public Vector checkVector(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull ConsoleErrorType errorType, @Nullable Vector value) {
        if (section.contains(path)) {
            if (!section.isVector(path)) {
                this.attemptConsoleMsg(errorType, section, path, value, vectorMsg);
                return value;
            }
            return section.getVector(path);
        }
        this.attemptConsoleMsg(errorType, section, path, value, noPathMsg);
        return value;
    }

    /**
     * Checks a vector.
     * @param section the section to check.
     * @param path the path within the section.
     * @param errorType the ConsoleErrorType (controls console msg)
     * @return true if config value is set correctly, false otherwise
     */
    public boolean checkVector(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull ConsoleErrorType errorType) {
        if (section.contains(path)) {
            if (!section.isVector(path)) {
                this.attemptConsoleMsg(errorType, section, path, null, intMsg);
                return false;
            }
            return true;
        }
        this.attemptConsoleMsg(errorType, section, path, null, noPathMsg);
        return false;
    }

    // =================================================================
    // VALUE CHECKER
    // =================================================================

    /**
     * Checks a value like ConfigChecker.
     * <p>Note: This method might be null if config value is not set correctly and the default value is null.
     * @param checkValue the value to check
     * @param sectionPath the sectionPath
     * @param path the specific path of the given value within the section
     * @param errorType the ConsoleErrorType (controls console msg)
     * @param value the default value
     * @param range the range to check
     * @param <T> the comparable such as Integer, Double etc.
     * @return checkValue (if within the range) or value
     */
    @Nullable
    public <T extends Comparable<?>> T checkValue(@NotNull final T checkValue, @Nullable final String sectionPath, @Nullable final String path, @NotNull final ConsoleErrorType errorType, @Nullable final T value, @NotNull final Range<Comparable<?>> range) {
        if (range.contains(checkValue))
            return checkValue;
        this.attemptConsoleMsg(errorType, sectionPath, path, value, getRangeMsg(range));
        return value;
    }
}