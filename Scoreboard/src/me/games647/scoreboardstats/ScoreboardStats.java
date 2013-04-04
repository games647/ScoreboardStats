package me.games647.scoreboardstats;

import java.util.List;
import me.games647.scoreboardstats.api.PlayerStats;
import me.games647.scoreboardstats.settings.SettingsHandler;

public final class ScoreboardStats extends org.bukkit.plugin.java.JavaPlugin {

    private static SettingsHandler settings;

    public static SettingsHandler getSettings() {
        return settings;
    }

    @Override
    public void onEnable() {
        settings = new SettingsHandler(this);
        setupDatabase();
        this.getServer().getPluginManager().registerEvents(new me.games647.scoreboardstats.listener.PlayerListener(), this);
    }

    private void setupDatabase() {
        try {
            getDatabase().find(PlayerStats.class).findRowCount();
        } catch (javax.persistence.PersistenceException ex) {
            getLogger().info("Can't find an existing Database, so creating a new one");
            installDDL();
        }
        me.games647.scoreboardstats.api.Database.setDatabase(getDatabase());
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        final List<Class<?>> list = new java.util.ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }
}
