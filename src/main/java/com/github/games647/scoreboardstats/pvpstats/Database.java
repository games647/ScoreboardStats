package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ReloadFixLoader;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.Settings;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * This represents a handler for saving player stats.
 *
 * @see EbeanServer
 */
public final class Database {

    private static final String METAKEY = "player_stats";
    private static final ExecutorService EXECUTOR = Executors
            //SQL transactions are mainly blocking so there is no need to update them smooth
            .newSingleThreadExecutor(new ThreadFactoryBuilder()
                    //Give the thread a name so we can find them
                    .setNameFormat("ScoreboardStats-Database").build());

    private static boolean uuidUse;

    private static Plugin instance;

    private static EbeanServer databaseInstance;
    private static DatabaseConfiguration dbConfiguration;

    /**
     * Get the cache player stats if they exists and the arguments are valid.
     *
     * @param request the associated player
     * @return the stats if they are in the cache
     */
    public static PlayerStats getCachedStats(Player request) {
        if (Settings.isPvpStats() && request != null) {
            final List<MetadataValue> metadata = request.getMetadata(METAKEY);
            if (metadata != null && !metadata.isEmpty()) {
                return (PlayerStats) metadata.get(0).value();
            }
        }

        return null;
    }

    /**
     * Starts loading the stats for a specific player in an external thread.
     *
     * @param player the associated player
     */
    public static void loadAccountAsync(Player player) {
        if (getCachedStats(player) == null && databaseInstance != null) {
            EXECUTOR.execute(new StatsLoader(instance, uuidUse, player));
        }
    }

    /**
     * Starts loading the stats for a specific player sync
     *
     * @param uniqueId the associated playername or uuid
     * @return the loaded stats
     */
    public static PlayerStats loadAccount(Object uniqueId) {
        if (uniqueId == null || databaseInstance == null) {
            return null;
        } else {
            PlayerStats stats = databaseInstance.find(PlayerStats.class)
                    .where().eq(uuidUse ? "uuid" : "playername", uniqueId).findUnique();

            //If there are no existing stat create a new cache object with empty stuff
            if (stats == null) {
                stats = new PlayerStats();
            }

            return stats;
        }
    }

    /**
     * Save PlayerStats async.
     *
     * @param stats PlayerStats data
     */
    public static void saveAsync(final PlayerStats stats) {
        EXECUTOR.submit(new Runnable() {

            @Override
            public void run() {
                save(stats);
            }
        });
    }

    /**
     * Save the PlayerStats on the current Thread.
     *
     * @param stats PlayerStats data
     */
    public static void save(PlayerStats stats) {
        if (stats != null && databaseInstance != null) {
            //Save the stats to the database
            databaseInstance.save(stats);
        }
    }

    /**
     * Starts saving all cache player stats and then clears the cache.
     */
    public static void saveAll() {
        if (Settings.isPvpStats()) {
            //If pvpstats are enabled save all stats that are in the cache
            for (Player player : Bukkit.getOnlinePlayers()) {
                //maybe batch this
                final List<MetadataValue> metadata = player.getMetadata(METAKEY);
                //can be null if that metadata doesn't exist
                if (metadata != null) {
                    for (MetadataValue metadataValue : metadata) {
                        if (metadataValue.getOwningPlugin().equals(instance)) {
                            //just remove our metadata
                            metadataValue.invalidate();
                        }
                    }
                }

                player.removeMetadata(METAKEY, instance);
            }

            EXECUTOR.shutdown();
            try {
                instance.getLogger().info(Lang.get("savingStats"));
                EXECUTOR.awaitTermination(15, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                instance.getLogger().log(Level.SEVERE, null, ex);
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

        //shouldn't be modifed, because it cause no effect
        return top.entrySet();
    }

    /**
     * Initialize a components and checking for an existing database
     *
     * @param plugin the ScoreboardStats instance
     */
    public static void setupDatabase(ScoreboardStats plugin) {
        instance = plugin;

        //Check if pvpstats should be enabled
        if (Settings.isPvpStats()) {
            dbConfiguration = new DatabaseConfiguration(plugin);
            dbConfiguration.loadConfiguration();
            uuidUse = dbConfiguration.isUuidUse();

            final ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(plugin.getClassLoaderBypass());

            //Disable the class caching temporialy, because after a reload
            //(with plugin file replacement) it still reference to the old file
            ReloadFixLoader.changeClassCache(false);

            try {
                final EbeanServer database = EbeanServerFactory.create(dbConfiguration.getServerConfig());
                final DdlGenerator gen = ((SpiEbeanServer) database).getDdlGenerator();
                gen.runScript(false, gen.generateCreateDdl().replace("table", "table IF NOT EXISTS"));
                //only create the table if it doesn't exist

                final DatabaseConverter converter = new DatabaseConverter(database);
                converter.convertNewDatabaseSystem();

                databaseInstance = database;
            } catch (Exception ex) {
                instance.getLogger().log(Level.WARNING, "Error creating database", ex);
                //this also excludes null values
                if (ex.getCause() instanceof ClassNotFoundException) {
                    instance.getLogger().info("Probarly because of missing drivers");
                    if ("Glowstone".equalsIgnoreCase(Bukkit.getName())) {
                        instance.getLogger().info("You're running Glowstone. Take a look here: \n"
                                + "https://github.com/GlowstoneMC/Glowstone/wiki/Library-Management");
                    }
                }
            }

            ReloadFixLoader.changeClassCache(true);
            Thread.currentThread().setContextClassLoader(previous);
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

    private static Iterable<PlayerStats> getTopList(String type) {
        if (databaseInstance == null) {
            return Collections.emptyList();
        }

        return databaseInstance.find(PlayerStats.class)
                .order(type + " desc")
                //we only need the name
                .select("playername")
                .setMaxRows(Settings.getTopitems())
                //we won't use more of it at once
                .setBufferFetchSizeHint(Settings.getTopitems())
                .findList();
    }

    private Database() {
        //Singleton
    }
}
