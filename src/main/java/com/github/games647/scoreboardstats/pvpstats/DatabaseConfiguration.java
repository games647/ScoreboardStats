package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.GlobalProperties;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ScoreboardStats;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Configuration for the SQL database.
 *
 * @see Database
 */
public class DatabaseConfiguration {

    private final ScoreboardStats plugin;

    private ServerConfig serverConfig;
    private boolean uuidUse;

    DatabaseConfiguration(ScoreboardStats instance) {
        plugin = instance;
    }

    /**
     * Gets the SQL configuration
     *
     * @return the server configuration
     */
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public boolean isUuidUse() {
        return uuidUse;
    }

    /**
     * Loads the eBean configuration
     */
    public void loadConfiguration() {
        GlobalProperties.put("ebean.classpathreader", "com.github.games647.scoreboardstats.pvpstats.PathReader");

        final ServerConfig databaseConfig = new ServerConfig();
        databaseConfig.addClass(PlayerStats.class);
        //we will replace it on every reload. As we cannot unregister the server easier, we choose this
        databaseConfig.setRegister(false);
        //Give the database a specific name
        databaseConfig.setName(plugin.getName());
        //don't put invalid values to the database
        databaseConfig.setValidateOnSave(true);

        final DataSourceConfig sqlConfig = getSqlConfig(databaseConfig);
        //set a correct path
        sqlConfig.setUrl(replaceUrlString(sqlConfig.getUrl()));
        //choose a heartbeat that just respond with a minimum of cpu usage
        sqlConfig.setHeartbeatSql("SELECT 1 LIMIT 1");

        if (sqlConfig.getDriver().contains("sqlite")) {
            //According to a bug in ebean the "autoincrement" will be before
            //"primary key" which occures a syntax exception on sqlite
            databaseConfig.setDatabasePlatform(new SQLitePlatform());
            databaseConfig.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        serverConfig = databaseConfig;
    }

    private DataSourceConfig getSqlConfig(ServerConfig serverConfig) {
        FileConfiguration sqlConfig;
        DataSourceConfig config;

        final File file = new File(plugin.getDataFolder(), "sql.yml");
        //Check if the file exists. If so load the settings form there
        if (file.exists()) {
            sqlConfig = YamlConfiguration.loadConfiguration(file);
            config = new DataSourceConfig();

            uuidUse = sqlConfig.getBoolean("uuidUse", false);

            final ConfigurationSection sqlSettingSection = sqlConfig.getConfigurationSection("SQL-Settings");
            config.setUsername(sqlSettingSection.getString("Username"));
            config.setPassword(sqlSettingSection.getString("Password"));
            config.setIsolationLevel(TransactionIsolation.getLevel(sqlSettingSection.getString("Isolation")));
            config.setDriver(sqlSettingSection.getString("Driver"));
            config.setUrl(sqlSettingSection.getString("Url"));

            serverConfig.setDataSourceConfig(config);
        } else {
            //Create a new configuration based on the default settings form bukkit.yml
            plugin.saveResource("sql.yml", false);
            //Some hosters configures the bukkit.yml database settings. Use them as default as they can be correct
            plugin.getServer().configureDbConfig(serverConfig);

            config = serverConfig.getDataSourceConfig();

            sqlConfig = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection sqlSettingSection = sqlConfig.getConfigurationSection("SQL-Settings");
            sqlSettingSection.set("Username", config.getUsername());
            sqlSettingSection.set("Password", config.getPassword());
            sqlSettingSection.set("Isolation", TransactionIsolation.getLevelDescription(config.getIsolationLevel()));
            sqlSettingSection.set("Driver", config.getDriver());
            sqlSettingSection.set("Url", config.getUrl());
            try {
                sqlConfig.save(file);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.WARNING, Lang.get("databaseConfigSaveError"), ex);
            }
        }

        config.setWaitTimeoutMillis(sqlConfig.getInt("SQL-Settings.Timeout"));

        return config;
    }

    private String replaceUrlString(String input) {
        //Replace the windows seperators ('\') with a '/'; \\ escape regEx --> \\\\ escape java
        String result = input.replaceAll("\\{DIR\\}", plugin.getDataFolder().getPath().replaceAll("\\\\", "/") + '/');
        result = result.replaceAll("\\{NAME\\}", plugin.getDescription().getName().replaceAll("[^\\w-]", ""));

        return result;
    }
}
