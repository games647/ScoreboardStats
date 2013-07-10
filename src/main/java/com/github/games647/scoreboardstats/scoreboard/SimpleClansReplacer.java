package com.github.games647.scoreboardstats.scoreboard;

import com.github.games647.scoreboardstats.listener.PluginListener;
import com.github.games647.variables.VariableList;

import org.bukkit.entity.Player;

final class SimpleClansReplacer {

    private SimpleClansReplacer() {}

    public static int getSimpleClans1Value(String key, Player player) {
        final com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer clanPlayer = PluginListener.getSimpleclans().getClanPlayer(player);
        if (clanPlayer == null) {
            return -1;
        }

        if (VariableList.KILLS_CIVILIAN.equals(key)) {
            return clanPlayer.getCivilianKills();
        }

        if (VariableList.KILLS_NEUTRAL.equals(key)) {
            return clanPlayer.getNeutralKills();
        }

        if (VariableList.KILLS_RIVAL.equals(key)) {
            return clanPlayer.getRivalKills();
        }

        if (VariableList.KILLS_TOTAL.equals(key)) {
            return clanPlayer.getCivilianKills() + clanPlayer.getNeutralKills() + clanPlayer.getRivalKills();
        }

        if (VariableList.DEATHS.equals(key)) {
            return clanPlayer.getDeaths();
        }

        if (VariableList.KDR.equals(key)) {
            return Math.round(clanPlayer.getKDR());
        }

        if (clanPlayer.getClan() != null) {
            if (VariableList.MEMBER.equals(key)) {
                return clanPlayer.getClan().getMembers().size();
            }

            if (VariableList.CLAN_KDR.equals(key)) {
                return Math.round(clanPlayer.getClan().getKDR());
            }

            if (VariableList.CLAN_MONEY.equals(key)) {
                return (int) Math.round(clanPlayer.getClan().getBalance());
            }

            if (VariableList.RIVAL.equals(key)) {
                return clanPlayer.getClan().getRivals().size();
            }

            if (VariableList.ALLIES.equals(key)) {
                return clanPlayer.getClan().getAllies().size();
            }

            if (VariableList.ALLIES_TOTAL.equals(key)) {
                return clanPlayer.getClan().getAllAllyMembers().size();
            }

            if (VariableList.CLAN_KILLS.equals(key)) {
                return clanPlayer.getClan().getTotalKills().length;
            }
        }

        return -1;
    }

    public static int getSimpleClans2Value(String key, Player player) {
        final net.sacredlabyrinth.phaed.simpleclans.ClanPlayer clanPlayer = PluginListener.getSimpleclans2().getClanPlayer(player);
        if (clanPlayer == null) {
            return -1;
        }

        if (VariableList.KILLS_CIVILIAN.equals(key)) {
            return clanPlayer.getCivilianKills();
        }

        if (VariableList.KILLS_NEUTRAL.equals(key)) {
            return clanPlayer.getNeutralKills();
        }

        if (VariableList.KILLS_RIVAL.equals(key)) {
            return clanPlayer.getRivalKills();
        }

        if (VariableList.KILLS_TOTAL.equals(key)) {
            return clanPlayer.getCivilianKills() + clanPlayer.getNeutralKills() + clanPlayer.getRivalKills();
        }

        if (VariableList.DEATHS.equals(key)) {
            return clanPlayer.getDeaths();
        }

        if (VariableList.KDR.equals(key)) {
            return Math.round(clanPlayer.getKDR());
        }

        if (clanPlayer.getClan() != null) {
            if (VariableList.MEMBER.equals(key)) {
                return clanPlayer.getClan().getMembers().size();
            }

            if (VariableList.CLAN_KDR.equals(key)) {
                return Math.round(clanPlayer.getClan().getTotalKDR());
            }

            if (VariableList.CLAN_MONEY.equals(key)) {
                return (int) Math.round(clanPlayer.getClan().getBalance());
            }

            if (VariableList.RIVAL.equals(key)) {
                return clanPlayer.getClan().getRivals().size();
            }

            if (VariableList.ALLIES.equals(key)) {
                return clanPlayer.getClan().getAllies().size();
            }

            if (VariableList.ALLIES_TOTAL.equals(key)) {
                return clanPlayer.getClan().getAllAllyMembers().size();
            }

            if (VariableList.CLAN_KILLS.equals(key)) {
                final net.sacredlabyrinth.phaed.simpleclans.Clan clan = clanPlayer.getClan();
                return clan.getTotalCivilian() + clan.getTotalNeutral() + clan.getTotalNeutral();
            }
        }

        return -1;
    }
}
