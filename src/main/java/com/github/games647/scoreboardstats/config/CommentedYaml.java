package com.github.games647.scoreboardstats.config;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Represents an easy way to load and save to yaml configs. Furthermore support
 * this class UTF-8 even before Bukkit introduced it and will also support comments
 * soon.
 *
 * @param <T> the plugin type
 */
public class CommentedYaml<T extends Plugin> {

    private static final String COMMENT_PREFIX = "# ";
    private static final String FILE_NAME = "config.yml";

    protected final transient T plugin;
    protected transient FileConfiguration config;

    public CommentedYaml(T plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the YAML file configuration from the disk while loading it
     * explicit with UTF-8. This allows umlauts and other UTF-8 characters
     * for all Bukkit versions.
     *
     * Bukkit add also this feature since
     * https://github.com/Bukkit/Bukkit/commit/24883a61704f78a952e948c429f63c4a2ab39912
     * To be allow the same feature for all Bukkit version, this method was
     * created.
     *
     * @return the loaded file configuration
     */
    public FileConfiguration getConfigFromDisk() {
        File file = new File(plugin.getDataFolder(), FILE_NAME);

        YamlConfiguration newConf = new YamlConfiguration();
        newConf.setDefaults(getDefaults());

        try {
            //UTF-8 should be available on all java running systems
            List<String> lines = Files.readLines(file, Charsets.UTF_8);

            load(lines, newConf);
        } catch (InvalidConfigurationException | IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't load the configuration", ex);
        }

        return newConf;
    }

    protected void loadConfig() {
        config = getConfigFromDisk();

        for (Field field : getClass().getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())
                    || !field.getType().isPrimitive() && field.getType() != String.class) {
                continue;
            }

            ConfigNode node = field.getAnnotation(ConfigNode.class);
            String path = field.getName();
            if (node != null && !Strings.isNullOrEmpty(path)) {
                path = node.path();
            }

            field.setAccessible(true);
            setField(path, field);
        }
    }

    protected void saveConfig() {
        if (config == null) {
            return;
        }

        //todo add comment support
        try {
            Files.write(config.saveToString(), new File(plugin.getDataFolder(), FILE_NAME), Charsets.UTF_8);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    private void setField(String path, Field field) throws IllegalArgumentException {
        if (config.isSet(path)) {
            try {
                if (config.isString(path)) {
                    field.set(this, ChatColor.translateAlternateColorCodes('&', config.getString(path)));
                } else {
                    field.set(this, config.get(path));
                }
            } catch (IllegalAccessException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        } else {
            plugin.getLogger().log(Level.INFO, "Path not fond {0}", path);
        }
    }

    private void load(List<String> lines, YamlConfiguration newConf) throws InvalidConfigurationException {
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            //remove the silly tab error from yaml
            builder.append(line.replace("\t", "    "));
            //indicates a new line
            builder.append('\n');
        }

        newConf.loadFromString(builder.toString());
    }

    /**
     * Get the default configuration located in the plugin jar
     *
     * @return the default configuration
     */
    private FileConfiguration getDefaults() {
        YamlConfiguration defaults = new YamlConfiguration();
        InputStream defConfigStream = plugin.getResource(FILE_NAME);
        if (defConfigStream != null) {
            try {
                InputStreamReader reader = new InputStreamReader(defConfigStream, Charsets.UTF_8);
                //stream will be closed in this method
                List<String> lines = CharStreams.readLines(reader);
                load(lines, defaults);
                return defaults;
            } catch (InvalidConfigurationException ex) {
                plugin.getLogger().log(Level.SEVERE, "Invalid Configuration", ex);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't load the configuration", ex);
            }
        }

        return defaults;
    }
}
