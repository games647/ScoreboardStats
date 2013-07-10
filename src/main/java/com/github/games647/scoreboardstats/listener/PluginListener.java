package com.github.games647.scoreboardstats.listener;

import com.earth2me.essentials.EssentialsTimer;
import com.earth2me.essentials.IEssentials;

import com.github.games647.variables.PluginNames;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;

import com.p000ison.dev.simpleclans2.clanplayer.CraftClanPlayerManager;

import de.blablubbabc.insigns.InSigns;

import net.milkbowl.vault.economy.Economy;

import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class PluginListener {

    private PluginListener() {}

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
        final PluginManager pluginm = Bukkit.getServer().getPluginManager();

        mcmmo       = pluginm.getPlugin(PluginNames.MCMMO)      != null;

        if (pluginm.getPlugin(PluginNames.FACTIONS) != null) {
            factions = pluginm.getPlugin(PluginNames.FACTIONS).getDescription().getVersion();
        }

        if (pluginm.getPlugin(PluginNames.HEROES) != null) {
            heroes = ((Heroes) pluginm.getPlugin(PluginNames.HEROES)).getCharacterManager();
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
            essentials = ((IEssentials) pluginm.getPlugin(PluginNames.ESSENTIALS)).getTimer();
        }

        if (pluginm.getPlugin(PluginNames.INSIGNS) != null) {
            SignsListener.registerSigns((InSigns) pluginm.getPlugin(PluginNames.INSIGNS));
        }

        if (pluginm.getPlugin(PluginNames.VAULT) != null) {
            final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        }
    }
}
