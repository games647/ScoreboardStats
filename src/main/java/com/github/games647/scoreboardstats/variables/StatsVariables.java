package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;

import org.bukkit.entity.Player;

public class StatsVariables implements ReplaceManager.Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        final PlayerStats stats = Database.getCacheIfAbsent(player);
        //Null if the stats aren't loaded yet
        if (!Settings.isPvpStats()) {
            return UNKOWN_VARIABLE;
        }

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
