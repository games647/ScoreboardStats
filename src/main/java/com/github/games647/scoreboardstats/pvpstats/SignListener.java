package com.github.games647.scoreboardstats.pvpstats;

import de.blablubbabc.insigns.SimpleChanger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Replace some variables on signs with the player individual stats.
 * The variables will be replaced dynamically
 *
 * @see Database
 */
public class SignListener extends SimpleChanger {

    private final Database statsDatabase;
    private final String variable;

    public SignListener(Plugin plugin, String key, Database statsDatabase) {
        super(plugin, key, plugin.getName().toLowerCase() + ".sign");

        this.variable = key.replace("[", "").replace("]", "");
        this.statsDatabase = statsDatabase;
    }

    @Override
    public String getValue(Player player, Location lctn, String string) {
        final PlayerStats playerCache = statsDatabase.getCachedStats(player);
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
