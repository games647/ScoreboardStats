package me.games647.scoreboardstats.listener;

import com.earth2me.essentials.EssentialsTimer;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;

public final class PluginListener implements org.bukkit.event.Listener {

    private static Economy econ;
    private static boolean mcmmo, paintball;
    private static EssentialsTimer essentials;
    private static SimpleClans simpleclans;

    public static Economy getEcon() {
        return econ;
    }

    public static boolean isMcmmo() {
        return mcmmo;
    }

    public static boolean isPaintball() {
        return paintball;
    }

    public static SimpleClans getSimpleclans() {
        return simpleclans;
    }

    public static EssentialsTimer getEssentials() {
        return essentials;
    }

    public static void init() {
        final org.bukkit.plugin.PluginManager pm = Bukkit.getServer().getPluginManager();

        mcmmo = (pm.getPlugin("mcMMO") != null);
        simpleclans = (SimpleClans) pm.getPlugin("SimpleClans");
        paintball = (pm.getPlugin("Paintball") != null);

        if (pm.getPlugin("Essentials") != null) {
            essentials = ((com.earth2me.essentials.Essentials) pm.getPlugin("Essentials")).getTimer();
        }

        if (pm.getPlugin("InSigns") != null) {
            SignsListener.registerSigns((de.blablubbabc.insigns.InSigns) pm.getPlugin("InSigns"));
        }

        if (pm.getPlugin("Vault") != null) {
            final org.bukkit.plugin.RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (economyProvider != null) {
                econ = economyProvider.getProvider();
            }
        }
    }
}
