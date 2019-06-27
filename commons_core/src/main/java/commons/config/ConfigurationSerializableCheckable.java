package commons.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@SuppressWarnings("WeakerAccess")
public abstract class ConfigurationSerializableCheckable implements ConfigurationSerializable {

    public ConfigurationSerializableCheckable() {}

    abstract public boolean checkConfigSection(ConfigChecker checker, ConfigurationSection section, String path, ConsoleErrorType errorType);
}
