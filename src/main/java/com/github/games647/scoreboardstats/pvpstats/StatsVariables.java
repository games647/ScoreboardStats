package com.github.games647.scoreboardstats.pvpstats;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

import org.bukkit.entity.Player;

/**
 * Replace the self tracking stats variables
 *
 * @see Database
 */
public class StatsVariables extends VariableReplaceAdapter<ScoreboardStats> {

    private final Database statsDatabase;

    public StatsVariables(ScoreboardStats plugin, Database statsDatabase) {
        super(plugin
                , "kills", "deaths", "mob", "kdr", "killstreak", "current_streak");

        this.statsDatabase = statsDatabase;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        PlayerStats stats = statsDatabase.getCachedStats(player);
        replaceEvent.setConstant(true);
        if (stats == null) {
            //Null if the stats aren't loaded yet
            return;
        }

        if ("kills".equals(variable)) {
            replaceEvent.setScore(stats.getKills());
        } else if ("deaths".equals(variable)) {
            replaceEvent.setScore(stats.getDeaths());
        } else if ("mob".equals(variable)) {
            replaceEvent.setScore(stats.getMobkills());
        } else if ("kdr".equals(variable)) {
            replaceEvent.setScore(stats.getKdr());
        } else if ("killstreak".equals(variable)) {
            replaceEvent.setScore(stats.getKillstreak());
        } else if ("current_streak".equals(variable)) {
            replaceEvent.setScore(stats.getLaststreak());
        }
    }
}
