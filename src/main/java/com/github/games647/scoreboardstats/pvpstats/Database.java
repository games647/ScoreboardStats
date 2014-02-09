package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ReloadFixLoader;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.Settings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.entity.Player;

/**
 * This represents a handler for saving player stats.
 */
public final class Database {

    private static EbeanServer databaseInstance;
    private static DatabaseConfiguration dbConfiguration;
    //We are using a mysql databse so we can use only one thread for saving because of locks
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private static final Cache<String, PlayerStats> CACHE = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .maximumSize(256)
            .expireAfterAccess(Settings.getSaveIntervall(), TimeUnit.MINUTES)
            .removalListener(RemoveListener.newInstace(EXECUTOR))
            .build(new CacheLoader<String, PlayerStats>() {

                @Override
                public PlayerStats load(String playerName) {
                    //This shouldn't be called because that can freeze the server
                    ScoreboardStats.getInstance().getLogger().warning(Lang.get("synchLoading"));

                    final PlayerStats stats = databaseInstance.find(PlayerStats.class)
                            .where().eq("playername", playerName).findUnique();
                    if (stats == null) {
                        return new PlayerStats();
                    }

                    return stats;
                }
            });

    /**
     * Get the cache player stats if they exists and the arguments are valid.
     */
    public static PlayerStats getCacheIfAbsent(Player request) {
        if (request != null && Settings.isPvpStats()) {
            final String playerName = request.getName();
            if (CACHE.asMap().containsKey(playerName)) {
                return CACHE.getUnchecked(playerName);
            }
        }

        return null;
    }

    /**
     * Starts loading the stats from a specific player in an external thread.
     */
    public static void loadAccount(String name) {
        final Map<String, PlayerStats> cache = CACHE.asMap();
        if (!Settings.isPvpStats() || cache.containsKey(name)) {
            return;
        }

        EXECUTOR.execute(new StatsLoader(name));
    }

    /*
     * Starts saving all cache player stats and then clears the cache.
     */
    public static void saveAll() {
        if (Settings.isPvpStats()) {
            //If pvpstats are enabled save all stats that are in the cache
            CACHE.invalidateAll();
            EXECUTOR.shutdown();
            try {
                Logger.getLogger("ScoreboardStats").info(Lang.get("savingStats"));
                EXECUTOR.awaitTermination(3, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                Logger.getLogger("ScoreboardStats")
                        .severe(Lang.get("debugException", ex));
            }
        }
    }

    /*
     * Gets the a map of the best players for a specific category.
     */
    public static Iterable<Map.Entry<String, Integer>> getTop() {
        //Get the top players for a specific type
        final String type = Settings.getTopType();
        final Map<String, Integer> top = new HashMap<String, Integer>(Settings.getTopitems());
        if ("%killstreak%".equals(type)) {
            for (PlayerStats stats : getTopList("killstreak desc")) {
                top.put(stats.getPlayername(), stats.getKillstreak());
            }
        } else if ("%mob%".equals(type)) {
            for (PlayerStats stats : getTopList("mobkills desc")) {
                top.put(stats.getPlayername(), stats.getMobkills());
            }
        } else {
            for (PlayerStats stats : getTopList("kills desc")) {
                top.put(stats.getPlayername(), stats.getKills());
            }
        }

        return top.entrySet();
    }

    /*
     * Initialize a components and checking for an existing database
     */
    public static void setupDatabase(ScoreboardStats pluginInstance) {
        //Check if pvpstats should be enabled
        if (Settings.isPvpStats()) {
            dbConfiguration = new DatabaseConfiguration(pluginInstance);
            dbConfiguration.loadConfiguration();

            final ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(pluginInstance.getClassLoaderBypass());

            if (ReloadFixLoader.changeClassCache(false)) {
                final EbeanServer database = EbeanServerFactory.create(dbConfiguration.getServerConfig());

                ReloadFixLoader.changeClassCache(true);
                Thread.currentThread().setContextClassLoader(previous);

                try {
                    //Check if a database is avaible with the requesting datas
                    database.find(PlayerStats.class).findRowCount();
                } catch (PersistenceException ex) {
                    //Create a new table
                    pluginInstance.getLogger().fine(Lang.get("debugException", ex));
                    pluginInstance.getLogger().info(Lang.get("newDatabase"));
                    final DdlGenerator gen = ((SpiEbeanServer) database).getDdlGenerator();
                    gen.runScript(false, gen.generateCreateDdl());
                }

                databaseInstance = database;
            }
        }
    }

    protected static EbeanServer getDatabaseInstance() {
        return databaseInstance;
    }

    protected static void putIntoCache(String name, PlayerStats cacheObject) {
        //delegate
        CACHE.asMap().put(name, cacheObject);
    }

    private static Iterable<PlayerStats> getTopList(String order) {
        return databaseInstance.find(PlayerStats.class).orderBy(order)
                .setMaxRows(Settings.getTopitems()).findList();
    }

    private Database() {
        //Singleton
    }
}
