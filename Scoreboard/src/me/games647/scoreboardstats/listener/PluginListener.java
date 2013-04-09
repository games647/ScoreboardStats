package me.games647.scoreboardstats.listener;

import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;

public final class PluginListener implements org.bukkit.event.Listener {

    private static Economy econ;
    private static boolean mcmmo, survival, paintball, mobarena, nolagg;
    private static SimpleClans simpleclans;

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

    public static SimpleClans getSimpleclans() {
        return simpleclans;
    }

    public static void init() {
        final org.bukkit.plugin.PluginManager pm = Bukkit.getServer().getPluginManager();
        mcmmo = (pm.getPlugin("mcMMO") != null);
        if (pm.getPlugin("Vault") != null) {
            final org.bukkit.plugin.RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (economyProvider != null) {
                econ = economyProvider.getProvider();
            }
        }
        if (pm.getPlugin("SimpleClans") != null) {
            simpleclans = (SimpleClans) pm.getPlugin("SimpleClans");
        }
        survival = (pm.getPlugin("SurvivalGames") != null);
        paintball = (pm.getPlugin("Paintball") != null);
        mobarena = (pm.getPlugin("MobArena") != null);
        nolagg = (pm.getPlugin("NoLagg") != null);
        if (pm.getPlugin("InSigns") != null) {
            new me.games647.scoreboardstats.listener.SignsListener((de.blablubbabc.insigns.InSigns) pm.getPlugin("InSigns"));
        }
    }
}
