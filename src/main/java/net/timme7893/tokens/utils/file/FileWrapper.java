package net.timme7893.tokens.utils.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


public class FileWrapper {

    private final JavaPlugin plugin;
    private final String folderName, fileName;
    private FileConfiguration config;
    private File configFile;

    public FileWrapper(JavaPlugin instance, String folderName, String fileName) {
        this.plugin = instance;
        this.folderName = folderName;
        this.fileName = fileName.split("/").length > 1 ? fileName.split("/")[1] : fileName;

        File config = new File(this.plugin.getDataFolder(), (this.folderName.isEmpty() ? "" : this.folderName + "/") + this.getFileName());
        if (!config.exists())
            this.plugin.saveResource((this.folderName.isEmpty() ? "" : this.folderName + "/") + this.getFileName(), false);
    }

    public String getFileName() {
        return fileName;
    }

    public void createNewFile(final String message, final String header) {
        reloadConfig();
        saveConfig();
        loadConfig(header);

        if (message != null) {
            plugin.getLogger().info(message);
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void loadConfig(final String header) {
        config.options().header(header);
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), (this.folderName.isEmpty() ? "" : this.folderName + "/") + this.fileName);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (final IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }
}
