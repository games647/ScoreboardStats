package com.github.games647.scoreboardstats.variables;

import org.bukkit.entity.Player;

/**
 * Represents a variable replacer
 *
 * @deprecated not fully featured and returns magic values. Will be removed in future versions
 */
public interface Replaceable {

    //todo find another method to prevent conflicts
    /**
     * Represents an unknown variable
     *
     * @deprecated Magic value
     */
    int UNKOWN_VARIABLE = -1337;

    /**
     * If the variable can be updated based on a event. Then the getScoreValue
     * method will be called only on the first replace event
     *
     * @deprecated Magic value
     */
    int ON_EVENT = Integer.MIN_VALUE - 1;

    /**
     * Get the score for specific variable
     *
     * @param player the associated player
     * @param variable the variable
     * @return the score
     */
    int getScoreValue(Player player, String variable);
}
