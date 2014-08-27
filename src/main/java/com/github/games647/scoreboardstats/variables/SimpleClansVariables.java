package com.github.games647.scoreboardstats.variables;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

/**
 * Replace all variables that are associated with the SimpleClans plugin
 *
 * @see SimpleClans
 * @see ClanPlayer
 * @see Clan
 * @see ClanManager
 */
public class SimpleClansVariables implements Replaceable {

    private final ClanManager clanManager;

    /**
     * Creates a new SimpleClans replacer
     */
    public SimpleClansVariables() {
        final Plugin clansPlugin = Bukkit.getPluginManager().getPlugin("SimpleClans");
        final String version = clansPlugin.getDescription().getVersion();
        if (!"Legacy".equalsIgnoreCase(version)) {
            throw new UnsupportedPluginException("SimpleClans under version Legacy are not supported");
        }

        final SimpleClans simpleClans = (SimpleClans) clansPlugin;
        clanManager = simpleClans.getClanManager();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        //If simpleclans doesn't track the player yet return -1
        final ClanPlayer clanPlayer = clanManager.getClanPlayer(player);

        if ("%kills%".equals(variable)) {
            if (clanPlayer == null) {
                return -1;
            } else {
                final int civilianKills = clanPlayer.getCivilianKills();
                final int neutralKills = clanPlayer.getNeutralKills();
                final int rivalKills = clanPlayer.getRivalKills();
                //count all kill types
                return civilianKills + neutralKills + rivalKills;
            }
        }

        if ("deaths".equals(variable)) {
            return clanPlayer == null ? -1 : clanPlayer.getDeaths();
        }

        if ("%kdr%".equals(variable)) {
            return clanPlayer == null ? -1 : Math.round(clanPlayer.getKDR());
        }

        //Check if the player has a clan
        final Clan clan = clanPlayer == null ? null : clanPlayer.getClan();
        if ("%member%".equals(variable)) {
            return clan == null ? -1 : clan.getMembers().size();
        }

        if ("%clan_kdr%".equals(variable)) {
            return clan == null ? -1 : Math.round(clan.getTotalKDR());
        }

        if ("%clan_money%".equals(variable)) {
            return clan == null ? -1 : NumberConversions.round(clan.getBalance());
        }

        if ("%rivals%".equals(variable)) {
            return clan == null ? -1 : clan.getRivals().size();
        }

        if ("%allies%".equals(variable)) {
            return clan == null ? -1 : clan.getAllies().size();
        }

        if ("%members_online%".equals(variable)) {
            return clan == null ? -1 : clan.getOnlineMembers().size();
        }

        if ("%allies_total%".equals(variable)) {
            return clan == null ? -1 : clan.getAllAllyMembers().size();
        }

        if ("%clan_kills%".equals(variable)) {
            return clan == null ? -1 : clan.getTotalCivilian() + clan.getTotalNeutral() + clan.getTotalNeutral();
        }

        return UNKOWN_VARIABLE;
    }
}