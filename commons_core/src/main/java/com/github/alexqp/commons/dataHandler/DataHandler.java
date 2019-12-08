package com.github.alexqp.commons.dataHandler;

import com.github.alexqp.commons.config.ConsoleErrorType;
import com.github.alexqp.commons.messages.ConsoleMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DataHandler {


    private JavaPlugin plugin;
    private File subDirectory;

    /**
     * Constructs a DataHandler with the plugin's folder as subDirectory.
     * @param plugin the plugin
     */
    public DataHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.subDirectory = plugin.getDataFolder();
    }

    /**
     * Constructs a DataHandler, may creates a new subDirectory.
     * @param plugin the plugin
     * @param subDirName the name of the subDirectory
     * @throws LoadSaveException if creation of subDirectory was somehow not possible.
     */
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

    /**
     * Resets the subDirectory.
     * @throws LoadSaveException if subDirectory is the plugin's folder or if deletion was somehow not possible.
     */
    public void resetSubDirectory() throws LoadSaveException {
        if (subDirectory.equals(plugin.getDataFolder())) {
            throw new LoadSaveException("cannot delete plugin folder.");
        }

        if (!this.deleteDirectory(subDirectory)) {
            throw new LoadSaveException("could not delete subDirectory.");
        }

        subDirectory = this.getSubDirectory(subDirectory.getName());
    }

    private boolean deleteDirectory(File directory) {
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File file : contents) {
                deleteDirectory(file);
            }
        }
        return directory.delete();
    }

    /**
     * Deletes a specific file.
     * @param fileName the fileName (with or without .yml)
     * @return true if file got deleted, false otherwise
     */
    public boolean deleteYmlFile(String fileName) {
        fileName = this.getYmlFileName(fileName);
        File file = new File(subDirectory, fileName);
        return file.delete();
    }

    /**
     * Deletes all files in the subDirectory except with given name.
     * @param fileNames a list of fileNames (with or without .yml) that should not be deleted
     * @return a list of fileNames that could not be deleted
     */
    public Set<String> deleteYmlFilesExcept(Set<String> fileNames) {
        Set<String> notDeleted = new HashSet<>();

        File[] contents = subDirectory.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (!file.getName().contains(".yml") || fileNames.contains(file.getName()) || fileNames.contains(file.getName().replace(".yml", "")))
                    continue;

                if (!file.delete()) {
                    notDeleted.add(file.getName());
                }
            }
        }
        return notDeleted;
    }

    private String getYmlFileName(final String fileName) {
        if (!fileName.endsWith(".yml"))
            return fileName  + ".yml";
        return fileName;
    }

    /**
     * Saves the given ymlFile.
     * @param fileName the fileName (with or without .yml)
     * @param ymlFile the yml-Configuration to save
     * @throws LoadSaveException if file could not be saved.
     * @see DataHandler#saveYmlFile(String, YamlConfiguration, boolean)
     */
    public void saveYmlFile(String fileName, final YamlConfiguration ymlFile) throws LoadSaveException {
        fileName = this.getYmlFileName(fileName);
        File file = new File(subDirectory, fileName);

        try {
            ymlFile.save(file);
        } catch (IOException e) {
            throw new LoadSaveException("file " + fileName + "could not be saved.");
        }
    }

    /**
     * Saves the given ymlFile, may sends error msg.
     * @param fileName the fileName (with or without .yml)
     * @param ymlFile the yml-Configuration to save
     * @param sendError should a msg be sent in case of an error?
     * @return true if saving was successful, false otherwise
     * @see DataHandler#saveYmlFile(String, YamlConfiguration)
     */
    public boolean saveYmlFile(String fileName, final YamlConfiguration ymlFile, boolean sendError)  {
        try {
            this.saveYmlFile(fileName, ymlFile);
            return true;
        }
        catch (LoadSaveException e) {
            if (sendError) {
                ConsoleMessage.send(ConsoleErrorType.ERROR, plugin, e.getMessage() + " Please check writing ability of directory");
            }
            return false;
        }
    }

    /**
     * Loads a YamlConfiguration into the given fileName.
     * @param fileName the fileName (with or without .yml)
     * @return the loaded yml-Configuration
     */
    public YamlConfiguration loadYmlFile(String fileName) {
        fileName = this.getYmlFileName(fileName);
        File file = new File(subDirectory, fileName);
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Loads a ConfigurationSerializable within the ymlFile as section.
     * @param serializableClass class extending ConfigurationSerializable
     * @param fileName the fileName (with or without .yml)
     * @param path the path
     * @param <T> the type of ConfigurationSerializable
     * @return the requested ConfigurationSerializable
     * @throws IllegalArgumentException if path is not existent or if path does not contain a valid obj of serializableClass
     * @see DataHandler#loadConfigurationSerializable(Class, ConfigurationSection, String)
     */
    public <T extends ConfigurationSerializable> T loadConfigurationSerializable(final Class<T> serializableClass, final String fileName, final String path)
        throws IllegalArgumentException {
        YamlConfiguration ymlFile = this.loadYmlFile(fileName);
        return this.loadConfigurationSerializable(serializableClass, ymlFile, path);
    }


    /**
     * Loads a ConfigurationSerializable with a section and path.
     * @param serializableClass class extending ConfigurationSerializable
     * @param section the section to check
     * @param path the path within the section
     * @param <T> the type of ConfigurationSerializable
     * @return the requested ConfigurationSerializable
     * @throws IllegalArgumentException if path is not existent or if path does not contain a valid obj of serializableClass
     * @see DataHandler#loadConfigurationSerializable(Class, String, String)
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
     * Loads multiple ConfigurationSerializables
     * @param serializableClass class extending ConfigurationSerializable
     * @param fileName the fileName (with or without .yml)
     * @param <T> the type of ConfigurationSerializable
     * @return a list of all available configurationSerializable within the file (not deep)
     * @see DataHandler#loadConfigurationSerializables(Class, ConfigurationSection)
     */
    public <T extends ConfigurationSerializable> List<T> loadConfigurationSerializables(final Class<T> serializableClass, final String fileName) {
        return this.loadConfigurationSerializables(serializableClass, this.loadYmlFile(fileName));
    }

    /**
     * Loads multiple ConfigurationSerializables within a section.
     * @param serializableClass class extending ConfigurationSerializable
     * @param section the section to check.
     * @param <T> the type of ConfigurationSerializable
     * @return a list of all available configurationSerializable within the section (not deep)
     * @see DataHandler#loadConfigurationSerializables(Class, String)
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
