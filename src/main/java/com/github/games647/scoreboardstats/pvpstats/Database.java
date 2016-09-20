package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.github.games647.scoreboardstats.BackwardsCompatibleUtil;
import com.github.games647.scoreboardstats.ReloadFixLoader;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.config.Settings;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

/**
 * This represents a handler for saving player stats.
 *
 * @see EbeanServer
 */
public class Database {

    private static final String METAKEY = "player_stats";

    private final ScoreboardStats plugin;

    private final ScheduledExecutorService executor;

    private final Map<String, Integer> toplist = Maps.newHashMapWithExpectedSize(Settings.getTopitems());

    private final DatabaseConfiguration dbConfig;
    private EbeanServer ebeanConnection;

    public Database(ScoreboardStats plugin) {
        this.plugin = plugin;
        this.dbConfig = new DatabaseConfiguration(plugin);

        //SQL transactions are mainly blocking so there is no need to update them smooth
        executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                //Give the thread a name so we can find them
                .setNameFormat(plugin.getName() + "-Database").build());
    }

    /**
     * Get the database instance.
     *
     * @return the database instance
     */
    public EbeanServer getDatabaseInstance() {
        return ebeanConnection;
    }

    /**
     * Get the cache player stats if they exists and the arguments are valid.
     *
     * @param request the associated player
     * @return the stats if they are in the cache
     */
    public PlayerStats getCachedStats(Player request) {
        if (request != null) {
            for (MetadataValue metadata : request.getMetadata(METAKEY)) {
                if (metadata.value() instanceof PlayerStats) {
                    return (PlayerStats) metadata.value();
                }
            }
        }

        return null;
    }

    /**
     * Starts loading the stats for a specific player in an external thread.
     *
     * @param player the associated player
     */
    public void loadAccountAsync(Player player) {
        if (getCachedStats(player) == null && ebeanConnection != null) {
            executor.execute(new StatsLoader(plugin, dbConfig.isUuidUse(), player, this));
        }
    }

    /**
     * Starts loading the stats for a specific player sync
     *
     * @param uniqueId the associated playername or uuid
     * @return the loaded stats
     */
    public PlayerStats loadAccount(Object uniqueId) {
        if (uniqueId == null || ebeanConnection == null) {
            return null;
        } else {
            PlayerStats stats = ebeanConnection.find(PlayerStats.class)
                    .where().eq(dbConfig.isUuidUse() ? "uuid" : "playername", uniqueId).findUnique();

            if (stats == null) {
                //If there are no existing stat create a new object with empty stats
                stats = new PlayerStats();
            }

            return stats;
        }
    }

    /**
     * Starts loading the stats for a specific player sync
     *
     * @param player the associated player
     * @return the loaded stats
     */
    public PlayerStats loadAccount(Player player) {
        if (player == null || ebeanConnection == null) {
            return null;
        } else {
            if (dbConfig.isUuidUse()) {
                return loadAccount(player.getUniqueId());
            } else {
                return loadAccount(player.getName());
            }
        }
    }

    /**
     * Save PlayerStats async.
     *
     * @param stats PlayerStats data
     */
    public void saveAsync(PlayerStats stats) {
        executor.submit(() -> ebeanConnection.save(stats));
    }

    /**
     * Save the PlayerStats on the current Thread.
     *
     * @param stats PlayerStats data
     */
    public void save(PlayerStats stats) {
        if (stats != null && ebeanConnection != null) {
            //Save the stats to the database
            if (stats.isSaved()) {
                ebeanConnection.update(stats);
            } else {
                ebeanConnection.insert(stats);
            }

            stats.setSaved();
        }
    }

    /**
     * Starts saving all cache player stats and then clears the cache.
     */
    public void saveAll() {
        try {
            plugin.getLogger().info(Lang.get("savingStats"));

            //If pvpstats are enabled save all stats that are in the cache
            List<PlayerStats> toSave = BackwardsCompatibleUtil.getOnlinePlayers().stream()
                    .map(this::getCachedStats)
                    .collect(Collectors.toList());

            ebeanConnection.save(toSave);
            executor.shutdown();

            executor.awaitTermination(15, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save the stats to the database", ex);
        } finally {
            //Make rally sure we remove all even on error
            BackwardsCompatibleUtil.getOnlinePlayers().stream()
                    .forEach(player -> player.removeMetadata(METAKEY, plugin));
        }
    }

    /**
     * Initialize a components and checking for an existing database
     */
    public void setupDatabase() {
        //Check if pvpstats should be enabled
        dbConfig.loadConfiguration();

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(plugin.getClassLoaderBypass());

        //Disable the class caching temporialy, because after a reload
        //(with plugin file replacement) it still reference to the old file
        ReloadFixLoader.setClassCache(false);

        try {
            EbeanServer database = EbeanServerFactory.create(dbConfig.getServerConfig());
            DdlGenerator gen = ((SpiEbeanServer) database).getDdlGenerator();
            //only create the table if it doesn't exist
            gen.runScript(false, gen.generateCreateDdl().replace("table", "table IF NOT EXISTS"));

            ebeanConnection = database;
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Error creating database", ex);
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
            ReloadFixLoader.setClassCache(true);
        }

        executor.scheduleWithFixedDelay(this::updateTopList, 0, 5, TimeUnit.MINUTES);

        executor.scheduleWithFixedDelay(() -> {
            if (ebeanConnection == null) {
                return;
            }

            Future<Collection<? extends Player>> syncPlayers = Bukkit.getScheduler()
                        .callSyncMethod(plugin, BackwardsCompatibleUtil::getOnlinePlayers);

            try {
                Collection<? extends Player> onlinePlayers = syncPlayers.get();

                List<PlayerStats> toSave = onlinePlayers.stream()
                    .map(this::getCachedStats)
                    .collect(Collectors.toList());

                toSave.stream().filter(stats -> !stats.isSaved()).forEach(ebeanConnection::insert);
                toSave.stream().filter(PlayerStats::isSaved).forEach(ebeanConnection::update);

                toSave.forEach(PlayerStats::setSaved);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }, 0, 1, TimeUnit.MINUTES);

        registerEvents();
    }

    /**
     * Get the a map of the best players for a specific category.
     *
     * @return a iterable of the entries
     */
    public Collection<Map.Entry<String, Integer>> getTop() {
        synchronized (toplist) {
            return toplist.entrySet();
        }
    }

    /**
     * Updates the toplist
     */
    public void updateTopList() {
        String type = Settings.getTopType();
        Map<String, Integer> newToplist;
        switch (type) {
            case "killstreak":
                newToplist = getTopList("killstreak").collect(Collectors.toMap(
                        PlayerStats::getPlayername,
                        PlayerStats::getKillstreak
                ));

                break;
            case "mob":
                newToplist = getTopList("mobkills").collect(Collectors.toMap(
                        PlayerStats::getPlayername,
                        PlayerStats::getMobkills
                ));

                break;
            default:
                newToplist = getTopList("kills").collect(Collectors.toMap(
                        PlayerStats::getPlayername,
                        PlayerStats::getKills
                ));

                break;
        }

        synchronized (toplist) {
            //set it after fetching so it's only blocking for a short time
            toplist.clear();
            toplist.putAll(newToplist);
        }
    }

    private Stream<PlayerStats> getTopList(String type) {
        if (ebeanConnection == null) {
            return Stream.empty();
        }

        return ebeanConnection.find(PlayerStats.class)
                .order(type + " desc")
                //we only need the name
                .select("playername")
                .setMaxRows(Settings.getTopitems())
                //we won't use more of it at once
                .setBufferFetchSizeHint(Settings.getTopitems())
                .findList().stream();
    }

    private void registerEvents() {
        if (Bukkit.getPluginManager().isPluginEnabled("InSigns")) {
            //Register this listerner if InSigns is available
            new SignListener(plugin, "[Kill]", this);
            new SignListener(plugin, "[Death]", this);
            new SignListener(plugin, "[KDR]", this);
            new SignListener(plugin, "[Streak]", this);
            new SignListener(plugin, "[Mob]", this);
        }

        plugin.getReplaceManager().register(new StatsVariables(plugin, this));
        Bukkit.getPluginManager().registerEvents(new StatsListener(plugin, this), plugin);
    }
}
