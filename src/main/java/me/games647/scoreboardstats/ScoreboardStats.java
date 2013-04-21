package me.games647.scoreboardstats;

import java.util.List;
import me.games647.scoreboardstats.api.Score;
import me.games647.scoreboardstats.api.pvpstats.Database;
import static me.games647.scoreboardstats.api.pvpstats.Database.saveAll;
import me.games647.scoreboardstats.api.pvpstats.PlayerStats;
import me.games647.scoreboardstats.listener.PlayerListener;
import me.games647.scoreboardstats.listener.PluginListener;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;

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
        settings = new SettingsHandler(this);
        setupDatabase();
        instance = this;
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

    private void regAll() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            Database.loadAccount(player.getName());
            Score.createScoreboard(player, true);
        }
    }

    private void unregisterAll() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            if (PlayerListener.list.contains(player.getName())) {
              ((CraftPlayer) player).getHandle().playerConnection.sendPacket(Score.getTEMPCLEARPACKET());
              ((CraftPlayer) player).getHandle().playerConnection.sendPacket(Score.getCLEARPACKET());
            } else {
              ((CraftPlayer) player).getHandle().playerConnection.sendPacket(Score.getCLEARPACKET());
            }
        }
    }
}
