package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.commands.DisableCommand;
import com.github.games647.scoreboardstats.commands.ReloadCommand;
import com.github.games647.scoreboardstats.commands.SidebarCommand;
import com.github.games647.scoreboardstats.listener.EntityListener;
import com.github.games647.scoreboardstats.listener.PlayerListener;
import com.github.games647.scoreboardstats.listener.SignsListener;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.google.common.collect.Sets;

import java.util.Set;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardStats extends JavaPlugin {

    private static ScoreboardStats instance;

    public static ScoreboardStats getInstance() {
        return instance;
    }

    private final Set<String> hidelist = Sets.newHashSet();

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
        super.onEnable();

        //Load the config and setting the database up
        if (settings == null) {
            settings = new Settings(this);
        }

        settings.loadConfig();
        Database.setupDatabase(this);

        //Register all events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        if (getServer().getPluginManager().isPluginEnabled("InSigns")) {
            //Register a this listerner if InSigns is availble
            getServer().getPluginManager().registerEvents(new SignsListener(), this);
        }

        //register all commands
        getCommand("sb:toggle").setExecutor(new DisableCommand(this));
        getCommand("sb:reload").setExecutor(new ReloadCommand());
        getCommand("sidebar").setExecutor(new SidebarCommand());

        if (scoreboardManager == null) {
            scoreboardManager = new SbManager(this);
        }

        scoreboardManager.regAll();

        //Start the refresh task
        refreshTask = new RefreshTask(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, refreshTask
                , 60L, 1L);

        if (Settings.isUpdateEnabled()) {
            final Updater updater = new UpdaterFix(this, 55148, getFile());
            if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
                //Check if a new update is available
                getLogger().info(Language.get("onUpdate"));
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        Language.clearCache();

        //Check if server can display scoreboards
        isScoreboardCompatible();
    }

    public void onReload() {
        final boolean pvpstats = Settings.isPvpStats();

        settings.loadConfig();

        if (pvpstats != Settings.isPvpStats()) {
            Database.setupDatabase(this);
        }

        scoreboardManager.unregisterAll();
        scoreboardManager.regAll();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        //Remove all running tasks
        getServer().getScheduler().cancelTasks(this);
        //Remove all listeners
        HandlerList.unregisterAll(this);
        //Clear all scoreboards and save them
        scoreboardManager.unregisterAll();
        Database.saveAll();
    }

    public Set<String> getHidelist() {
        return hidelist;
    }

    public ClassLoader getClassLoaderBypass() {
        //make the access to the class loader public because we need it for setting the database up
        return super.getClassLoader();
    }

    public RefreshTask getRefreshTask() {
        return refreshTask;
    }

    private boolean isScoreboardCompatible() {
        final String bukkitVersion = getServer().getBukkitVersion();
        //Convert the version string into and integer
        final int version = Integer.parseInt(bukkitVersion.split("\\-")[0].replace(".", ""));
        //Only version above 1.5 supports the scoreboard feature
        if (version >= 150) {
            return true;
        }

        getLogger().warning(Language.get("noCompatibleVersion"));
        //This plugin isn't compatible with the server version so we disabling it
        getPluginLoader().disablePlugin(this);
        return false;
    }
}
