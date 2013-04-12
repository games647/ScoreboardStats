package me.games647.scoreboardstats.api.pvpstats;

import com.avaje.ebean.EbeanServer;
import java.util.HashMap;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;

public final class Database {

    private static EbeanServer databaseinstance;
    private static HashMap<String, Cache> cache = new HashMap<String, Cache>();

    public static void setDatabase(final EbeanServer base) {
        Database.databaseinstance = base;
    }

    public static Cache getCache(final String name) {
        return cache.get(name);
    }

    public static void loadAccount(final String name) {
        final PlayerStats stats = databaseinstance.find(PlayerStats.class).where().eq("playername", name).findUnique();
        Cache playercache;
        if (stats == null) {
            playercache = new Cache();
        } else {
            playercache = new Cache(stats.getKills(), stats.getMobkills(), stats.getDeaths());
        }
        cache.put(name, playercache);
    }

    public static int getKdr(final String name) {
        final Cache stats = getCache(name);

        if (stats.getDeaths() == 0) {
            return stats.getKills();
        }

        return stats.getKills() / stats.getDeaths();
    }

    public static void saveAccount(final String name) {
        PlayerStats stats = databaseinstance.find(PlayerStats.class).where().eq("playername", name).findUnique();
        if (stats == null) {
            stats = new PlayerStats();
            stats.setPlayername(name);
        }
        final Cache playercache = cache.get(name);
        cache.remove(name);
        stats.setDeaths(playercache.getDeaths());
        stats.setKills(playercache.getKills());
        stats.setMobkills(playercache.getMob());
        databaseinstance.save(stats);
    }

    public static HashMap<String, Integer> getTop() {
        final java.util.List<PlayerStats> list = databaseinstance.find(PlayerStats.class).orderBy("kills asc").setMaxRows(getSettings().getTopitems()).findList();
        final HashMap<String, Integer> top = new HashMap<String, Integer>(getSettings().getTopitems());
        for (int i = 0; i < list.size(); i++) {
            final PlayerStats stats = list.get(i);
            top.put(stats.getPlayername(), stats.getKills());
        }
        return top;
    }
}
