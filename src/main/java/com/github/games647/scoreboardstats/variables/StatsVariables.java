package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;

import org.bukkit.entity.Player;

public class StatsVariables implements ReplaceManager.Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        final String playerName = player.getName();
        final PlayerCache cache = Database.getCacheIfAbsent(playerName);
        if (cache == null) {
            return -1;
        }

        if ("%kills%".equals(variable)) {
            return cache.getKills();
        }

        if ("%deaths%".equals(variable)) {
            return cache.getDeaths();
        }

        if ("%mob%".equals(variable)) {
            return cache.getMob();
        }

        if ("%kdr%".equals(variable)) {
            return Database.getKdr(playerName);
        }

        if ("%killstreak%".equals(variable)) {
            return cache.getStreak();
        }

        if ("%current_streak%".equals(variable)) {
            return cache.getLaststreak();
        }

        return UNKOWN_VARIABLE;
    }
}
