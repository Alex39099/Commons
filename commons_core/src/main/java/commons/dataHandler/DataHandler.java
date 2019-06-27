package commons.dataHandler;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DataHandler {

    private JavaPlugin plugin;
    private File subDirectory;

    public DataHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.subDirectory = plugin.getDataFolder();
    }

    public DataHandler(final JavaPlugin plugin, final String subDirName) throws LoadSaveException {
        this(plugin);
        this.subDirectory = this.getSubDirectory(subDirName);
    }

    private File getSubDirectory(final String subDirName) throws LoadSaveException {
        File dir = new File(plugin.getDataFolder(), subDirName);
        if (!dir.exists())
            if (!dir.mkdirs())
                throw new LoadSaveException("could not create subDirectory");
        return dir;
    }

    private String getYmlFileName(final String fileName) {
        if (!fileName.endsWith(".yml"))
            return fileName  + ".yml";
        return fileName;
    }

    /**
     *
     * @param fileName the fileName (with or without .yml)
     * @param ymlFile the yml-Configuration to save.
     * @throws LoadSaveException if file could not be saved.
     */
    public void saveYmlFile(String fileName, final YamlConfiguration ymlFile) throws LoadSaveException {
        fileName = this.getYmlFileName(fileName);
        File file = new File(subDirectory, fileName);

        try {
            ymlFile.save(file);
        } catch (IOException e) {
            throw new LoadSaveException("file " + fileName + "could not be saved");
        }
    }

    /**
     *
     * @param fileName the fileName (with or without .yml)
     * @return the loaded yml-Configuration
     */
    public YamlConfiguration loadYmlFile(String fileName) {
        fileName = this.getYmlFileName(fileName);
        File file = new File(subDirectory, fileName);
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     *
     * @param serializableClass class extending ConfigurationSerializable
     * @param fileName the fileName
     * @param path the path
     * @param <T> the type of ConfigurationSerializable
     * @return the requested ConfigurationSerializable
     * @throws IllegalArgumentException if path is not existent or if path does not contain a valid obj of serializableClass
     */
    public <T extends ConfigurationSerializable> T loadConfigurationSerializable(final Class<T> serializableClass, final String fileName, final String path)
        throws IllegalArgumentException {
        YamlConfiguration ymlFile = this.loadYmlFile(fileName);
        return this.loadConfigurationSerializable(serializableClass, ymlFile, path);
    }


    /**
     *
     * @param serializableClass class extending ConfigurationSerializable
     * @param section the section to check
     * @param path the path within the section
     * @param <T> the type of ConfigurationSerializable
     * @return the requested ConfigurationSerializable
     * @throws IllegalArgumentException if path is not existent or if path does not contain a valid obj of serializableClass
     */
    public <T extends ConfigurationSerializable> T loadConfigurationSerializable(final Class<T> serializableClass, final ConfigurationSection section, final String path)
        throws IllegalArgumentException {
        if (!section.contains(path))
            throw new IllegalArgumentException("path is not existent in the given file");
        Object obj = section.get(path);
        if (!serializableClass.isInstance(obj)) {
            throw new IllegalArgumentException("path does not contain a valid obj of serializableClass");
        }
        return serializableClass.cast(section.get(path));
    }

    /**
     *
     * @param serializableClass class extending ConfigurationSerializable
     * @param fileName the fileName
     * @param <T> the type of ConfigurationSerializable
     * @return a list of all available configurationSerializable within the file (not deep)
     */
    public <T extends ConfigurationSerializable> List<T> loadConfigurationSerializables(final Class<T> serializableClass, final String fileName) {
        return this.loadConfigurationSerializables(serializableClass, this.loadYmlFile(fileName));
    }

    /**
     *
     * @param serializableClass class extending ConfigurationSerializable
     * @param section the section to check.
     * @param <T> the type of ConfigurationSerializable
     * @return a list of all available configurationSerializable within the section (not deep)
     */
    public <T extends ConfigurationSerializable> List<T> loadConfigurationSerializables(final Class<T> serializableClass, final ConfigurationSection section) {
        List<T> list = new ArrayList<>();
        for (String path : section.getKeys(false)) {
            Object obj = section.get(path);
            if (serializableClass.isInstance(obj)) {
                @SuppressWarnings("unchecked")
                T tObj = (T) obj;
                list.add(tObj);
            }
        }
        return list;
    }
}
