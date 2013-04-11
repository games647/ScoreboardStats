package me.games647.scoreboardstats.api.pvpstats;

import me.games647.scoreboardstats.api.pvpstats.PlayerStats;
import com.avaje.ebean.EbeanServer;
import java.util.HashMap;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;

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

    public static void increaseDeaths(final String name) {
        final PlayerStats stats = checkAccount(name);

        stats.setDeaths(stats.getDeaths() + 1);
        database.save(stats);
    }

    public static void increaseKills(final String name) {
        final PlayerStats stats = checkAccount(name);

        stats.setKills(stats.getKills() + 1);
        database.save(stats);
    }

    public static void increaseMobKills(final String name) {
        final PlayerStats stats = checkAccount(name);

        stats.setMobkills(stats.getMobkills() + 1);
        database.save(stats);
    }

    public static int getKdr(final String name) {
        final PlayerStats stats = checkAccount(name);

        if (stats.getDeaths() == 0) {
            return stats.getKills();
        }

        return stats.getKills() / stats.getDeaths();
    }

    public static HashMap<String, Integer> getTop() {
        final java.util.List<PlayerStats> list = database.find(PlayerStats.class).orderBy("kills asc").setMaxRows(getSettings().getTopitems()).findList();
        final HashMap<String, Integer> top = new HashMap<String, Integer>(getSettings().getTopitems());
        for (int i = 0; i < list.size(); i++) {
            final PlayerStats stats = list.get(i);
            top.put(stats.getPlayername(), stats.getKills());
        }
        return top;
    }
}
