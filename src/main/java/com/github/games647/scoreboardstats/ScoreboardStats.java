package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.listener.EntityListener;
import com.github.games647.scoreboardstats.listener.PlayerListener;
import com.github.games647.scoreboardstats.listener.SignsListener;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.variables.ReplaceManager;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents the main class of this plugin.
 */
public class ScoreboardStats extends JavaPlugin {

    private static ScoreboardStats instance;

    /**
     * Get the current instance of scoreboardstats. This makes the instance
     * easier available for other plugins and makes it useless useless to keep
     * EVERY time the reference.
     *
     * @return the current instance of scoreboardstats or null
     */
    public static ScoreboardStats getInstance() {
        return instance;
    }

    private final RefreshTask refreshTask = new RefreshTask(this);
    private final Settings settings = new Settings(this);
    private SbManager scoreboardManager;

    /**
     * Create a new plugin instance
     */
    public ScoreboardStats() {
        super();

        instance = this;
    }

    /**
     * Get the class loader for this plugin. This is a workaround to make
     * it available for other classes, because getClassLoader is as default
     * protected.
     *
     * @return the class loader for this plugin
     */
    public ClassLoader getClassLoaderBypass() {
        return super.getClassLoader();
    }

    /**
     * Get the scoreboard manager.
     *
     * @return the manager
     */
    public SbManager getScoreboardManager() {
        return scoreboardManager;
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
     * Enable the plugin
     */
    @Override
    public void onEnable() {
        super.onEnable();

        //Load the config
        settings.loadConfig();

        Updater updater = null;
        if (Settings.isUpdateEnabled()) {
            //Run the update process early so it can run in the backround as seperate thread
            updater = new UpdaterFix(this, getFile());
        }

        Database.setupDatabase(this);

        //Register all events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        if (getServer().getPluginManager().isPluginEnabled("InSigns")) {
            //Register a this listerner if InSigns is available
            getServer().getPluginManager().registerEvents(new SignsListener(), this);
        }

        //register all commands
        getCommand("sidebar").setExecutor(new SidebarCommands(this));

        //start tracking the ticks
        getServer().getScheduler().runTaskTimer(this, new TicksPerSecondTask(), 20L * 5, 20L);
        //Start the refresh task
        getServer().getScheduler().runTaskTimer(this, refreshTask, 20L * 5, 1L);

//        if (Settings.isCompatibilityMode()) {
//            scoreboardManager = new PacketSbManager(this);
//        } else {
            scoreboardManager = new SbManager(this);
//        }

        scoreboardManager.registerAll();

//        checkCompatibility();

        if (updater != null && Updater.UpdateResult.SUCCESS == updater.getResult()) {
            //the updater run async so don't block it this method
            //Check if a new update is available
            getLogger().info(Lang.get("onUpdate"));
        }
    }

    /**
     * Load the plugin.
     */
    @Override
    public void onLoad() {
        super.onLoad();

        //Create a logger that is available by just the plugin name
        Logger.getLogger("ScoreboardStats").setParent(getLogger());

        //Check if server can display scoreboards
        isScoreboardCompatible();
    }

    /**
     * Disable the plugin
     */
    @Override
    public void onDisable() {
        super.onDisable();

        if (scoreboardManager != null) {
            //Clear all scoreboards
            scoreboardManager.unregisterAll();
        }

        Database.saveAll();
    }

    /**
     * Reload the plugin
     */
    public void onReload() {
        final boolean pvpstats = Settings.isPvpStats();

        settings.loadConfig();

        if (pvpstats != Settings.isPvpStats()) {
            Database.setupDatabase(this);
        }

        if (refreshTask != null) {
            refreshTask.clear();
        }

        if (scoreboardManager != null) {
            scoreboardManager.unregisterAll();
            scoreboardManager.registerAll();
        }
    }

    /**
     * Gets the minecraft version as string. So for example it will return 1.7.5
     *
     * @return the minecraft version
     */
    public String getMinecraftVersion() {
        return getServer().getVersion().split("MC: ")[1].split("\\)")[0];
    }

    /**
     * Get the plugin file for this plugin. This is a workaround to make
     * it available for other classes, because getFile is as default
     * protected.
     *
     * @return the plugin file
     */
    protected File getFileBypass() {
        return super.getFile();
    }

    private boolean isScoreboardCompatible() {
        final int compare = ReplaceManager.compare("1.5.0", getMinecraftVersion());
        if (compare >= 0) {
            //The minecraft version is higher or equal the minimum scoreboard version
            return true;
        }

        getLogger().warning(Lang.get("noCompatibleVersion"));
        //This plugin isn't compatible with the server version so we disabling it
        getPluginLoader().disablePlugin(this);
        return false;
    }

//    private void checkCompatibility() {
//        //Inform the user that he should use compatibility modus to be compatible with some plugins
//        if (Settings.isCompatibilityMode()) {
//            getLogger().info("The plugin will now use raw packets to be compatible with other scoreboard plugins");
//        } else {
//            final String[] plugins = new String[] {"HealthBar", "ColoredTags"};
//            boolean found = false;
//            for (String name : plugins) {
//                if (getServer().getPluginManager().getPlugin(name) != null) {
//                    found = true;
//                    break;
//                }
//            }
//
//            if (found) {
//                getLogger().info("You are using plugins that with your configuration not compatible with scoreboardstats");
//                getLogger().info("Please enable in your configuration the 'compatibilityMode'");
//                getLogger().info("If you do so, this plugin operate over raw packets. All your plugins will still be compatible with scoreboards");
//            }
//        }
//    }
}
