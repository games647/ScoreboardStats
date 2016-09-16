package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.GlobalProperties;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.Version;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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

    private ServerConfig serverConfig;
    private boolean uuidUse;

    DatabaseConfiguration(Plugin instance) {
        plugin = instance;
    }

    /**
     * Get the SQL configuration
     *
     * @return the server configuration
     */
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    /**
     * Get whether the stats should be searched by the UUID or name
     *
     * @return whether the stats should be searched by the UUID or name
     */
    public boolean isUuidUse() {
        return uuidUse;
    }

    /**
     * Loads the eBean configuration
     */
    public void loadConfiguration() {
        //If the server path contains non-latin characters, ebean will fail because of the old version, so use this
        GlobalProperties.put("ebean.classpathreader", PathReader.class.getName());

        ServerConfig databaseConfig = new ServerConfig();
        databaseConfig.addClass(PlayerStats.class);
        //we will replace it on every reload. As we cannot unregister the server easier, we choose this
        databaseConfig.setRegister(false);
        //Give the database a specific name
        databaseConfig.setName(plugin.getName());
        //don't put invalid values to the database
        databaseConfig.setValidateOnSave(true);

        DataSourceConfig sqlConfig = getSqlConfig(databaseConfig);
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

        File file = new File(plugin.getDataFolder(), "sql.yml");
        //Check if the file exists. If so load the settings form there
        if (file.exists()) {
            sqlConfig = YamlConfiguration.loadConfiguration(file);
            config = new DataSourceConfig();

            uuidUse = sqlConfig.getBoolean("uuidUse", Version.isUUIDCompatible());
            if (Version.isUUIDCompatible() && !uuidUse) {
                sqlConfig.set("uuidUse", true);
                uuidUse = true;
                plugin.getLogger().info("Forcing uuidUse to true, because the server is uuid compatible");
                try {
                    sqlConfig.save(file);
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.WARNING, Lang.get("databaseConfigSaveError"), ex);
                }
            }

            ConfigurationSection sqlSettingSection = sqlConfig.getConfigurationSection("SQL-Settings");
            config.setUsername(sqlSettingSection.getString("Username"));
            config.setPassword(sqlSettingSection.getString("Password"));
            config.setIsolationLevel(TransactionIsolation.getLevel(sqlSettingSection.getString("Isolation")));
            config.setDriver(sqlSettingSection.getString("Driver"));
            config.setUrl(sqlSettingSection.getString("Url"));

            String tablePrefix = sqlSettingSection.getString("tablePrefix", "");
            setTablePrefix(tablePrefix);

            serverConfig.setDataSourceConfig(config);
        } else {
            //Create a new configuration based on the default settings form bukkit.yml
            plugin.saveResource("sql.yml", false);
            //Some hosters configures the bukkit.yml database settings. Use them as default as they can be correct
            plugin.getServer().configureDbConfig(serverConfig);

            config = serverConfig.getDataSourceConfig();

            sqlConfig = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection sqlSettingSection = sqlConfig.getConfigurationSection("SQL-Settings");
            sqlSettingSection.set("Username", config.getUsername());
            sqlSettingSection.set("Password", config.getPassword());
            sqlSettingSection.set("Isolation", TransactionIsolation.getLevelDescription(config.getIsolationLevel()));
            sqlSettingSection.set("Driver", config.getDriver());
            sqlSettingSection.set("Url", config.getUrl());

            sqlConfig.set("uuidUse", Version.isUUIDCompatible());
            uuidUse = sqlConfig.getBoolean("uuidUse");
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

    private void setTablePrefix(String prefix) {
        final Table oldAnnotation = PlayerStats.class.getAnnotation(Table.class);
        Table newAnnotation = new Table() {
            @Override
            public String name() {
                return prefix + '_' + oldAnnotation.name();
            }

            @Override
            public String catalog() {
                return "";
            }

            @Override
            public String schema() {
                return "";
            }

            @Override
            public UniqueConstraint[] uniqueConstraints() {
                return new UniqueConstraint[]{};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return oldAnnotation.annotationType();
            }
        };

        try {
            Field field = Class.class.getDeclaredField("annotations");
            field.setAccessible(true);

            Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) field.get(Table.class);
            annotations.put(Table.class, newAnnotation);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }
}
