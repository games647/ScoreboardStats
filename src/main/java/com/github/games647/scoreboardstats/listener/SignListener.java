package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;

import de.blablubbabc.insigns.SimpleChanger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Replace some variables on signs with the player individual stats.
 * The variables will be replaced dynamically
 *
 * @see SignSendEvent
 * @see Database
 */
public class SignListener extends SimpleChanger {

    private final String variable;

    public SignListener(Plugin plugin, String key) {
        super(plugin, key, "scoreboardstats.sign");

        this.variable = key.replace("[", "").replace("]", "");
    }

    @Override
    public String getValue(Player player, Location lctn, String string) {
        final PlayerStats playerCache = Database.getCachedStats(player);
        if (playerCache == null) {
            //The stats aren't loaded yet
            return "Not loaded";
        }

        if ("Kill".equals(variable)) {
            return Integer.toString(playerCache.getKills());
        } else if ("Death".equals(variable)) {
            return Integer.toString(playerCache.getDeaths());
        } else if ("KDR".equals(variable)) {
            return Integer.toString(playerCache.getKdr());
        } else if ("Streak".equals(variable)) {
            return Integer.toString(playerCache.getKillstreak());
        }

        return Integer.toString(playerCache.getMobkills());
    }
}
