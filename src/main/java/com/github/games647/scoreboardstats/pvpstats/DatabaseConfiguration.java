package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.github.games647.scoreboardstats.Language;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
        databaseConfig.setClasses(getDatabaseClasses());
        databaseConfig.setName(pluginInstance.getName());

        final DataSourceConfig ds = getSqlConfig(databaseConfig);
        ds.setUrl(replaceUrlString(ds.getUrl()));

        serverConfig = databaseConfig;
    }

    private List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> classes = Lists.newArrayList();
        classes.add(PlayerStats.class);
        return classes;
    }

    private DataSourceConfig getSqlConfig(ServerConfig serverConfig) {
        final File file = new File(pluginInstance.getDataFolder(), "sql.yml");
        final FileConfiguration sqlConfig;
        final DataSourceConfig config;
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
        return input.replace("{DIR}", pat.matcher(pluginInstance.getDataFolder().getPath()).replaceAll("/") + '/')
                .replace("{NAME}", pluginInstance.getName());
    }
}
