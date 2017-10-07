package com.github.games647.scoreboardstats.config;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

/**
 * Represents an easy way to load and save to yaml configs. Furthermore support
 * this class UTF-8 even before Bukkit introduced it and will also support comments
 * soon.
 */
class CommentedYaml {

    private static final String COMMENT_PREFIX = "# ";
    private static final String FILE_NAME = "config.yml";

    protected final transient Logger logger;
    protected final transient Path dataFolder;
    protected transient FileConfiguration config;

    CommentedYaml(Logger logger, Path dataFolder) {
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    /**
     * Gets the YAML file configuration from the disk while loading it
     * explicit with UTF-8. This allows umlauts and other UTF-8 characters
     * for all Bukkit versions.
     * <p>
     * Bukkit add also this feature since
     * https://github.com/Bukkit/Bukkit/commit/24883a61704f78a952e948c429f63c4a2ab39912
     * To be allow the same feature for all Bukkit version, this method was
     * created.
     *
     * @return the loaded file configuration
     */
    public FileConfiguration getConfigFromDisk() {
        Path file = dataFolder.resolve(FILE_NAME);

        YamlConfiguration newConf = new YamlConfiguration();
        newConf.setDefaults(getDefaults());
        try {
            //UTF-8 should be available on all java running systems
            List<String> lines = Files.readAllLines(file);
            load(lines, newConf);
        } catch (InvalidConfigurationException | IOException ex) {
            logger.error("Couldn't load the configuration", ex);
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
            List<String> lines = Collections.singletonList(config.saveToString());
            Files.write(dataFolder.resolve(FILE_NAME), lines);
        } catch (IOException ex) {
            logger.error("Failed to save config", ex);
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
                logger.error("Error setting field", ex);
            }
        } else {
            logger.error("Path not fond {}", path);
        }
    }

    private void load(Iterable<String> lines, YamlConfiguration newConf) throws InvalidConfigurationException {
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
    private Configuration getDefaults() {
        YamlConfiguration defaults = new YamlConfiguration();

        try (InputStream defConfigStream = getClass().getResourceAsStream('/' + FILE_NAME)) {
            try {
                Readable reader = new InputStreamReader(defConfigStream, Charsets.UTF_8);
                //stream will be closed in this method
                List<String> lines = CharStreams.readLines(reader);
                load(lines, defaults);
                return defaults;
            } catch (InvalidConfigurationException ex) {
                logger.error("Invalid Configuration", ex);
            } catch (IOException ex) {
                logger.error("Couldn't load the configuration", ex);
            }
        } catch (IOException ioEx) {
            logger.error("Failed to find default", ioEx);
        }

        return defaults;
    }
}
