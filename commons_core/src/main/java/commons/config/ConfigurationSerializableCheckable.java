package commons.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@SuppressWarnings("WeakerAccess")
public abstract class ConfigurationSerializableCheckable implements ConfigurationSerializable {

    public ConfigurationSerializableCheckable() {}

    /**
     * Gets executed by ConfigChecker to check an instance. This should make use of the configChecker's boolean methods.
     * @param checker the configChecker
     * @param section the section
     * @param path the path within the section (i. e. the name)
     * @param errorType the ConsoleErrorType (controls console messages)
     * @return true if all values are set correctly (data types), false otherwise.
     */
    abstract public boolean checkConfigSection(ConfigChecker checker, ConfigurationSection section, String path, ConsoleErrorType errorType);
}
