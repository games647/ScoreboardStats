package com.github.games647.scoreboardstats.variables;

import org.bukkit.entity.Player;

/**
 * Represents a variable replacer
 *
 * @deprecated not fully featured and returns magic values. Will be removed in future versions
 */
@Deprecated
public interface Replaceable {

    /**
     * Represents an unknown variable
     *
     * @deprecated Magic value
     */
    @Deprecated
    int UNKOWN_VARIABLE = -1337;

    /**
     * Get the score for specific variable
     *
     * @param player the associated player
     * @param variable the variable
     * @return the score
     */
    @Deprecated
    int getScoreValue(Player player, String variable);
}
