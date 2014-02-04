package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.github.games647.scoreboardstats.Language;
import com.github.games647.scoreboardstats.ScoreboardStats;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/* package */ class DatabaseConfiguration {

    private final ScoreboardStats pluginInstance;

    private ServerConfig serverConfig;

    protected DatabaseConfiguration(ScoreboardStats instance) {
        pluginInstance = instance;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    protected void loadConfiguration() {
        final ServerConfig databaseConfig = new ServerConfig();
        databaseConfig.setRegister(false);
        databaseConfig.addClass(PlayerStats.class);
        //Give the database a specific name
        databaseConfig.setName(pluginInstance.getName());
        databaseConfig.setValidateOnSave(true);

        final DataSourceConfig sqlConfig = getSqlConfig(databaseConfig);
        //set a correct path
        sqlConfig.setUrl(replaceUrlString(sqlConfig.getUrl()));

        serverConfig = databaseConfig;
    }

    private DataSourceConfig getSqlConfig(ServerConfig serverConfig) {
        FileConfiguration sqlConfig;
        DataSourceConfig config;

        final File file = new File(pluginInstance.getDataFolder(), "sql.yml");
        //Check if the file exists. If so load the settings form there
        if (file.exists()) {
            sqlConfig = YamlConfiguration.loadConfiguration(file);
            config = new DataSourceConfig();

            config.setUsername(sqlConfig.getString("SQL-Settings.Username"));
            config.setPassword(sqlConfig.getString("SQL-Settings.Password"));
            config.setIsolationLevel(TransactionIsolation.getLevel(sqlConfig.getString("SQL-Settings.Isolation")));
            config.setDriver(sqlConfig.getString("SQL-Settings.Driver"));
            config.setUrl(sqlConfig.getString("SQL-Settings.Url"));

            serverConfig.setDataSourceConfig(config);
        } else {
            //Create a new configuration based on the default settings form bukkit.yml
            pluginInstance.saveResource("sql.yml", false);
            pluginInstance.getServer().configureDbConfig(serverConfig);

            config = serverConfig.getDataSourceConfig();

            sqlConfig = YamlConfiguration.loadConfiguration(file);
            sqlConfig.set("SQL-Settings.Username", config.getUsername());
            sqlConfig.set("SQL-Settings.Password", config.getPassword());
            sqlConfig.set("SQL-Settings.Isolation", TransactionIsolation.getLevelDescription(config.getIsolationLevel()));
            sqlConfig.set("SQL-Settings.Driver", config.getDriver());
            sqlConfig.set("SQL-Settings.Url", config.getUrl());
            try {
                sqlConfig.save(file);
            } catch (IOException ex) {
                pluginInstance.getLogger().warning(Language.get("databaseConfigSaveError"));
                pluginInstance.getLogger().throwing(pluginInstance.getClass().getName(), "getSqlConfig", ex);
            }
        }

        config.setMinConnections(sqlConfig.getInt("SQL-Settings.MinConnections"));
        config.setMaxConnections(sqlConfig.getInt("SQL-Settings.MaxConnections"));
        config.setWaitTimeoutMillis(sqlConfig.getInt("SQL-Settings.Timeout"));

        return config;
    }

    private String replaceUrlString(String input) {
        final Pattern pat = Pattern.compile("\\\\");
        return input
                .replace("{DIR}", pat.matcher(pluginInstance.getDataFolder().getPath()).replaceAll("/") + '/')
                .replace("{NAME}", pluginInstance.getName());
    }
}
