package com.github.games647.scoreboardstats;

import static com.github.games647.scoreboardstats.pvpstats.Database.saveAll;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.github.games647.scoreboardstats.scoreboard.ScoreboardManager;
import com.github.games647.variables.VariableList;
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

    @Override
    public void onEnable() {
        instance = this;
        settings = new SettingsHandler(this);
        setupDatabase();
        ScoreboardManager.regAll();
        com.github.games647.scoreboardstats.listener.PluginListener.init();
        this.getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.PlayerListener(), this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new com.github.games647.scoreboardstats.UpdateThread(), VariableList.STARTUP_DELAY, settings.getIntervall() * VariableList.TICKS_PER_SECOND);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new java.util.ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }

    public static void onReload() {
        ScoreboardManager.unregisterAll();
        saveAll();
        settings.loadConfig();
        ScoreboardManager.regAll();
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        saveAll();
        ScoreboardManager.unregisterAll();
    }

    private void setupDatabase() {
        final com.avaje.ebean.EbeanServer database = getDatabase();

        if (!settings.isPvpStats()) {
            return;
        }

        try {
            database.find(PlayerStats.class).findRowCount();
        } catch (javax.persistence.PersistenceException ex) {
            getLogger().info("Can't find an existing Database, so creating a new one");
            installDDL();
        }

        com.github.games647.scoreboardstats.pvpstats.Database.setDatabase(database);
        this.getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.EntityListener(), this);
    }
}
