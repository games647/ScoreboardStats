package com.github.games647.scoreboardstats.pvpstats;

import com.zaxxer.hikari.HikariConfig;

import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Configuration for the SQL database.
 *
 * @see Database
 */
public class DatabaseConfiguration {

    private final Plugin plugin;

    private HikariConfig serverConfig;
    private String tablePrefix;

    DatabaseConfiguration(Plugin instance) {
        plugin = instance;
    }

    /**
     * Get the SQL configuration
     *
     * @return the server configuration
     */
    public HikariConfig getServerConfig() {
        return serverConfig;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    /**
     * Loads the eBean configuration
     */
    public void loadConfiguration() {
        serverConfig = new HikariConfig();

        Path file = plugin.getDataFolder().toPath().resolve("sql.yml");
        //Check if the file exists. If so load the settings form there
        if (Files.notExists(file)) {
            //Create a new configuration based on the default settings form bukkit.yml
            plugin.saveResource("sql.yml", false);
        }

        FileConfiguration sqlConfig = YamlConfiguration.loadConfiguration(file.toFile());

        ConfigurationSection sqlSettingSection = sqlConfig.getConfigurationSection("SQL-Settings");
        serverConfig.setUsername(sqlSettingSection.getString("Username"));
        serverConfig.setPassword(sqlSettingSection.getString("Password"));
        serverConfig.setDriverClassName(sqlSettingSection.getString("Driver"));
        serverConfig.setJdbcUrl(replaceUrlString(sqlSettingSection.getString("Url")));
        if (serverConfig.getDriverClassName().contains("sqlite")) {
            serverConfig.setConnectionTestQuery("SELECT 1");
        }

        tablePrefix = sqlSettingSection.getString("tablePrefix", "");
    }

    private String replaceUrlString(String input) {
        //Replace the windows seperators ('\') with a '/'; \\ escape regEx --> \\\\ escape java
        String result = input.replaceAll("\\{DIR\\}", plugin.getDataFolder().getPath().replaceAll("\\\\", "/") + '/');
        result = result.replaceAll("\\{NAME\\}", plugin.getDescription().getName().replaceAll("[^\\w-]", ""));

        return result;
    }
}
