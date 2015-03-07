package com.github.games647.scoreboardstats;

import com.avaje.ebean.EbeanServer;
import com.comphenix.protocol.ProtocolLibrary;
import com.github.games647.scoreboardstats.Updater.UpdateCallback;
import com.github.games647.scoreboardstats.Updater.UpdateResult;
import com.github.games647.scoreboardstats.listener.EntityListener;
import com.github.games647.scoreboardstats.listener.PlayerListener;
import com.github.games647.scoreboardstats.listener.SignListener;
import com.github.games647.scoreboardstats.protocol.PacketSbManager;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents the main class of this plugin.
 *
 * Take a look here to see newest source files and contribute to the project
 * https://github.com/games647/ScoreboardStats
 */
public class ScoreboardStats extends JavaPlugin {

    //don't create instances here that accesses the bukkit API
    private RefreshTask refreshTask;
    private Settings settings;
    private ReloadFixLoader classLoader;
    private SbManager scoreboardManager;

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
     * Get the replace manager.
     *
     * @return the manager
     */
    public ReplaceManager getReplaceManager() {
        if (scoreboardManager != null) {
            scoreboardManager.getReplaceManager();
        }

        return null;
    }

    /**
     * Get the refresh task for updating the scoreboard
     *
     * @return the refresh task instance
     */
    public RefreshTask getRefreshTask() {
        return refreshTask;
    }

    @Override
    public EbeanServer getDatabase() {
        //these method exists to make it easier access from another plugin
        return Database.getDatabaseInstance();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> classes = Lists.newArrayList();
        classes.add(PlayerStats.class);
        return classes;
    }

    /**
     * Load the plugin.
     */
    @Override
    public void onLoad() {
        //Create a logger that is available by just the plugin name
        //have to be peformed before the first logging message by this plugin, so it prints it correctly
        Logger.getLogger("ScoreboardStats").setParent(getLogger());

        //Check if server can display scoreboards; the version can only be with a complete shutdown
        checkScoreboardCompatibility();
    }

    /**
     * Enable the plugin
     */
    @Override
    public void onEnable() {
        if (!this.isEnabled()) {
            //cancel initialization if the already disabled it
            return;
        }

        //this is needed by settings (for localized messages)
        classLoader = new ReloadFixLoader(this, getClassLoader());

        //Load the config + needs to be initialised to get the configurated value for update-checking
        settings = new Settings(this);
        settings.loadConfig();

        if (Settings.isUpdateEnabled()) {
            //start this as early as possible, so it can run async in the background
            new UpdaterFix(this, this.getFile(), true, new UpdateCallback() {

                @Override
                public void onFinish(Updater updater) {
                    //This method will be performed on the main thread after the
                    //update check finished so this won't block the main thread
                    if (updater.getResult() == UpdateResult.SUCCESS) {
                        getLogger().info(Lang.get("onUpdate"));
                    }
                }
            });
        }

        refreshTask = new RefreshTask(this);
        Database.setupDatabase(this);

        //Register all events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        if (getServer().getPluginManager().isPluginEnabled("InSigns")) {
            //Register this listerner if InSigns is available
            new SignListener(this, "[Kill]");
            new SignListener(this, "[Death]");
            new SignListener(this, "[KDR]");
            new SignListener(this, "[Streak]");
            new SignListener(this, "[Mob]");
        }

        //register all commands
        getCommand("sidebar").setExecutor(new SidebarCommands(this));

        //start tracking the ticks
        getServer().getScheduler().runTaskTimer(this, new TicksPerSecondTask(), 20L * 5, 20L);
        //Start the refresh task; it should run on every tick, because it's smoothly update the variables with limit
        getServer().getScheduler().runTaskTimer(this, refreshTask, 20L * 5, 1L);

        if (Settings.isCompatibilityMode()) {
            scoreboardManager = new PacketSbManager(this);
        } else {
            scoreboardManager = new BukkitScoreboardManager(this);
        }

        //creates scoreboards for every player that is online
        scoreboardManager.registerAll();

        //Inform the user that he should use compatibility modus to be compatible with some plugins
        checkCompatibility();
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

        //flush the cache to the database
        Database.saveAll();

        if (Settings.isCompatibilityMode()) {
            //the plugin will be disabled unregister all listeners including protocollibs
            ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        }
    }

    /**
     * Reload the plugin
     */
    public void onReload() {
        final boolean oldMode = Settings.isCompatibilityMode();

        if (settings != null) {
            settings.loadConfig();
        }

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
                scoreboardManager = new BukkitScoreboardManager(this);
            }
        }

        if (scoreboardManager != null) {
            scoreboardManager.registerAll();
        }
    }

    private void checkScoreboardCompatibility() {
        //Scoreboards are introduced in minecraft 1.5
        final int compare = Version.compare("1.5", Version.getMinecraftVersionString());
        if (compare >= 0) {
            //The minecraft version is higher or equal the minimum scoreboard version
            return;
        }

        getLogger().warning(Lang.get("noCompatibleVersion"));
        //This plugin isn't compatible with the server version so we disabling it
        getPluginLoader().disablePlugin(this);
    }

    private void checkCompatibility() {
        if (!Settings.isCompatibilityMode()) {
            //Thise plugins won't work without compatibilityMode, but do with it, so suggest it
            final String[] plugins = {"HealthBar", "ColoredTags", "McCombatLevel", "Ghost_Player"};
            for (String name : plugins) {
                //just check if the plugin is available not if it's active
                if (getServer().getPluginManager().getPlugin(name) != null) {
                    //Found minimum one plugin. Inform the adminstrator
                    getLogger().info("You are using plugins that requires to activate compatibilityMode");
                    getLogger().info("Otherwise the plugins won't work");
                    getLogger().info("Please enable it in the ScoreboardStats configuration 'compatibilityMode'");
                    getLogger().info("Then this plugin will send raw packets, but will be still compatible other plugins");
                    break;
                }
            }
        }
    }
}
