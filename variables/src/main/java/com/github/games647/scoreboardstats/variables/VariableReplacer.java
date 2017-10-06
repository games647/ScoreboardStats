package com.github.games647.scoreboardstats.variables;

import org.bukkit.entity.Player;

/**
 * Represents a replacer to update display names or integer values of the
 * scoreboard.
 */
@FunctionalInterface
public interface VariableReplacer {

    /**
     * Called every time ScoreboardStats wants to update a variable
     *
     * @param player the player who owns the scoreboard or null if global
     * @param variable the variable <b>without the variable identifiers (%)</b>
     * @param replaceEvent the event to set the new values
     */
    void onReplace(Player player, String variable, ReplaceEvent replaceEvent);
}
