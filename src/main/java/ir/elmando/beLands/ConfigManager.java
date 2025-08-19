package ir.elmando.beLands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig(); // Make sure the default config exists
        this.config = plugin.getConfig();
    }

    /** Reloads config from disk */
    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        plugin.getLogger().info("Config reloaded!");
    }

    /**
     * Get a string from the config.
     */
    public String getString(String path) {
        if (!config.contains(path)) {
            plugin.getLogger().log(Level.WARNING, "Config path '" + path + "' not found!");
            return "";
        }
        return config.getString(path);
    }

    public @NotNull Component getMessage(String path, Map<String, String> replacements) {
        String msg = getString(path);

        if (replacements != null && !replacements.isEmpty()) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return MiniMessage.miniMessage().deserialize(msg);
    }

    public @NotNull Component getMessage(String path) {
        String msg = getString(path);
        return MiniMessage.miniMessage().deserialize(msg);
    }

    /**
     * Get an integer from the config.
     */
    public int getInt(String path) {
        if (!config.contains(path)) {
            plugin.getLogger().log(Level.WARNING, "Config path '" + path + "' not found!");
            return 0;
        }
        return config.getInt(path);
    }

    /**
     * Get a boolean from the config.
     */
    public boolean getBoolean(String path) {
        if (!config.contains(path)) {
            plugin.getLogger().log(Level.WARNING, "Config path '" + path + "' not found!");
            return false;
        }
        return config.getBoolean(path);
    }

    /**
     * Set a value in the config and save.
     */
    public void set(String path, Object value) {
        config.set(path, value);
        plugin.saveConfig();
    }

    /**
     * Check if a path exists in the config.
     */
    public boolean contains(String path) {
        return config.contains(path);
    }
}
