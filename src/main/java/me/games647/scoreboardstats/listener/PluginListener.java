package me.games647.scoreboardstats.listener;

import com.earth2me.essentials.EssentialsTimer;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;

public final class PluginListener {

    private static Economy econ;
    private static boolean mcmmo;
    private static EssentialsTimer essentials;
    private static SimpleClans simpleclans;

    public static Economy getEcon() {
        return econ;
    }

    public static boolean isMcmmo() {
        return mcmmo;
    }

    public static SimpleClans getSimpleclans() {
        return simpleclans;
    }

    public static EssentialsTimer getEssentials() {
        return essentials;
    }

    public static void init() {
        final org.bukkit.plugin.PluginManager pluginm = Bukkit.getServer().getPluginManager();

        mcmmo = (pluginm.getPlugin("mcMMO") != null);
        simpleclans = (SimpleClans) pluginm.getPlugin("SimpleClans");

        if (pluginm.getPlugin("Essentials") != null) {
            essentials = ((pluginm.getPlugin("Essentials") != null) ? (((com.earth2me.essentials.Essentials) pluginm.getPlugin("Essentials")).getTimer()) : null);
        }

        if (pluginm.getPlugin("InSigns") != null) {
            SignsListener.registerSigns((de.blablubbabc.insigns.InSigns) pluginm.getPlugin("InSigns"));
        }

        if (pluginm.getPlugin("Vault") != null) {
            final org.bukkit.plugin.RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (economyProvider != null) {
                econ = economyProvider.getProvider();
            }
        }
    }
}
