package com.github.games647.scoreboardstats.listener;

import com.earth2me.essentials.EssentialsTimer;
import com.github.games647.variables.PluginNames;
import com.p000ison.dev.simpleclans2.clanplayer.CraftClanPlayerManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public final class PluginListener implements org.bukkit.event.Listener {

    public PluginListener() {
        init();
    }

    private static Economy economy;
    private static boolean mcmmo;
    private static EssentialsTimer essentials;
    private static CraftClanPlayerManager simpleclans;

    public static Economy getEconomy() {
        return economy;
    }

    public static boolean isMcmmo() {
        return mcmmo;
    }

    public static CraftClanPlayerManager getSimpleclans() {
        return simpleclans;
    }

    public static EssentialsTimer getEssentials() {
        return essentials;
    }

    public static void init() {
        final org.bukkit.plugin.PluginManager pluginm = Bukkit.getServer().getPluginManager();

        mcmmo = pluginm.getPlugin(PluginNames.MCMMO) != null;

        if (pluginm.getPlugin(PluginNames.SIMPLECLANS) != null) {
            simpleclans = ((com.p000ison.dev.simpleclans2.SimpleClans) pluginm.getPlugin(PluginNames.SIMPLECLANS)).getClanPlayerManager();
        }

        if (pluginm.getPlugin(PluginNames.ESSENTIALS) != null) {
            essentials = ((com.earth2me.essentials.Essentials) pluginm.getPlugin(PluginNames.ESSENTIALS)).getTimer();
        }

        if (pluginm.getPlugin(PluginNames.INSIGNS) != null) {
            SignsListener.registerSigns((de.blablubbabc.insigns.InSigns) pluginm.getPlugin(PluginNames.INSIGNS));
        }

        if (pluginm.getPlugin(PluginNames.VAULT) != null) {
            final org.bukkit.plugin.RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        }
    }

    @EventHandler
    public void onDisable(final org.bukkit.event.server.PluginDisableEvent disable) {
        if (disable.getPlugin().getName().equals(PluginNames.MCMMO)) {
            mcmmo = false;
        }
    }

    @EventHandler
    public void onEnable(final org.bukkit.event.server.PluginEnableEvent enable) {
        if (enable.getPlugin().getName().equals(PluginNames.MCMMO)) {
            mcmmo = true;
        }
    }
}
