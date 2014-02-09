package com.github.games647.scoreboardstats.pvpstats;

/**
 * This class is used for loading the player stats.
 */
public class StatsLoader implements Runnable {

    private final String playerName;

    /*
     * Creates a new loader for a specific player
     */
    public StatsLoader(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void run() {
        PlayerStats stats = Database.getDatabaseInstance()
                .find(PlayerStats.class).where().eq("playername", playerName)
                .findUnique();

        //If there are no existing stat create a new cache object with empty stuff
        if (stats == null) {
            stats = new PlayerStats();
            stats.setPlayername(playerName);
        }

        Database.putIntoCache(playerName, stats);
    }
}
