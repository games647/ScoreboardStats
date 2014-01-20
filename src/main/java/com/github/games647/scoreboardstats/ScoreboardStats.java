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
            getLogger().info(Language.get("onUpdate"));
        }

        Settings.loadConfig();
        Database.setupDatabase(this);

        //Register all events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);

        if (getServer().getPluginManager().isPluginEnabled("InSigns")) {
            getServer().getPluginManager().registerEvents(new SignsListener(), this);
        }

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
    public void onLoad() {
        super.onLoad();

        //Check if server can display scoreboards
        isScoreboardCompatible();
    }

    public void onReload() {
        final int intervall = Settings.getIntervall();
        final boolean pvpstats = Settings.isPvpStats();

        Settings.loadConfig();
        if (intervall != Settings.getIntervall()) {
            getServer().getScheduler().cancelTask(refreshTask);
            getServer().getScheduler().scheduleSyncRepeatingTask(this,
                    new RefreshTask(this), 60L,
                    Settings.getIntervall() * 20L);
        }

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
        return super.getClassLoader();
    }

    //todo cath exception
    private boolean isScoreboardCompatible() throws NumberFormatException {
        final String bukkitVersion = getServer().getBukkitVersion();
        final int version = Integer.parseInt(bukkitVersion.split("\\-")[0].replace(".", ""));
        if (version >= 150) {
            return true;
        }

        getLogger().warning(Language.get("noCompatibleVersion"));
        getPluginLoader().disablePlugin(this);
        return false;
    }
}
