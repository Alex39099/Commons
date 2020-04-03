package com.github.alexqp.commons.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * This class is used in {@link ConfigChecker}.
 * @see ConfigChecker#checkSerializable(ConfigurationSection, String, ConsoleErrorType, ConfigurationSerializableCheckable, boolean)
 * @see ConfigChecker#checkSerializable(ConfigurationSection, String, ConsoleErrorType, Class, boolean)
 */
@SuppressWarnings("WeakerAccess")
public interface ConfigurationSerializableCheckable extends ConfigurationSerializable {

    /**
     * Gets executed by ConfigChecker to check an instance.
     * @param checker the configChecker
     * @param section the section
     * @param path the path within the section (i. e. the name)
     * @param errorType the ConsoleErrorType (controls console messages)
     * @param overwrite should values get overwritten?
     * @return true if all values are set correctly, false otherwise. This has no impact on ConfigChecker if overwrite is false (i. e. no extra msg)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean checkValues(ConfigChecker checker, ConfigurationSection section, String path, ConsoleErrorType errorType, boolean overwrite);
}
