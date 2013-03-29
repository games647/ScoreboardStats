package me.games647.gscoreboard.api;

import com.avaje.ebean.EbeanServer;

public final class Database {

    private static EbeanServer database;

    public static void setDatabase(EbeanServer database) {
        Database.database = database;
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

    public static int increase(final String name, final boolean type) {
        final PlayerStats stats = checkAccount(name);
        if (type) {
            stats.setKills(stats.getKills() + 1);
            database.save(stats);
            return stats.getKills();
        } else {
            stats.setDeaths(stats.getDeaths() + 1);
            database.save(stats);
            return stats.getDeaths();
        }
    }
}
