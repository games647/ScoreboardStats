package com.github.games647.scoreboardstats.pvpstats;

import lombok.ToString;

/**
 * This class is used for loading the player stats.
 */
@ToString
public class StatsLoader implements Runnable {

    private final String playerName;

    /**
     * Creates a new loader for a specific player
     *
     * @param playerName the associated player for the stats
     */
    public StatsLoader(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void run() {
        PlayerStats stats = Database.getDatabaseInstance().find(PlayerStats.class, playerName);

        //If there are no existing stat create a new cache object with empty stuff
        if (stats == null) {
            stats = new PlayerStats(playerName);
        }

        Database.putIntoCache(playerName, stats);
    }
}
