package me.games647.scoreboardstats;

import java.util.List;
import me.games647.scoreboardstats.api.Score;
import me.games647.scoreboardstats.api.pvpstats.Database;
import static me.games647.scoreboardstats.api.pvpstats.Database.saveAll;
import me.games647.scoreboardstats.api.pvpstats.PlayerStats;
import me.games647.scoreboardstats.listener.PluginListener;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;

public final class ScoreboardStats extends org.bukkit.plugin.java.JavaPlugin {

    private static SettingsHandler settings;
    private static ScoreboardStats instance;

    public static SettingsHandler getSettings() {
        return settings;
    }

    public static ScoreboardStats getInstance() {
        return instance;
    }

    private static void regAll() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            Database.loadAccount(player.getName());
            Score.createScoreboard(player);
        }
    }

    private static void unregisterAll() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        settings = new SettingsHandler(this);
        setupDatabase();
        regAll();
        PluginListener.init();
        getServer().getPluginManager().registerEvents(new me.games647.scoreboardstats.listener.PlayerListener(), this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new me.games647.scoreboardstats.api.UpdateThread(), 60L, settings.getIntervall() * 20L);
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
        unregisterAll();
    }
}
