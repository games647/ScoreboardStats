package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.Version;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;
import com.github.games647.scoreboardstats.variables.VariableReplacer;

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
public class SimpleClansVariables implements VariableReplacer {

    private final ClanManager clanManager;

    /**
     * Creates a new SimpleClans replacer
     */
    public SimpleClansVariables() {
        final Plugin clansPlugin = Bukkit.getPluginManager().getPlugin("SimpleClans");
        final String version = clansPlugin.getDescription().getVersion();
        if ("Legacy".equalsIgnoreCase(version) || Version.compare("2.5", version) >= 0) {
            final SimpleClans simpleClans = (SimpleClans) clansPlugin;
            clanManager = simpleClans.getClanManager();
        } else {
            throw new UnsupportedPluginException("SimpleClans under version Legacy are not supported");
        }
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        //If simpleclans doesn't track the player yet return -1
        final ClanPlayer clanPlayer = clanManager.getClanPlayer(player);

        if ("kills".equals(variable)) {
            if (clanPlayer == null) {
                replaceEvent.setScore(-1);
            } else {
                final int civilianKills = clanPlayer.getCivilianKills();
                final int neutralKills = clanPlayer.getNeutralKills();
                final int rivalKills = clanPlayer.getRivalKills();
                //count all kill types
                replaceEvent.setScore(civilianKills + neutralKills + rivalKills);
            }
        }

        if ("deaths".equals(variable)) {
            replaceEvent.setScore(clanPlayer == null ? -1 : clanPlayer.getDeaths());
        }

        if ("kdr".equals(variable)) {
            replaceEvent.setScore(clanPlayer == null ? -1 : Math.round(clanPlayer.getKDR()));
        }

        //Check if the player has a clan
        final Clan clan = clanPlayer == null ? null : clanPlayer.getClan();
        if ("member".equals(variable)) {
            replaceEvent.setScore(clan == null ? -1 : clan.getMembers().size());
        }

        if ("clan_kdr".equals(variable)) {
            replaceEvent.setScore(clan == null ? -1 : Math.round(clan.getTotalKDR()));
        }

        if ("clan_money".equals(variable)) {
            replaceEvent.setScore(clan == null ? -1 : NumberConversions.round(clan.getBalance()));
        }

        if ("rivals".equals(variable)) {
            replaceEvent.setScore(clan == null ? -1 : clan.getRivals().size());
        }

        if ("allies".equals(variable)) {
            replaceEvent.setScore(clan == null ? -1 : clan.getAllies().size());
        }

        if ("members_online".equals(variable)) {
            replaceEvent.setScore(clan == null ? -1 : clan.getOnlineMembers().size());
        }

        if ("allies_total".equals(variable)) {
            replaceEvent.setScore(clan == null ? -1 : clan.getAllAllyMembers().size());
        }

        if ("clan_kills".equals(variable)) {
            if (clan == null) {
                replaceEvent.setScore(-1);
            } else {
                final int civilianKills = clan.getTotalCivilian();
                final int neutralKills = clan.getTotalNeutral();
                final int rivalKills = clan.getTotalNeutral();
                //count all kill types
                replaceEvent.setScore(civilianKills + neutralKills + rivalKills);
            }
        }
    }
}
