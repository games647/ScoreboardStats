package com.github.games647.scoreboardstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.github.games647.scoreboardstats.commands.DisableCommand;
import com.github.games647.scoreboardstats.commands.ReloadCommand;
import com.github.games647.scoreboardstats.commands.SidebarCommand;
import com.github.games647.scoreboardstats.listener.EntityListener;
import com.github.games647.scoreboardstats.listener.PlayerListener;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

public class ScoreboardStats extends JavaPlugin {

    private static ScoreboardStats instance;

    public static ScoreboardStats getInstance() {
        return instance;
    }

    private final Set<String> hidelist = Sets.newHashSet();
    private SbManager scoreboardManager;

    private int refreshTask;

    public ScoreboardStats() {
        super();

        instance = this;
    }

    public SbManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        final Updater updater = new Updater(this, 55148, getFile(), Updater.UpdateType.DEFAULT, false);
        if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
            getLogger().info("A new update is avaible and will be install after a reload or restart");
            getLogger().info("Thanks to Gravity for his great work");
        }

        Settings.loadConfig();
        setupDatabase();

        //Register all events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);

        getCommand("sb:reload").setExecutor(new ReloadCommand());
        getCommand("sb:toggle").setExecutor(new DisableCommand());
        getCommand("sidebar").setExecutor(new SidebarCommand());

        scoreboardManager = new SbManager();
        scoreboardManager.regAll();

        refreshTask = getServer().getScheduler().scheduleSyncRepeatingTask(this,
                new RefreshTask(this),
                60L,
                Settings.getIntervall() * 20L);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        isScoreboardCompatible(); //Check if server can display scoreboards
    }

    public void onReload() {
        final int intervall = Settings.getIntervall();
        final int length = Settings.getItemsLenght();
        final boolean pvpstats = Settings.isPvpStats();

        Settings.loadConfig();
        if (intervall != Settings.getIntervall()) {
            getServer().getScheduler().cancelTask(refreshTask);
            getServer().getScheduler().scheduleSyncRepeatingTask(this,
                    new RefreshTask(this), 60L,
                    Settings.getIntervall() * 20L);
        }

        if (length != Settings.getItemsLenght()) {
            scoreboardManager.unregisterAll();
        }

        if (pvpstats != Settings.isPvpStats()) {
            instance.setupDatabase();
            scoreboardManager.regAll();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        getServer().getScheduler().cancelTasks(this); //Remove all running tasks
        HandlerList.unregisterAll(this); //Remove all listeners
        Database.saveAll();
        scoreboardManager.unregisterAll(); //Clear all scoreboards
    }

    public Set<String> getHidelist() {
        return hidelist;
    }

    private void setupDatabase() {
        if (Settings.isPvpStats()) {
            final ServerConfig databaseConfig = new ServerConfig();
            databaseConfig.setRegister(false);
            databaseConfig.setClasses(getDatabaseClasses());
            databaseConfig.setName(getName());

            final DataSourceConfig ds = getSqlConfig(databaseConfig);
            ds.setUrl(replaceUrlString(ds.getUrl()));

            final ClassLoader previous = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader(getClassLoader());
            final EbeanServer database = EbeanServerFactory.create(databaseConfig);
            Thread.currentThread().setContextClassLoader(previous);

            try {
                database.find(PlayerStats.class).findRowCount();
            } catch (PersistenceException ex) {
                getLogger().log(Level.INFO, "{0}" + "Can't find an existing Database, so creating a new one" + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.YELLOW));

                final DdlGenerator gen = ((SpiEbeanServer) database).getDdlGenerator();
                gen.runScript(false, gen.generateCreateDdl());
            }

            Database.setDatabaseInstance(database);
        }
    }

    private String replaceUrlString(String input) {
        final Pattern pat = Pattern.compile("\\\\");
        return input
                .replace("{DIR}", pat.matcher(getDataFolder().getPath()).replaceAll("/") + '/')
                .replace("{NAME}", getName());
    }

    private DataSourceConfig getSqlConfig(ServerConfig serverConfig) {
        final File file = new File(getDataFolder(), "sql.yml");
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
            saveResource("sql.yml", false);

            getServer().configureDbConfig(serverConfig);
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
                getLogger().log(Level.WARNING, "{0}" + "Error while trying to save the sql.yml" + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
                getLogger().throwing(getClass().getName(), "getSqlConfig", ex);
            }
        }

        config.setMinConnections(sqlConfig.getInt("SQL-Settings.MinConnections"));
        config.setMaxConnections(sqlConfig.getInt("SQL-Settings.MaxConnections"));
        config.setWaitTimeoutMillis(sqlConfig.getInt("SQL-Settings.Timeout"));

        return config;
    }

    private boolean isScoreboardCompatible() throws NumberFormatException { //ToDo cath exception
        final String bukkitVersion = getServer().getBukkitVersion();
        final int version = Integer.parseInt(bukkitVersion.split("\\-")[0].replace(".", ""));
        if (version >= 150) {
            return true;
        }

        getLogger().warning("Scoreboards are only supported in 1.5 or above");
        getPluginLoader().disablePlugin(this);
        return false;
    }
}
