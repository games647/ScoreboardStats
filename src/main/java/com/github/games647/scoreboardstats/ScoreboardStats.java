package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.commands.SidebarCommands;
import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.scoreboard.bukkit.BukkitScoreboardManager;
import com.github.games647.scoreboardstats.scoreboard.protocol.PacketSbManager;
import com.github.games647.scoreboardstats.variables.ReplaceManager;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents the main class of this plugin.
 */
public class ScoreboardStats extends JavaPlugin {

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

    /**
     * Load the plugin.
     */
    @Override
    public void onLoad() {
        //Create a logger that is available by just the plugin name
        //have to be performed before the first logging message by this plugin, so it prints it correctly
        Logger.getLogger(getName()).setParent(getLogger());
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

        if (Settings.isCompatibilityMode()) {
            scoreboardManager = new PacketSbManager(this);
        } else {
            scoreboardManager = new BukkitScoreboardManager(this);
        }

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

        if (Settings.isCompatibilityMode()) {
            scoreboardManager = new PacketSbManager(this);
        } else {
            scoreboardManager = new BukkitScoreboardManager(this);
        }

        if (database == null) {
            database = new Database(this);
            database.setupDatabase();
        } else {
            database.setupDatabase();
        }

        scoreboardManager.registerAll();
    }
}
