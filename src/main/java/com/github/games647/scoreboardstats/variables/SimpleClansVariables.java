package com.github.games647.scoreboardstats.variables;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class SimpleClansVariables implements ReplaceManager.Replaceable {

    private ClanManager clanManager;

    public SimpleClansVariables() {
        initialize();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        final ClanPlayer clanPlayer = clanManager.getClanPlayer(player);
        if (clanPlayer == null) {
            return -1;
        }

        if ("%kills_civilian%".equals(variable)) {
            return clanPlayer.getCivilianKills();
        }

        if ("%kills_neutral%".equals(variable)) {
            return clanPlayer.getNeutralKills();
        }

        if ("%kills_rival%".equals(variable)) {
            return clanPlayer.getRivalKills();
        }

        if ("%kills_total%".equals(variable)) {
            return clanPlayer.getCivilianKills() + clanPlayer.getNeutralKills() + clanPlayer.getRivalKills();
        }

        if ("deaths".equals(variable)) {
            return clanPlayer.getDeaths();
        }

        if ("%kdr%".equals(variable)) {
            return Math.round(clanPlayer.getKDR());
        }

        if (clanPlayer.getClan() != null) {
            if ("%member%".equals(variable)) {
                return clanPlayer.getClan().getMembers().size();
            }

            if ("%clan_kdr%".equals(variable)) {
                return Math.round(clanPlayer.getClan().getTotalKDR());
            }

            if ("%clan_money%".equals(variable)) {
                return (int) Math.round(clanPlayer.getClan().getBalance());
            }

            if ("%rivals%".equals(variable)) {
                return clanPlayer.getClan().getRivals().size();
            }

            if ("%allies%".equals(variable)) {
                return clanPlayer.getClan().getAllies().size();
            }

            if ("%allies_total%".equals(variable)) {
                return clanPlayer.getClan().getAllAllyMembers().size();
            }

            if ("%clan_kills%".equals(variable)) {
                final Clan clan = clanPlayer.getClan();
                return clan.getTotalCivilian() + clan.getTotalNeutral() + clan.getTotalNeutral();
            }
        }

        return UNKOWN_VARIABLE;
    }

    private void initialize() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        final SimpleClans clansPlugin = (SimpleClans) pluginManager.getPlugin("SimpleClans");
        clanManager = clansPlugin.getClanManager();
    }
}