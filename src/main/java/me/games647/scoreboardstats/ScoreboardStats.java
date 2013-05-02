package me.games647.scoreboardstats;

import java.util.List;
import me.games647.scoreboardstats.api.Score;
import me.games647.scoreboardstats.api.VariableReplacer;
import static me.games647.scoreboardstats.api.pvpstats.Database.saveAll;
import me.games647.scoreboardstats.api.pvpstats.PlayerStats;

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
        me.games647.scoreboardstats.listener.PluginListener.init();
        this.getServer().getPluginManager().registerEvents(new me.games647.scoreboardstats.listener.PlayerListener(), this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new me.games647.scoreboardstats.api.UpdateThread(), 60L, settings.getIntervall() * 20L);
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

        me.games647.scoreboardstats.api.pvpstats.Database.setDatabase(getDatabase());
        this.getServer().getPluginManager().registerEvents(new me.games647.scoreboardstats.listener.EntityListener(), this);
    }
}
