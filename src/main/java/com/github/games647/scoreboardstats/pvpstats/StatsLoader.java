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
        final PlayerStats stats = Database.getDatabaseInstance()
                .find(PlayerStats.class).where().eq("playername", playerName)
                .findUnique();
        if (stats == null) {
            //If there are no existing stat create a new cache object with empty stuff
            Database.putIntoCache(playerName, new PlayerCache());
        } else {
            final int kills = stats.getKills();
            final int deaths = stats.getDeaths();
            final int mobKills = stats.getMobkills();
            final int killstreak = stats.getKillstreak();
            Database.putIntoCache(playerName, new PlayerCache(kills, mobKills, deaths, killstreak));
        }
    }
}
