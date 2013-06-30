package com.github.games647.scoreboardstats;

import static com.github.games647.scoreboardstats.pvpstats.Database.saveAll;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.github.games647.scoreboardstats.scoreboard.SbManager;
import com.github.games647.variables.Commands;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import java.util.List;
import net.h31ix.Updater;

public final class ScoreboardStats extends org.bukkit.plugin.java.JavaPlugin {

    public final java.util.Set<String> hidelist = new java.util.HashSet<String>(10);

    private static ScoreboardStats instance;

    private int taskid;

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

        if (Settings.isUpdateInfo()) {
            new Updater(this, "scoreboardstats", getFile(), Updater.UpdateType.DEFAULT, true);
        }

        setupDatabase();

        com.github.games647.scoreboardstats.listener.PluginListener.init();

        getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.EntityListener(), this);

        getCommand(Commands.RELOAD_COMMAND) .setExecutor(new com.github.games647.scoreboardstats.commands.ReloadCommand());
        getCommand(Commands.HIDE_COMMAND)   .setExecutor(new com.github.games647.scoreboardstats.commands.DisableCommand());
        getCommand(Commands.SIDEBAR)        .setExecutor(new com.github.games647.scoreboardstats.commands.SidebarCommand());

        SbManager.regAll();

        taskid = getServer().getScheduler()
                .scheduleSyncRepeatingTask(this, new com.github.games647.scoreboardstats.RefreshTask(), Other.STARTUP_DELAY, Settings.getIntervall() * Other.TICKS_PER_SECOND - Other.HALF_SECOND_TICK);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new java.util.ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }

    public void onReload() {
        final int     intervall     = Settings.getIntervall();
        final int     length        = Settings.getItemsLenght();
        final boolean pvpstats      = Settings.isPvpStats();

        Settings.loadConfig();

        if (intervall != Settings.getIntervall()) {
            getServer().getScheduler().cancelTask(taskid);
            getServer().getScheduler()
                    .scheduleSyncRepeatingTask(this, new com.github.games647.scoreboardstats.RefreshTask(), Other.STARTUP_DELAY, Settings.getIntervall() * Other.TICKS_PER_SECOND - Other.HALF_SECOND_TICK);
        }

        if (length != Settings.getItemsLenght()) {
            SbManager.unregisterAll();
        }

        if (pvpstats != Settings.isPvpStats()) {
            instance.setupDatabase();
            SbManager.regAll();
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        saveAll();
        SbManager.unregisterAll();
    }

    private void setupDatabase() {
        if (Settings.isPvpStats()) {
            final com.avaje.ebean.EbeanServer database = getDatabase();

            try {
                database.find(PlayerStats.class).findRowCount();
            } catch (javax.persistence.PersistenceException ex) {
                getServer().getConsoleSender().sendMessage(Message.LOG_NAME + Message.NON_EXISTING_DATABASE);
                installDDL();
            }

            com.github.games647.scoreboardstats.pvpstats.Database.setDatabase(database);
        }
    }
}
