package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.listener.EntityListener;
import com.github.games647.scoreboardstats.listener.PlayerListener;
import com.github.games647.scoreboardstats.listener.SignsListener;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.google.common.collect.Sets;

import java.io.File;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardStats extends JavaPlugin {

    private static ScoreboardStats instance;

    public static ScoreboardStats getInstance() {
        return instance;
    }

    private final Set<String> hidelist = Sets.newHashSet();

    private final TicksPerSecondTask tpsTask = new TicksPerSecondTask();
    private RefreshTask refreshTask;
    private SbManager scoreboardManager;
    private Settings settings;

    public ScoreboardStats() {
        super();

        instance = this;
    }

    public SbManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Override
    public void onEnable() {
        //Load the config and setting the database up
        if (settings == null) {
            settings = new Settings(this);
        }

        Updater updater = null;
        if (Settings.isUpdateEnabled()) {
            updater = new UpdaterFix(this, getFile());
        }

        settings.loadConfig();
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

        //Start the refresh task
        refreshTask = new RefreshTask(this);
        getServer().getScheduler().runTaskTimer(this, refreshTask
                , 60L, 1L);
        getServer().getScheduler().runTaskTimer(this, tpsTask, 60L, 20L);

        if (scoreboardManager == null) {
            scoreboardManager = new SbManager(this);
        }

        scoreboardManager.registerAll();

        if (updater != null && updater.getResult() == Updater.UpdateResult.SUCCESS) {
            //the updater run async so don't block it this method
            //Check if a new update is available
            getLogger().info(Lang.get("onUpdate"));
        }
    }

    @Override
    public void onLoad() {
        //Check if server can display scoreboards
        isScoreboardCompatible();
    }

    @Override
    public void onDisable() {
        //Clear all scoreboards and copyDefault them
        if (scoreboardManager != null) {
           scoreboardManager.unregisterAll();
        }

        Database.saveAll();
    }

    public void onReload() {
        final boolean pvpstats = Settings.isPvpStats();

        settings.loadConfig();

        if (pvpstats != Settings.isPvpStats()) {
            Database.setupDatabase(this);
        }

        scoreboardManager.unregisterAll();
        scoreboardManager.registerAll();
    }

    public Set<String> getHidelist() {
        return hidelist;
    }

    public File getFileBypass() {
        return super.getFile();
    }

    public ClassLoader getClassLoaderBypass() {
        return super.getClassLoader();
    }

    public RefreshTask getRefreshTask() {
        return refreshTask;
    }

    private boolean isScoreboardCompatible() {
        final String minecraftVersion = getServer().getVersion().split("MC: ")[1].split("\\)")[0];
        //Convert the version string into an integer
        final int version = Integer.parseInt(minecraftVersion.replace(".", ""));
        //Only version above 1.5 supports the scoreboard feature
        if (version >= 150) {
            return true;
        }

        getLogger().warning(Lang.get("noCompatibleVersion"));
        //This plugin isn't compatible with the server version so we disabling it
        getPluginLoader().disablePlugin(this);
        return false;
    }
}
