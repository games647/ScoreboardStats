package com.github.games647.scoreboardstats.variables;

import org.bukkit.entity.Player;

/**
 * Represents a variable replacer
 */
public interface Replaceable {

    //todo find another method to prevent conflicts
    /**
     * Represents an unknown variable
     */
    int UNKOWN_VARIABLE = -1337;

    /**
     * Get the score for specific variable
     *
     * @param player the associated player
     * @param variable the variable
     * @return the score
     */
    int getScoreValue(Player player, String variable);
}
