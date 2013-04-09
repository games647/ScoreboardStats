package me.games647.scoreboardstats;

import java.util.List;
import me.games647.scoreboardstats.api.PlayerStats;
import me.games647.scoreboardstats.settings.SettingsHandler;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public final class ScoreboardStats extends org.bukkit.plugin.java.JavaPlugin {

    private static SettingsHandler settings;
    private static Economy econ;
    private static boolean mcmmo, survival, paintball, mobarena, nolagg;
    private static SimpleClans simpleclans;

    public static SimpleClans getSimpleclans() {
        return simpleclans;
    }

    public static SettingsHandler getSettings() {
        return settings;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static boolean isMcmmo() {
        return mcmmo;
    }

    public static boolean isSurvival() {
        return survival;
    }

    public static boolean isPaintball() {
        return paintball;
    }

    public static boolean isMobarena() {
        return mobarena;
    }

    public static boolean isNolagg() {
        return nolagg;
    }

    @Override
    public void onEnable() {
        settings = new SettingsHandler(this);
        setupDatabase();
        setupPlugins();
        getServer().getPluginManager().registerEvents(new me.games647.scoreboardstats.listener.PlayerListener(), this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new me.games647.scoreboardstats.api.UpdateThread(), 60L, getConfig().getInt("Scoreboard.Update-delay") * 20L);
    }

    private void setupDatabase() {

        if (!getSettings().isPvpstats()) {
            return;
        }

        try {
            getDatabase().find(PlayerStats.class).findRowCount();
        } catch (javax.persistence.PersistenceException ex) {
            getLogger().info("Can't find an existing Database, so creating a new one");
            installDDL();
        }
        me.games647.scoreboardstats.api.Database.setDatabase(getDatabase());
        getServer().getPluginManager().registerEvents(new me.games647.scoreboardstats.listener.EntityListener(), this);
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
    }

    private void setupPlugins() {
        final org.bukkit.plugin.PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("mcMMO") != null) {
            mcmmo = true;
        }
        if (pm.getPlugin("Vault") != null) {
            final org.bukkit.plugin.RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (economyProvider != null) {
                econ = economyProvider.getProvider();
            }
        }
        if (pm.getPlugin("SimpleClans") != null) {
            simpleclans = (SimpleClans) pm.getPlugin("SimpleClans");
        }
        if (pm.getPlugin("SurvivalGames") != null) {
            survival = true;
        }
        if (pm.getPlugin("Paintball") != null) {
            paintball = true;
        }
        if (pm.getPlugin("MobArena") != null) {
            mobarena = true;
        }
        if (pm.getPlugin("NoLagg") != null) {
            nolagg = true;
        }
        if (pm.getPlugin("InSigns") != null) {
            new me.games647.scoreboardstats.listener.SignsListener(pm.getPlugin("InSigns"));
        }
    }
}
