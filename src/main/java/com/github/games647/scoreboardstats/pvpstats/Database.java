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
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

/**
 * This represents a handler for saving player stats.
 */
public final class Database {

    private static EbeanServer databaseInstance;
    private static DatabaseConfiguration dbConfiguration;

    private static final ExecutorService EXECUTOR = Executors
            .newSingleThreadExecutor(new ThreadFactoryBuilder()
                    .setNameFormat("ScoreboardStats-Saver").build());

    private static final Cache<String, PlayerStats> CACHE = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .maximumSize(256)
            //Only two threads access this map
            .concurrencyLevel(2)
            .expireAfterAccess(Settings.getSaveIntervall(), TimeUnit.MINUTES)
            .removalListener(RemoveListener.newInstace(EXECUTOR))
            .build(new CacheLoader<String, PlayerStats>() {

                @Override
                public PlayerStats load(String playerName) {
                    //This shouldn't be called because that can freeze the server
                    Logger.getLogger("ScoreboardStats").warning(Lang.get("synchLoading"));

                    PlayerStats stats = databaseInstance.find(PlayerStats.class, playerName);
                    if (stats == null) {
                        stats = new PlayerStats();
                        stats.setPlayername(playerName);
                        return stats;
                    }

                    return stats;
                }
            });

    /**
     * Get the cache player stats if they exists and the arguments are valid.
     *
     * @param request the associated player
     * @return the stats if they are in the cache
     */
    public static PlayerStats getCachedStats(Player request) {
        if (Settings.isPvpStats() && request != null) {
            final String playerName = request.getName();
            if (CACHE.asMap().containsKey(playerName)) {
                return CACHE.getUnchecked(playerName);
            }
        }

        return null;
    }

    /**
     * Starts loading the stats from a specific player in an external thread.
     *
     * @param player the associated player
     * @see StatsLoader
     */
    public static void loadAccount(Player player) {
        if (player != null && Settings.isPvpStats() && databaseInstance != null) {
            final String playerName = player.getName();
            if (!CACHE.asMap().containsKey(playerName)) {
                EXECUTOR.execute(new StatsLoader(playerName));
            }
        }
    }

    /**
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
                Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Get the a map of the best players for a specific category.
     *
     * @return a iterable of the entries
     */
    public static Iterable<Map.Entry<String, Integer>> getTop() {
        //Get the top players for a specific type
        final String type = Settings.getTopType();
        final Map<String, Integer> top = Maps.newHashMapWithExpectedSize(Settings.getTopitems());
        if ("%killstreak%".equals(type)) {
            for (PlayerStats stats : getTopList("killstreak")) {
                top.put(stats.getPlayername(), stats.getKillstreak());
            }
        } else if ("%mob%".equals(type)) {
            for (PlayerStats stats : getTopList("mobkills")) {
                top.put(stats.getPlayername(), stats.getMobkills());
            }
        } else {
            for (PlayerStats stats : getTopList("kills")) {
                top.put(stats.getPlayername(), stats.getKills());
            }
        }

        return Iterables.unmodifiableIterable(top.entrySet());
    }

    /**
     * Initialize a components and checking for an existing database
     *
     * @param plugin the scoreboardstats instance
     */
    public static void setupDatabase(ScoreboardStats plugin) {
        //Check if pvpstats should be enabled
        if (Settings.isPvpStats()) {
            try {
                dbConfiguration = new DatabaseConfiguration(plugin);
                dbConfiguration.loadConfiguration();

                final ClassLoader previous = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(plugin.getClassLoaderBypass());

                //Disable the class caching temporialy, because after a reload (with plugin file replacement) it still reference to the old file
                if (ReloadFixLoader.changeClassCache(false)) {
                    final EbeanServer database = EbeanServerFactory.create(dbConfiguration.getServerConfig());

                    ReloadFixLoader.changeClassCache(true);
                    Thread.currentThread().setContextClassLoader(previous);

                    final DdlGenerator gen = ((SpiEbeanServer) database).getDdlGenerator();
                    gen.runScript(false, gen.generateCreateDdl().replace("table", "table IF NOT EXISTS"));

                    databaseInstance = database;
                }
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Invalid configuration:", ex);
            }
        }
    }

    /**
     * Get the database instance.
     *
     * @return the database instance
     */
    public static EbeanServer getDatabaseInstance() {
        return databaseInstance;
    }

    /**
     * Put an entry into the cache.
     *
     * @param name the player name
     * @param cacheObject the cache object
     */
    protected static void putIntoCache(String name, PlayerStats cacheObject) {
        CACHE.asMap().put(name, cacheObject);
    }

    private static Iterable<PlayerStats> getTopList(String type) {
        if (databaseInstance == null) {
            return Collections.emptyList();
        }

        return databaseInstance.find(PlayerStats.class)
                .order(type + " desc")
                .select("playername, " + type)
                .setMaxRows(Settings.getTopitems())
                .setBufferFetchSizeHint(Settings.getTopitems())
                .findList();
    }

    private Database() {
        //Singleton
    }
}
