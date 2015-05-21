package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.Version;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

/**
 * Replace all variables that are associated with the SimpleClans plugin
 *
 * http://dev.bukkit.org/bukkit-plugins/simpleclans/
 *
 * @see SimpleClans
 * @see ClanPlayer
 * @see Clan
 * @see ClanManager
 */
public class SimpleClansVariables extends VariableReplaceAdapter<SimpleClans> {

    private final ClanManager clanManager;

    /**
     * Creates a new SimpleClans replacer
     */
    public SimpleClansVariables() {
        super((SimpleClans) Bukkit.getPluginManager().getPlugin("SimpleClans")
                , "kills", "deaths", "kdr"
                , "members", "clan_kdr", "rivals", "allies"
                , "clan_money", "clan_kills", "allies_total", "members_online");

        final String version = getPlugin().getDescription().getVersion();
        if (Version.compare("2.5", version) >= 0) {
            clanManager = getPlugin().getClanManager();
        } else {
            throw new UnsupportedPluginException("SimpleClans under version 2.5 are not supported");
        }
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        //If simpleclans doesn't track the player yet return -1
        final ClanPlayer clanPlayer = clanManager.getClanPlayer(player);
        if (clanPlayer == null) {
            replaceEvent.setScore(-1);
            return;
        }

        if ("kills".equals(variable)) {
            final int civilianKills = clanPlayer.getCivilianKills();
            final int neutralKills = clanPlayer.getNeutralKills();
            final int rivalKills = clanPlayer.getRivalKills();
            //count all kill types
            replaceEvent.setScore(civilianKills + neutralKills + rivalKills);
        } else if ("deaths".equals(variable)) {
            replaceEvent.setScore(clanPlayer.getDeaths());
        } else if ("kdr".equals(variable)) {
            replaceEvent.setScore(Math.round(clanPlayer.getKDR()));
        } else {
            //Check if the player has a clan
            final Clan clan = clanPlayer.getClan();
            if (clan == null) {
                replaceEvent.setScore(-1);
                return;
            }

            if ("members".equals(variable)) {
                replaceEvent.setScore(clan.getMembers().size());
            } else if ("clan_kdr".equals(variable)) {
                replaceEvent.setScore(Math.round(clan.getTotalKDR()));
            }
            if ("clan_money".equals(variable)) {
                replaceEvent.setScore(NumberConversions.round(clan.getBalance()));
            } else if ("rivals".equals(variable)) {
                replaceEvent.setScore(clan.getRivals().size());
            } else if ("allies".equals(variable)) {
                replaceEvent.setScore(clan.getAllies().size());
            } else if ("members_online".equals(variable)) {
                replaceEvent.setScore(clan.getOnlineMembers().size());
            } else if ("allies_total".equals(variable)) {
                replaceEvent.setScore(clan.getAllAllyMembers().size());
            } else if ("clan_kills".equals(variable)) {
                final int civilianKills = clan.getTotalCivilian();
                final int neutralKills = clan.getTotalNeutral();
                final int rivalKills = clan.getTotalNeutral();
                //count all kill types
                replaceEvent.setScore(civilianKills + neutralKills + rivalKills);
            }
        }
    }
}
