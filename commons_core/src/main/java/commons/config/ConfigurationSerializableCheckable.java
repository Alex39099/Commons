package commons.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@SuppressWarnings("WeakerAccess")
public abstract class ConfigurationSerializableCheckable implements ConfigurationSerializable {

    /**
     * This constructor is used in configChecker, DO NOT overwrite this with a weaker access!
     * @deprecated not needed anymore.
     */
    public ConfigurationSerializableCheckable() {}

    /**
     * Gets executed by ConfigChecker to check an instance. This should make use of the configChecker's boolean methods.
     * (except checkConfigSection for the name! Please use section#getConfigurationSection(path) != null)
     * @param checker the configChecker
     * @param section the section
     * @param path the path within the section (i. e. the name)
     * @param errorType the ConsoleErrorType (controls console messages)
     * @return true if all values are set correctly (data types), false otherwise.
     * @deprecated
     */
    @SuppressWarnings("unused")
    public boolean checkConfigSection(ConfigChecker checker, ConfigurationSection section, String path, ConsoleErrorType errorType) {
        return false;
    }

    /**
     * Gets executed by ConfigChecker to check an instance.
     * @param checker the configChecker
     * @param section the section
     * @param path the path within the section (i. e. the name)
     * @param errorType the ConsoleErrorType (controls console messages)
     * @param overwrite should values get overwritten?
     * @return true if all values are set correctly, false otherwise. This has no impact on ConfigChecker!
     */
    abstract public boolean checkValues(ConfigChecker checker, ConfigurationSection section, String path, ConsoleErrorType errorType, boolean overwrite);
}
