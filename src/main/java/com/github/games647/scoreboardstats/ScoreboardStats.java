package com.github.games647.scoreboardstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.github.games647.scoreboardstats.commands.DisableCommand;
import com.github.games647.scoreboardstats.commands.ReloadCommand;
import com.github.games647.scoreboardstats.commands.SidebarCommand;
import com.github.games647.scoreboardstats.listener.EntityListener;
import com.github.games647.scoreboardstats.listener.PlayerListener;
import com.github.games647.scoreboardstats.pvpstats.Database;
import static com.github.games647.scoreboardstats.pvpstats.Database.saveAll;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.github.games647.scoreboardstats.scoreboard.SbManager;
import com.github.games647.variables.Commands;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ScoreboardStats extends JavaPlugin {

    public final Set<String> hidelist = new HashSet<String>(10);

    private static ScoreboardStats instance;

    private int taskid;

    public static ScoreboardStats getInstance() {
        return instance;
    }

    public ScoreboardStats() {
        super();
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Settings.loadConfig();

        if (Settings.isUpdateInfo()) {
            new Updater(this, "scoreboardstats", getFile(), Updater.UpdateType.DEFAULT, true);
        }

        setupDatabase();

        com.github.games647.scoreboardstats.listener.PluginListener.init();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);

        getCommand(Commands.RELOAD_COMMAND) .setExecutor(new ReloadCommand());
        getCommand(Commands.HIDE_COMMAND)   .setExecutor(new DisableCommand());
        getCommand(Commands.SIDEBAR)        .setExecutor(new SidebarCommand());

        SbManager.regAll();

        taskid = getServer().getScheduler().scheduleSyncRepeatingTask(this,
                new RefreshTask(),
                Other.STARTUP_DELAY,
                Settings.getIntervall() * Other.TICKS_PER_SECOND - Other.HALF_SECOND_TICK);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }

    public void onReload() {
        final int     intervall     = Settings.getIntervall();
        final int     length        = Settings.getItemsLenght();
        final boolean pvpstats      = Settings.isPvpStats();

        Settings.loadConfig();

        if (intervall != Settings.getIntervall()) {
            getServer().getScheduler().cancelTask(taskid);
            getServer().getScheduler().scheduleSyncRepeatingTask(this,
                    new RefreshTask(), Other.STARTUP_DELAY,
                    Settings.getIntervall() * Other.TICKS_PER_SECOND - Other.HALF_SECOND_TICK);
        }

        if (length != Settings.getItemsLenght()) {
            SbManager.unregisterAll();
        }

        if (pvpstats != Settings.isPvpStats()) {
            instance.setupDatabase();
            SbManager.regAll();
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        saveAll();
        SbManager.unregisterAll();
    }

    private void setupDatabase() {
        if (Settings.isPvpStats()) {
            final ServerConfig db = new ServerConfig();

            db.setDefaultServer(false);
            db.setRegister(false);
            db.setClasses(getDatabaseClasses());
            db.setName(getDescription().getName());
            getServer().configureDbConfig(db);

            final DataSourceConfig ds = getSqlConfig(db.getDataSourceConfig());
            ds.setUrl(replaceDatabaseString(ds.getUrl()));

            final ClassLoader previous = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader(this.getClassLoader());
            final EbeanServer database = EbeanServerFactory.create(db);
            Thread.currentThread().setContextClassLoader(previous);

            try {
                database.find(PlayerStats.class).findRowCount();
            } catch (javax.persistence.PersistenceException ex) {
                getServer().getConsoleSender().sendMessage(Message.LOG_NAME + Message.NON_EXISTING_DATABASE);
                installDDL();
            }

            Database.setDatabase(database);
        }
    }

    private String replaceDatabaseString(String input) {
        input = input.replaceAll("\\{DIR\\}", getDataFolder().getPath().replaceAll("\\\\", "/") + "/");
        input = input.replaceAll("\\{NAME\\}", getDescription().getName().replaceAll("[^\\w_-]", ""));
        return input;
    }

    private DataSourceConfig getSqlConfig(DataSourceConfig config) {
        final File file = new File(getDataFolder(), "sql.yml");
        if (file.exists()) {
            final FileConfiguration sqlConfig = YamlConfiguration.loadConfiguration(file);
            config.setUsername(sqlConfig.getString("SQL-Settings.Username"));
            config.setPassword(sqlConfig.getString("SQL-Settings.Password"));
            config.setIsolationLevel(TransactionIsolation.getLevel(sqlConfig.getString("SQL-Settings.Isolation")));
            config.setDriver(sqlConfig.getString("SQL-Settings.Driver"));
            config.setUrl(sqlConfig.getString("SQL-Settings.Url"));
            config.setMinConnections(sqlConfig.getInt("SQL-Settings.MinConnections"));
            config.setMaxConnections(sqlConfig.getInt("SQL-Settings.MaxConnections"));
            config.setWaitTimeoutMillis(sqlConfig.getInt("SQL-Settings.Timeout"));
            config.setHeartbeatSql(sqlConfig.getString("SQL-Settings.HeartbeatSQL"));
        } else {
            saveResource("sql.yml", false);
            final FileConfiguration sqlConfig = YamlConfiguration.loadConfiguration(file);
            sqlConfig.set("SQL-Settings.Username", config.getUsername());
            sqlConfig.set("SQL-Settings.Password", config.getPassword());
            sqlConfig.set("SQL-Settings.Isolation", TransactionIsolation.getLevelDescription(config.getIsolationLevel()));
            sqlConfig.set("SQL-Settings.Driver", config.getDriver());
            sqlConfig.set("SQL-Settings.Url", config.getUrl());
            sqlConfig.set("SQL-Settings.Password", config.getPassword());
            try {
                sqlConfig.save(file);
            } catch (IOException ex) {
                getServer().getConsoleSender().sendMessage(Message.LOG_NAME + Message.FILE_EXCEPTION);
                ex.printStackTrace();
            }
        }

        return config;
    }
}
