package com.github.games647.scoreboardstats;

import org.bukkit.entity.Player;

public interface BoardManager {

    /**
     * Adding all players to the refresh queue and loading the player stats if enabled
     */
    void registerAll();

    /**
     * Clear the scoreboard for all players
     */
    void unregisterAll();

    /**
     * Creates a new scoreboard based on the configuration.
     *
     * @param player for who should the scoreboard be set.
     */
    void createScoreboard(Player player);

    void createTopListScoreboard(Player player);

    void onUpdate(Player player);

    void updateVariable(Player player, String variable, int newScore);

    void updateVariable(Player player, String variable, String newScore);

    /**
     * Unregister ScoreboardStats from the player
     *
     * @param player who owns the scoreboard
     */
    void unregister(Player player);

    /**
     * Called if the scoreboard should be updated.
     *
     * @param player for who should the scoreboard be set.
     */
    void sendUpdate(Player player);
}
