package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.commands.SidebarCommands;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.scoreboard.protocol.PacketSbManager;
import com.github.games647.scoreboardstats.variables.ReplaceManager;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.JDK14LoggerAdapter;

/**
 * Represents the main class of this plugin.
 */
public class ScoreboardStats extends JavaPlugin {

    private final Logger logger = createLoggerFromJDK(getLogger());

    //don't create instances here that accesses the bukkit API - it will be incompatible with older mc versions
    private RefreshTask refreshTask;
    private Settings settings;
    private SbManager scoreboardManager;
    private Database database;

    private ReplaceManager replaceManager;

    /**
     * Get the scoreboard manager.
     *
     * @return the manager
     */
    public SbManager getScoreboardManager() {
        return scoreboardManager;
    }

    /**
     * Get the replace manager.
     *
     * @return the manager
     */
    public ReplaceManager getReplaceManager() {
        return replaceManager;
    }

    /**
     * Get the refresh task for updating the scoreboard
     *
     * @return the refresh task instance
     */
    public RefreshTask getRefreshTask() {
        return refreshTask;
    }

    /**
     * The database manager for pvp stats
     *
     * @return pvp stats database manager
     */
    public Database getStatsDatabase() {
        return database;
    }

    public Logger getLog() {
        return logger;
    }

    /**
     * Enable the plugin
     */
    @Override
    public void onEnable() {
        //Load the config + needs to be initialised to get the configured value for update-checking
        settings = new Settings(this);
        settings.loadConfig();

        refreshTask = new RefreshTask(this);

        //Register all events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        //register all commands based on the root command of this plugin
        getCommand(getName().toLowerCase()).setExecutor(new SidebarCommands(this));

        //start tracking the ticks
        getServer().getScheduler().runTaskTimer(this, new TicksPerSecondTask(), 5 * 20L, 3 * 20L);
        //Start the refresh task; it should run on every tick, because it's smoothly update the variables with limit
        getServer().getScheduler().runTaskTimer(this, refreshTask, 5 * 20L, 1L);

        scoreboardManager = new PacketSbManager(this);
        replaceManager = new ReplaceManager(scoreboardManager, this);

        if (Settings.isPvpStats()) {
            database = new Database(this);
            database.setupDatabase();
        }

        //creates scoreboards for every player that is online
        scoreboardManager.registerAll();
    }

    /**
     * Disable the plugin
     */
    @Override
    public void onDisable() {
        if (scoreboardManager != null) {
            //Clear all scoreboards
            scoreboardManager.unregisterAll();
        }

        if (database != null) {
            //flush the cache to the database
            database.saveAll();
        }
    }

    /**
     * Reload the plugin
     */
    public void onReload() {
        if (settings != null) {
            settings.loadConfig();
        }

        if (refreshTask != null) {
            refreshTask.clear();
        }

        if (scoreboardManager != null) {
            scoreboardManager.unregisterAll();
        }

        scoreboardManager = new PacketSbManager(this);
        if (database == null) {
            database = new Database(this);
            database.setupDatabase();
        } else {
            database.setupDatabase();
        }

        scoreboardManager.registerAll();
    }

    private static Logger createLoggerFromJDK(java.util.logging.Logger parent) {
        try {
            Class<JDK14LoggerAdapter> adapterClass = JDK14LoggerAdapter.class;
            Constructor<JDK14LoggerAdapter> cons = adapterClass.getDeclaredConstructor(java.util.logging.Logger.class);
            cons.setAccessible(true);
            return cons.newInstance(parent);
        } catch (ReflectiveOperationException reflectEx) {
            parent.log(Level.WARNING, "Cannot create slf4j logging adapter", reflectEx);
            parent.log(Level.WARNING, "Creating logger instance manually...");
            return LoggerFactory.getLogger(parent.getName());
        }
    }
}
