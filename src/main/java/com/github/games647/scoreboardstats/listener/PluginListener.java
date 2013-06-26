package com.github.games647.scoreboardstats.listener;

import com.earth2me.essentials.EssentialsTimer;
import com.github.games647.variables.PluginNames;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.p000ison.dev.simpleclans2.clanplayer.CraftClanPlayerManager;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.Bukkit;

public final class PluginListener {

    private static String   factions;

    private static boolean  mcmmo;

    private static CharacterManager         heroes;
    private static Economy                  economy;
    private static EssentialsTimer          essentials;
    private static CraftClanPlayerManager   simpleclans;
    private static ClanManager              simpleclans2;

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

    public static CharacterManager getHeroes() {
        return heroes;
    }

    public static String getFactions() {
        return factions;
    }

    public static ClanManager getSimpleclans2() {
        return simpleclans2;
    }

    public static void init() {
        final org.bukkit.plugin.PluginManager pluginm = Bukkit.getServer().getPluginManager();

        mcmmo       = pluginm.getPlugin(PluginNames.MCMMO)      != null;

        if (pluginm.getPlugin(PluginNames.FACTIONS) != null) {
            factions = pluginm.getPlugin(PluginNames.FACTIONS).getDescription().getVersion();
        }

        if (pluginm.getPlugin(PluginNames.HEROES) != null) {
            heroes = ((com.herocraftonline.heroes.Heroes) pluginm.getPlugin(PluginNames.HEROES)).getCharacterManager();
        }

        if (pluginm.getPlugin(PluginNames.SIMPLECLANS) != null) {
            final String version = pluginm.getPlugin(PluginNames.SIMPLECLANS).getDescription().getVersion();
            if (version.charAt(0) == '1') {
                simpleclans = ((com.p000ison.dev.simpleclans2.SimpleClans) pluginm.getPlugin(PluginNames.SIMPLECLANS)).getClanPlayerManager();
            } else {
                simpleclans2 = ((net.sacredlabyrinth.phaed.simpleclans.SimpleClans) pluginm.getPlugin(PluginNames.SIMPLECLANS)).getClanManager();
            }
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
}
