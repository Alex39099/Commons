package commons.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class ConfigurationSerializableCheckable implements ConfigurationSerializable {

    public ConfigurationSerializableCheckable() {}

    abstract boolean checkConfigSection(ConfigChecker checker, ConfigurationSection section, String path, ConsoleErrorType errorType);
}
