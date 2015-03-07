package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.VariableReplacer;

import org.bukkit.entity.Player;

/**
 * Replace the self tracking stats variables
 *
 * @see Database
 */
public class StatsVariables implements VariableReplacer {

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if (!Settings.isPvpStats()) {
            return;
        }

        //Null if the stats aren't loaded yet
        final PlayerStats stats = Database.getCachedStats(player);

        if ("kills".equals(variable)) {
            replaceEvent.setScore(stats == null ? -1 : stats.getKills());
        }

        if ("deaths".equals(variable)) {
            replaceEvent.setScore(stats == null ? -1 : stats.getDeaths());
        }

        if ("mob".equals(variable)) {
            replaceEvent.setScore(stats == null ? -1 : stats.getMobkills());
        }

        if ("kdr".equals(variable)) {
            replaceEvent.setScore(stats == null ? -1 : stats.getKdr());
        }

        if ("killstreak".equals(variable)) {
            replaceEvent.setScore(stats == null ? -1 : stats.getKillstreak());
        }

        if ("current_streak".equals(variable)) {
            replaceEvent.setScore(stats == null ? -1 : stats.getLaststreak());
        }
    }
}
