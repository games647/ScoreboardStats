package com.github.games647.scoreboardstats;

import static com.github.games647.scoreboardstats.pvpstats.Database.saveAll;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.github.games647.scoreboardstats.scoreboard.SbManager;
import com.github.games647.variables.Commands;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import java.util.List;
import net.h31ix.updater.Updater;

public final class ScoreboardStats extends org.bukkit.plugin.java.JavaPlugin {

    private static SettingsHandler settings;
    private static ScoreboardStats instance;

    private int taskid;

    public final java.util.Set<String> hidelist = new java.util.HashSet<String>();

    public static SettingsHandler getSettings() {
        return settings;
    }

    public static ScoreboardStats getInstance() {
        return instance;
    }

    public ScoreboardStats() {
        super();
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        settings = new SettingsHandler(this);

        if (settings.isUpdateInfo()) {
            new Updater(this, "scoreboardstats", this.getFile(), Updater.UpdateType.DEFAULT, true);
        }

        setupDatabase();

        com.github.games647.scoreboardstats.listener.PluginListener.init();

        getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.EntityListener(), this);

        getCommand(Commands.RELOAD_COMMAND).setExecutor(new com.github.games647.scoreboardstats.commands.ReloadCommand(this));
        getCommand(Commands.HIDE_COMMAND).setExecutor(new com.github.games647.scoreboardstats.commands.DisableCommand(this));

        SbManager.regAll();

        taskid = getServer().getScheduler()
                .scheduleSyncRepeatingTask(this, new com.github.games647.scoreboardstats.RefreshTask(), Other.STARTUP_DELAY, settings.getIntervall() * Other.TICKS_PER_SECOND);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new java.util.ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }

    public void onReload() {
        final int intervall = settings.getIntervall();
        final boolean pvpstats = settings.isPvpStats();
        final int length = settings.getItemsLength();

        settings.loadConfig();

        if (intervall != settings.getIntervall()) {
            getServer().getScheduler().cancelTask(taskid);
            getServer().getScheduler()
                    .scheduleSyncRepeatingTask(this, new com.github.games647.scoreboardstats.RefreshTask(), Other.STARTUP_DELAY, settings.getIntervall() * Other.TICKS_PER_SECOND);
        }

        if (pvpstats != settings.isPvpStats()) {
            instance.setupDatabase();
            SbManager.regAll();
        }

        if (length != settings.getItemsLength()) {
            for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                final org.bukkit.scoreboard.Objective objective = player.getScoreboard().getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

                if (objective == null
                        || !objective.getName().equals(Other.PLUGIN_NAME)) {
                    continue;
                }

                player.getScoreboard().clearSlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
            }
        }
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
                getLogger().info(Message.NON_EXISTING_DATABASE);
                installDDL();
            }

            com.github.games647.scoreboardstats.pvpstats.Database.setDatabase(database);
        }
    }
}
