package com.github.games647.scoreboardstats.listener;

import com.github.games647.variables.VariableList;
import org.bukkit.entity.Player;

public final class SimpleClansReplacer  {

    public static int getSimpleClans1Value(String key, Player player) {
        if (VariableList.KILLS_CIVILIAN.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getCivilianKills();
        }

        if (VariableList.KILLS_NEUTRAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getNeutralKills();
        }

        if (VariableList.KILLS_RIVAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getRivalKills();
        }

        if (VariableList.KILLS_TOTAL.equals(key)) {
            final com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer clanPlayer = PluginListener.getSimpleclans().getClanPlayer(player);
            return clanPlayer.getCivilianKills() + clanPlayer.getNeutralKills() + clanPlayer.getRivalKills();
        }

        if (VariableList.DEATHS.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getDeaths();
        }

        if (VariableList.KDR.equals(key)) {
            return Math.round(PluginListener.getSimpleclans().getClanPlayer(player).getKDR());
        }

        if (VariableList.MEMBER.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getMembers().size();
        }

        if (VariableList.CLAN_KDR.equals(key)) {
            return (int) PluginListener.getSimpleclans().getClanPlayer(player).getClan().getKDR();
        }

        if (VariableList.CLAN_MONEY.equals(key)) {
            return (int) PluginListener.getSimpleclans().getClanPlayer(player).getClan().getBalance();
        }

        if (VariableList.RIVAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getRivals().size();
        }

        if (VariableList.ALLIES.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getAllies().size();
        }

        if (VariableList.ALLIES_TOTAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getAllAllyMembers().size();
        }

        if (VariableList.CLAN_KILLS.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getTotalKills().length;
        }

        return -1;
    }

    public static int getSimpleClans2Value(String key, Player player) {
        if (VariableList.KILLS_CIVILIAN.equals(key)) {
            return PluginListener.getSimpleclans2().getClanPlayer(player).getCivilianKills();
        }

        if (VariableList.KILLS_NEUTRAL.equals(key)) {
            return PluginListener.getSimpleclans2().getClanPlayer(player).getNeutralKills();
        }

        if (VariableList.KILLS_RIVAL.equals(key)) {
            return PluginListener.getSimpleclans2().getClanPlayer(player).getRivalKills();
        }

        if (VariableList.KILLS_TOTAL.equals(key)) {
            final net.sacredlabyrinth.phaed.simpleclans.ClanPlayer clanPlayer = PluginListener.getSimpleclans2().getClanPlayer(player);
            return clanPlayer.getCivilianKills() + clanPlayer.getNeutralKills() + clanPlayer.getRivalKills();
        }

        if (VariableList.DEATHS.equals(key)) {
            return PluginListener.getSimpleclans2().getClanPlayer(player).getDeaths();
        }

        if (VariableList.KDR.equals(key)) {
            return Math.round(PluginListener.getSimpleclans2().getClanPlayer(player).getKDR());
        }

        if (VariableList.MEMBER.equals(key)) {
            return PluginListener.getSimpleclans2().getClanPlayer(player).getClan().getMembers().size();
        }

        if (VariableList.CLAN_KDR.equals(key)) {
            return (int) PluginListener.getSimpleclans2().getClanPlayer(player).getClan().getTotalKDR();
        }

        if (VariableList.CLAN_MONEY.equals(key)) {
            return (int) PluginListener.getSimpleclans2().getClanPlayer(player).getClan().getBalance();
        }

        if (VariableList.RIVAL.equals(key)) {
            return PluginListener.getSimpleclans2().getClanPlayer(player).getClan().getRivals().size();
        }

        if (VariableList.ALLIES.equals(key)) {
            return PluginListener.getSimpleclans2().getClanPlayer(player).getClan().getAllies().size();
        }

        if (VariableList.ALLIES_TOTAL.equals(key)) {
            return PluginListener.getSimpleclans2().getClanPlayer(player).getClan().getAllAllyMembers().size();
        }

        if (VariableList.CLAN_KILLS.equals(key)) {
            final net.sacredlabyrinth.phaed.simpleclans.Clan clan = PluginListener.getSimpleclans2().getClanPlayer(player).getClan();
            return clan.getTotalCivilian() + clan.getTotalNeutral() + clan.getTotalNeutral();
        }

        return -1;
    }
}
