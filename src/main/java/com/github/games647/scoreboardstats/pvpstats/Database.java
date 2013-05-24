package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import static com.github.games647.scoreboardstats.ScoreboardStats.getSettings;
import com.github.games647.variables.Data;
import com.github.games647.variables.VariableList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Database {

    private static EbeanServer databaseInstance;
    private static final Map<String, PlayerCache> cache = new ConcurrentHashMap<String, PlayerCache>(10);

    public static void setDatabase(final EbeanServer base) {
        databaseInstance = base;
    }

    public static PlayerCache getCache(final String name) {
        return cache.get(name);
    }

    public static void clearTable() {

    }

    public static void loadAccount(final String name) {
        final PlayerStats stats = databaseInstance.find(PlayerStats.class).where().eq(Data.STATS_NAME, name).findUnique();

        cache.put(name, (stats == null) ? new PlayerCache() : new PlayerCache(stats.getKills(), stats.getMobkills(), stats.getDeaths(), stats.getKillstreak()));
    }

    public static int getKdr(final String name) {
        final PlayerCache stats = getCache(name);

        return (stats == null) ? 0 : stats.getDeaths() == 0
                ? stats.getKills() : stats.getKills() / stats.getDeaths();
    }

    public static void saveAccount(final String name, final boolean remove) {
        final PlayerCache playercache = cache.get(name);

        if (playercache == null) {
            return;
        } else if (remove) {
            cache.remove(name);
        }

        if (playercache.getKills() == 0
                && playercache.getDeaths() == 0
                && playercache.getMob() == 0) { //There are no need to save these data
            return;
        }

        PlayerStats stats = databaseInstance.find(PlayerStats.class).where().eq(Data.STATS_NAME, name).findUnique();

        if (stats == null) {
            stats = new PlayerStats();
            stats.setPlayername(name);
        }

        stats.setDeaths(playercache.getDeaths());
        stats.setKills(playercache.getKills());
        stats.setMobkills(playercache.getMob());
        stats.setKillstreak(playercache.getStreak());
        databaseInstance.save(stats);
    }

    public static void saveAll() {
        for (final String playername : cache.keySet()) {
            saveAccount(playername, false);
        }

        cache.clear();
    }

    public static Map<String, Integer> getTop() {
        final String type = getSettings().getTopType();
        final Map<String, Integer> top = new ConcurrentHashMap<String, Integer>(getSettings().getTopitems());

        if (VariableList.KILLSTREAK.equals(type)) {
            for (final PlayerStats stats : databaseInstance.find(PlayerStats.class).orderBy(Data.ODER_KILLSTREAK).setMaxRows(getSettings().getTopitems()).findList()) {
                top.put(stats.getPlayername(), stats.getKillstreak());
            }
        } else if (VariableList.MOB.equals(type)) {
            for (final PlayerStats stats : databaseInstance.find(PlayerStats.class).orderBy(Data.ODER_MOB).setMaxRows(getSettings().getTopitems()).findList()) {
                top.put(stats.getPlayername(), stats.getMobkills());
            }
        } else {
            for (final PlayerStats stats : databaseInstance.find(PlayerStats.class).orderBy(Data.ODER_KILL).setMaxRows(getSettings().getTopitems()).findList()) {
                top.put(stats.getPlayername(), stats.getKills());
            }
        }

        return top;
    }
}
