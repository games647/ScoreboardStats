package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;

import org.bukkit.entity.Player;

public class StatsVariables implements ReplaceManager.Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        final PlayerCache cache = Database.getCacheIfAbsent(player);
        //Null if the stats aren't loaded yet
        if (!Settings.isPvpStats()) {
            return UNKOWN_VARIABLE;
        }

        if ("%kills%".equals(variable)) {
            return cache == null ? -1 : cache.getKills();
        }

        if ("%deaths%".equals(variable)) {
            return cache == null ? -1 : cache.getDeaths();
        }

        if ("%mob%".equals(variable)) {
            return cache == null ? -1 : cache.getMob();
        }

        if ("%kdr%".equals(variable)) {
            return cache == null ? -1 : cache.getKdr();
        }

        if ("%killstreak%".equals(variable)) {
            return cache == null ? -1 : cache.getHighestStreak();
        }

        if ("%current_streak%".equals(variable)) {
            return cache == null ? -1 : cache.getLaststreak();
        }

        return UNKOWN_VARIABLE;
    }
}
