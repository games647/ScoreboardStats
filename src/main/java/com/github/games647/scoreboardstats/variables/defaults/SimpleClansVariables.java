package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

/**
 * Replace all variables that are associated with the SimpleClans plugin
 *
 * https://dev.bukkit.org/bukkit-plugins/simpleclans/
 *
 * @see SimpleClans
 * @see ClanPlayer
 * @see Clan
 * @see ClanManager
 */
public class SimpleClansVariables extends DefaultReplaceAdapter<SimpleClans> {

    private final ClanManager clanManager;

    /**
     * Creates a new SimpleClans replacer
     */
    public SimpleClansVariables() {
        super(SimpleClans.getInstance()
                , "kills", "deaths", "kdr"
                , "members", "clan_kdr", "rivals", "allies"
                , "clan_money", "clan_kills", "allies_total", "members_online");

        checkVersionException("2.5");

        clanManager = getPlugin().getClanManager();
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        //If simpleclans doesn't track the player yet return -1
        ClanPlayer clanPlayer = clanManager.getClanPlayer(player);
        if (clanPlayer == null) {
            replaceEvent.setScore(-1);
            return;
        }

        if ("kills".equals(variable)) {
            int civilianKills = clanPlayer.getCivilianKills();
            int neutralKills = clanPlayer.getNeutralKills();
            int rivalKills = clanPlayer.getRivalKills();
            //count all kill types
            replaceEvent.setScore(civilianKills + neutralKills + rivalKills);
        } else if ("deaths".equals(variable)) {
            replaceEvent.setScore(clanPlayer.getDeaths());
        } else if ("kdr".equals(variable)) {
            replaceEvent.setScore(Math.round(clanPlayer.getKDR() * 100));
        } else {
            //Check if the player has a clan
            Clan clan = clanPlayer.getClan();
            if (clan == null) {
                replaceEvent.setScore(-1);
                return;
            }

            if ("members".equals(variable)) {
                replaceEvent.setScore(clan.getMembers().size());
            } else if ("clan_kdr".equals(variable)) {
                replaceEvent.setScore(Math.round(clan.getTotalKDR() * 100));
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
                int civilianKills = clan.getTotalCivilian();
                int neutralKills = clan.getTotalNeutral();
                int rivalKills = clan.getTotalNeutral();
                //count all kill types
                replaceEvent.setScore(civilianKills + neutralKills + rivalKills);
            }
        }
    }
}
