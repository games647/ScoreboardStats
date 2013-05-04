package com.github.games647.scoreboardstats;

import java.util.List;
import com.github.games647.scoreboardstats.scoreboard.Score;
import com.github.games647.scoreboardstats.scoreboard.VariableReplacer;
import static com.github.games647.scoreboardstats.pvpstats.Database.saveAll;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;

public final class ScoreboardStats extends org.bukkit.plugin.java.JavaPlugin {

    private static SettingsHandler settings;
    private static ScoreboardStats instance;
    private static VariableReplacer replacer;

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
        Score.regAll();
        com.github.games647.scoreboardstats.listener.PluginListener.init();
        this.getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.PlayerListener(), this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new com.github.games647.scoreboardstats.UpdateThread(), 60L, settings.getIntervall() * 20L);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new java.util.ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        saveAll();
        Score.unregisterAll();
    }

    private void setupDatabase() {
        if (!settings.isPvpstats()) {
            return;
        }

        try {
            getDatabase().find(PlayerStats.class).findRowCount();
        }
        catch (javax.persistence.PersistenceException ex) {
            getLogger().info("Can't find an existing Database, so creating a new one");
            installDDL();
        }

        com.github.games647.scoreboardstats.pvpstats.Database.setDatabase(getDatabase());
        this.getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.EntityListener(), this);
    }
}
