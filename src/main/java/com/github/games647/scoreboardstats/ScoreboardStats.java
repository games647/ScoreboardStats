package com.github.games647.scoreboardstats;

import static com.github.games647.scoreboardstats.pvpstats.Database.saveAll;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.github.games647.scoreboardstats.scoreboard.SbManager;
import com.github.games647.variables.Other;
import java.util.List;

public final class ScoreboardStats extends org.bukkit.plugin.java.JavaPlugin {

    private static SettingsHandler settings;
    private static ScoreboardStats instance;

    public static SettingsHandler getSettings() {
        return settings;
    }

    public static ScoreboardStats getInstance() {
        return instance;
    }

    public ScoreboardStats() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        settings = new SettingsHandler();
        setupDatabase();
        SbManager.regAll();
        com.github.games647.scoreboardstats.listener.PluginListener.init();
        getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.PlayerListener(), this);
        getServer().getScheduler()
                .scheduleSyncRepeatingTask(this
                        , new com.github.games647.scoreboardstats.UpdateThread(), Other.STARTUP_DELAY, settings.getIntervall() * Other.TICKS_PER_SECOND);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new java.util.ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }

    public static void onReload() {
        SbManager.unregisterAll();
        saveAll();
        settings.loadConfig();
        SbManager.regAll();
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        saveAll();
        SbManager.unregisterAll();
    }

    private void setupDatabase() {
        if (settings.isPvpStats()) {
            final com.avaje.ebean.EbeanServer database = getDatabase();

            try {
                database.find(PlayerStats.class).findRowCount();
            } catch (javax.persistence.PersistenceException ex) {
                getLogger().info("Can't find an existing Database, so creating a new one");
                installDDL();
            }

            com.github.games647.scoreboardstats.pvpstats.Database.setDatabase(database);
            getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.EntityListener(), this);
        }
    }
}
