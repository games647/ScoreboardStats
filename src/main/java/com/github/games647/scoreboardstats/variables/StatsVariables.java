package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;

import org.bukkit.entity.Player;

/**
 * Replace the self tracking stats variables
 */
public class StatsVariables implements Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        if (!Settings.isPvpStats()) {
            return UNKOWN_VARIABLE;
        }

        //Null if the stats aren't loaded yet
        final PlayerStats stats = Database.getCachedStats(player);

        if ("%kills%".equals(variable)) {
            return stats == null ? -1 : stats.getKills();
        }

        if ("%deaths%".equals(variable)) {
            return stats == null ? -1 : stats.getDeaths();
        }

        if ("%mob%".equals(variable)) {
            return stats == null ? -1 : stats.getMobkills();
        }

        if ("%kdr%".equals(variable)) {
            return stats == null ? -1 : stats.getKdr();
        }

        if ("%killstreak%".equals(variable)) {
            return stats == null ? -1 : stats.getKillstreak();
        }

        if ("%current_streak%".equals(variable)) {
            return stats == null ? -1 : stats.getLaststreak();
        }

        return UNKOWN_VARIABLE;
    }
}
