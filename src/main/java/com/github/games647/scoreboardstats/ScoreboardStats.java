package com.github.games647.scoreboardstats;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.games647.scoreboardstats.listener.EntityListener;
import com.github.games647.scoreboardstats.listener.PlayerListener;
import com.github.games647.scoreboardstats.listener.SignListener;
import com.github.games647.scoreboardstats.protocol.PacketSbManager;
import com.github.games647.scoreboardstats.pvpstats.Database;

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

    private RefreshTask refreshTask;
    private Settings settings;
    private ReloadFixLoader classLoader;
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
    public ReloadFixLoader getClassLoaderBypass() {
        return classLoader;
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
        if (!this.isEnabled()) {
            return;
        }

        classLoader = new ReloadFixLoader(this, getClassLoader());
        settings = new Settings(this);
        refreshTask = new RefreshTask(this);

        //Load the config
        settings.loadConfig();

        if (Settings.isUpdateEnabled()) {
            new UpdaterFix(this, this.getFile(), true, new Updater.UpdateCallback() {

                @Override
                public void onFinish(Updater updater) {
                    if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
                        getLogger().info(Lang.get("onUpdate"));
                    }
                }
            });
        }

        Database.setupDatabase(this);

        //Register all events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        if (getServer().getPluginManager().isPluginEnabled("InSigns")) {
            //Register a this listerner if InSigns is available
            getServer().getPluginManager().registerEvents(new SignListener(), this);
        }

        //register all commands
        getCommand("sidebar").setExecutor(new SidebarCommands(this));

        //start tracking the ticks
        getServer().getScheduler().runTaskTimer(this, new TicksPerSecondTask(), 20L * 5, 20L);
        //Start the refresh task
        getServer().getScheduler().runTaskTimer(this, refreshTask, 20L * 5, 1L);

        if (Settings.isCompatibilityMode()) {
            scoreboardManager = new PacketSbManager(this);
        } else {
            scoreboardManager = new SbManager(this);
        }

        scoreboardManager.registerAll();

        checkCompatibility();
    }

    /**
     * Load the plugin.
     */
    @Override
    public void onLoad() {
        //Create a logger that is available by just the plugin name
        Logger.getLogger("ScoreboardStats").setParent(getLogger());

        //Check if server can display scoreboards
        checkScoreboardCompatibility();
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

        Database.saveAll();

        if (Settings.isCompatibilityMode()) {
            ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        }
    }

    /**
     * Reload the plugin
     */
    public void onReload() {
        final boolean oldMode = Settings.isCompatibilityMode();

        settings.loadConfig();

        Database.setupDatabase(this);

        if (refreshTask != null) {
            refreshTask.clear();
        }

        if (scoreboardManager != null) {
            scoreboardManager.unregisterAll();
        }

        if (oldMode != Settings.isCompatibilityMode()) {
            if (Settings.isCompatibilityMode()) {
                scoreboardManager = new PacketSbManager(this);
            } else {
                scoreboardManager = new SbManager(this);
            }
        }

        if (scoreboardManager != null) {
            scoreboardManager.registerAll();
        }
    }

    private void checkScoreboardCompatibility() {
        final int compare = Version.compare("1.5", Version.getMinecraftVersionString());
        if (compare <= 0) {
            //The minecraft version is higher or equal the minimum scoreboard version
            return;
        }

        getLogger().warning(Lang.get("noCompatibleVersion"));
        //This plugin isn't compatible with the server version so we disabling it
        getPluginLoader().disablePlugin(this);
    }

    private void checkCompatibility() {
        //Inform the user that he should use compatibility modus to be compatible with some plugins
        if (Settings.isCompatibilityMode()) {
            getLogger().info("The plugin will now use raw packets to be compatible with other scoreboard plugins");
        } else {
            final String[] plugins = {"HealthBar", "ColoredTags"};
            boolean found = false;
            for (String name : plugins) {
                if (getServer().getPluginManager().getPlugin(name) != null) {
                    found = true;
                    break;
                }
            }

            if (found) {
                getLogger().info("You are using plugins that with your configuration not compatible with scoreboardstats");
                getLogger().info("Please enable in your configuration the 'compatibilityMode'");
                getLogger().info("If you do so, this plugin operate over raw packets. All your plugins will still be compatible with scoreboards");
            }
        }
    }
}
