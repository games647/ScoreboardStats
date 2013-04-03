package me.games647.scoreboardstats.api;

import com.avaje.ebean.EbeanServer;

public final class Database {

    private static EbeanServer database;

    public static void setDatabase(final EbeanServer base) {
        Database.database = base;
    }

    public static PlayerStats checkAccount(final String name) {
        PlayerStats stats = database.find(PlayerStats.class).where().eq("playername", name).findUnique();
        if (stats == null) {
            stats = new PlayerStats();
            stats.setPlayername(name);
            database.save(stats);
        }
        return stats;
    }

    public static int increaseDeaths(final String name) {
        final PlayerStats stats = checkAccount(name);

        stats.setDeaths(stats.getDeaths() + 1);
        database.save(stats);

        return stats.getDeaths();
    }

    public static int increaseKills(final String name) {
        final PlayerStats stats = checkAccount(name);

        stats.setKills(stats.getKills() + 1);
        database.save(stats);

        return stats.getKills();
    }

    public static int increaseMobKills(final String name) {
        final PlayerStats stats = checkAccount(name);

        stats.setMobkills(stats.getMobkills() + 1);
        database.save(stats);

        return stats.getMobkills();
    }
    //Maybe I'll add more features such as a leaderboard
}
