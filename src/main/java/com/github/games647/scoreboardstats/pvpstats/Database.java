package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.github.games647.scoreboardstats.Language;
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

    private static final Cache<String, PlayerCache> CACHE = CacheBuilder.newBuilder()
            .maximumSize(512)
            .expireAfterAccess(Settings.getSaveIntervall(), TimeUnit.MINUTES)
            .removalListener(RemoveListener.getNewInstace(EXECUTOR))
            .build(new CacheLoader<String, PlayerCache>() {
                @Override
                public PlayerCache load(String playerName) {
                    //This shouldn't be called because that can freeze the server
                    ScoreboardStats.getInstance().getLogger().warning(Language.get("synchLoading"));

                    final PlayerStats stats = databaseInstance.find(PlayerStats.class)
                            .where().eq("playername", playerName).findUnique();
                    if (stats == null) {
                        return new PlayerCache();
                    }

                    final int kills = stats.getKills();
                    final int deaths = stats.getDeaths();
                    final int mobKills = stats.getMobkills();
                    final int killstreak = stats.getKillstreak();
                    return new PlayerCache(kills, mobKills, deaths, killstreak);
                }
            });

    /**
     * Get the cache player stats if they exists and the arguments are valid.
     */
    public static PlayerCache getCacheIfAbsent(Player request) {
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
        final Map<String, PlayerCache> cache = CACHE.asMap();
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
        }
    }

    /*
     * Gets the a map of the best players for a specific category.
     */
    public static Map<String, Integer> getTop() {
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

        return top;
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
            final EbeanServer database = EbeanServerFactory.create(dbConfiguration.getServerConfig());
            Thread.currentThread().setContextClassLoader(previous);

            try {
                //Check if a database is avaible with the requesting datas
                database.find(PlayerStats.class).findRowCount();
            } catch (PersistenceException ex) {
                //Create a new table
                pluginInstance.getLogger().fine(Language.get("debugException", ex));
                pluginInstance.getLogger().info(Language.get("newDatabase"));
                final DdlGenerator gen = ((SpiEbeanServer) database).getDdlGenerator();
                gen.runScript(false, gen.generateCreateDdl());
            }

            databaseInstance = database;
        }
    }

    protected static EbeanServer getDatabaseInstance() {
        return databaseInstance;
    }

    protected static void putIntoCache(String name, PlayerCache cacheObject) {
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
