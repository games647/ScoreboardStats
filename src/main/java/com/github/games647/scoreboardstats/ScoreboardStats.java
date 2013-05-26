package com.github.games647.scoreboardstats;

import static com.github.games647.scoreboardstats.pvpstats.Database.saveAll;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.github.games647.scoreboardstats.scoreboard.SbManager;
import com.github.games647.variables.Commands;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import java.util.List;

public final class ScoreboardStats extends org.bukkit.plugin.java.JavaPlugin {

    private static SettingsHandler      settings;
    private static ScoreboardStats      instance;

    public final  java.util.Set<String> hidelist = new java.util.HashSet<String>();

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

        if (settings.isUpdateInfo()) UpdateCheck.checkUpdate(getDescription().getVersion(), Other.UPDATE_LINK);

        setupDatabase();

        getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.PluginListener(), this);
        getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new com.github.games647.scoreboardstats.listener.EntityListener(), this);

        getCommand(Commands.RELOAD_COMMAND) .setExecutor(new com.github.games647.scoreboardstats.commands.ReloadCommand());
        getCommand(Commands.HIDE_COMMAND)   .setExecutor(new com.github.games647.scoreboardstats.commands.DisableCommand(this));

        SbManager.regAll();

        getServer().getScheduler()
                .scheduleSyncRepeatingTask(this
                        , new com.github.games647.scoreboardstats.RefreshTask(), Other.STARTUP_DELAY, settings.getIntervall() * Other.TICKS_PER_SECOND);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        final List<Class<?>> list = new java.util.ArrayList<Class<?>>(1);
        list.add(PlayerStats.class);

        return list;
    }

    public static void onReload() {
        settings.loadConfig();
        instance.setupDatabase();
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
                getLogger().info(Message.NON_EXISTING_DATABASE);
                installDDL();
            }

            com.github.games647.scoreboardstats.pvpstats.Database.setDatabase(database);
        }
    }
}
