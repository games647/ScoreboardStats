package com.github.games647.scoreboardstats.pvp;

import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.Replacer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

/**
 * This represents a handler for saving player stats.
 */
public class Database {

    private static final String METAKEY = "player_stats";

    private final Plugin plugin;
    private final Logger logger;

    private final Map<String, Integer> toplist;
    private final DatabaseConfiguration dbConfig;
    private HikariDataSource dataSource;

    public Database(Plugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;

        this.dbConfig = new DatabaseConfiguration(plugin);
        this.toplist = Maps.newHashMapWithExpectedSize(Settings.getTopitems());
    }

    /**
     * Get the cache player stats if they exists and the arguments are valid.
     *
     * @param request the associated player
     * @return the stats if they are in the cache
     */
    @Deprecated
    public PlayerStats getCachedStats(Player request) {
        return getStats(request).orElse(null);
    }

    public Optional<PlayerStats> getStats(Player request) {
        if (request != null) {
            for (MetadataValue metadata : request.getMetadata(METAKEY)) {
                if (metadata.value() instanceof PlayerStats) {
                    return Optional.of((PlayerStats) metadata.value());
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Starts loading the stats for a specific player in an external thread.
     *
     * @param player the associated player
     */
    public void loadAccountAsync(Player player) {
        if (dataSource != null && !getStats(player).isPresent()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new StatsLoader(plugin, player, this));
        }
    }

    /**
     * Starts loading the stats for a specific player sync
     *
     * @param uniqueId the associated playername or uuid
     * @return the loaded stats
     */
    public Optional<PlayerStats> loadAccount(Object uniqueId) {
        if (uniqueId == null || dataSource == null) {
            return Optional.empty();
        } else {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM player_stats WHERE "
                         + "uuid=?")) {

                stmt.setString(1, uniqueId.toString());
                try (ResultSet resultSet = stmt.executeQuery()) {
                    return Optional.of(extractPlayerStats(resultSet));
                }
            } catch (SQLException ex) {
                logger.error("Error loading player profile", ex);
            }

            return Optional.empty();
        }
    }

    private PlayerStats extractPlayerStats(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);

        String rawUUID = resultSet.getString(2);
        UUID uuid = null;
        if (rawUUID != null) {
            uuid = UUID.fromString(rawUUID);
        }

        String playerName = resultSet.getString(3);

        int kills = resultSet.getInt(4);
        int deaths = resultSet.getInt(5);
        int mobkills = resultSet.getInt(6);
        int killstreak = resultSet.getInt(7);

        Instant lastOnline = resultSet.getTimestamp(8).toInstant();
        return new PlayerStats(id, uuid, playerName, kills, deaths, mobkills, killstreak, lastOnline);
    }

    /**
     * Starts loading the stats for a specific player sync
     *
     * @param player the associated player
     * @return the loaded stats
     */
    public Optional<PlayerStats> loadAccount(Player player) {
        if (player == null || dataSource == null) {
            return Optional.empty();
        } else {
            return loadAccount(player.getUniqueId());
        }
    }

    /**
     * Save PlayerStats async.
     *
     * @param stats PlayerStats data
     */
    public void saveAsync(PlayerStats stats) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> save(Lists.newArrayList(stats)));
    }

    /**
     * Save the PlayerStats on the current Thread.
     *
     * @param stats PlayerStats data
     */
    public void save(Collection<PlayerStats> stats) {
        if (stats != null && dataSource != null) {
            update(stats.stream()
                    .filter(Objects::nonNull)
                    .filter(stat -> !stat.isNew())
                    .collect(Collectors.toList()));

            insert(stats.stream()
                    .filter(Objects::nonNull)
                    .filter(PlayerStats::isNew)
                    .collect(Collectors.toList()));
        }
    }

    private void update(Collection<PlayerStats> stats) {
        if (stats.isEmpty()) {
            return;
        }

        //Save the stats to the database
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE player_stats "
                     + "SET kills=?, deaths=?, killstreak=?, mobkills=?, last_online=?, playername=? "
                     + "WHERE id=?")) {
            conn.setAutoCommit(false);
            for (PlayerStats stat : stats) {
                stmt.setInt(1, stat.getKills());
                stmt.setInt(2, stat.getDeaths());
                stmt.setInt(3, stat.getKillstreak());
                stmt.setInt(4, stat.getMobkills());

                stmt.setTimestamp(5, Timestamp.from(stat.getLastOnlineDate()));
                stmt.setString(6, stat.getPlayername());

                stmt.setInt(7, stat.getId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception ex) {
            logger.error("Error updating profiles", ex);
        }
    }

    private void insert(Collection<PlayerStats> stats) {
        if (stats.isEmpty()) {
            return;
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO player_stats "
                     + "(uuid, playername, kills, deaths, killstreak, mobkills, last_online) VALUES "
                     + "(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            for (PlayerStats stat : stats) {
                stmt.setString(1, stat.getUuid() == null ? null : stat.getUuid().toString());
                stmt.setString(2, stat.getPlayername());

                stmt.setInt(3, stat.getKills());
                stmt.setInt(4, stat.getDeaths());
                stmt.setInt(5, stat.getKillstreak());
                stmt.setInt(6, stat.getMobkills());

                stmt.setTimestamp(7, Timestamp.from(stat.getLastOnlineDate()));
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                for (PlayerStats stat : stats) {
                    if (!generatedKeys.next()) {
                        break;
                    }

                    stat.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception ex) {
            logger.error("Error inserting profiles", ex);
        }
    }

    /**
     * Starts saving all cache player stats and then clears the cache.
     */
    public void saveAll() {
        try {
            logger.info("Now saving the stats to the database. This could take a while.");

            //If pvpstats are enabled save all stats that are in the cache
            List<PlayerStats> toSave = Bukkit.getOnlinePlayers().stream()
                    .map(this::getStats)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            if (!toSave.isEmpty()) {
                save(toSave);
            }

            dataSource.close();
        } finally {
            //Make rally sure we remove all even on error
            Bukkit.getOnlinePlayers()
                    .forEach(player -> player.removeMetadata(METAKEY, plugin));
        }
    }

    /**
     * Initialize a components and checking for an existing database
     */
    public void setupDatabase() {
        //Check if pvpstats should be enabled
        dbConfig.loadConfiguration();
        dataSource = new HikariDataSource(dbConfig.getServerConfig());

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + dbConfig.getTablePrefix() + "player_stats ( "
                    + "id integer PRIMARY KEY AUTO_INCREMENT, "
                    + "uuid varchar(40) NOT NULL, "
                    + "playername varchar(16) NOT NULL, "
                    + "kills integer NOT NULL, "
                    + "deaths integer NOT NULL, "
                    + "mobkills integer NOT NULL, "
                    + "killstreak integer NOT NULL, "
                    + "last_online timestamp NOT NULL )";

            if (dbConfig.getServerConfig().getDriverClassName().contains("sqlite")) {
                createTableQuery = createTableQuery.replace("AUTO_INCREMENT", "");
                dataSource.setMaximumPoolSize(1);
            }

            stmt.execute(createTableQuery);
        } catch (Exception ex) {
            logger.error("Error creating database ", ex);
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateTopList, 20 * 60 * 5, 0);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (dataSource == null) {
                return;
            }

            Future<Collection<? extends Player>> syncPlayers = Bukkit.getScheduler()
                    .callSyncMethod(plugin, Bukkit::getOnlinePlayers);

            try {
                Collection<? extends Player> onlinePlayers = syncPlayers.get();

                List<PlayerStats> toSave = onlinePlayers.stream()
                        .map(this::getStats)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

                if (!toSave.isEmpty()) {
                    save(toSave);
                }
            } catch (CancellationException cancelEx) {
                //ignore it on shutdown
            } catch (Exception ex) {
                logger.error("Error fetching top list", ex);
            }
        }, 20 * 60, 20 * 60 * 5);

        registerEvents();
    }

    /**
     * Get the a map of the best players for a specific category.
     *
     * @return a iterable of the entries
     */
    public Iterable<Entry<String, Integer>> getTop() {
        synchronized (toplist) {
            return toplist.entrySet();
        }
    }

    /**
     * Updates the toplist
     */
    private void updateTopList() {
        String type = Settings.getTopType();
        Map<String, Integer> newToplist;
        switch (type) {
            case "killstreak":
                newToplist = getTopList("killstreak", PlayerStats::getKillstreak);
                break;
            case "mob":
                newToplist = getTopList("mobkills", PlayerStats::getMobkills);
                break;
            default:
                newToplist = getTopList("kills", PlayerStats::getKills);
                break;
        }

        synchronized (toplist) {
            //set it after fetching so it's only blocking for a short time
            toplist.clear();
            toplist.putAll(newToplist);
        }
    }

    private Map<String, Integer> getTopList(String type, Function<PlayerStats, Integer> valueMapper) {
        if (dataSource == null) {
            return Collections.emptyMap();
        }

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            try (ResultSet resultSet = stmt.executeQuery("SELECT * FROM player_stats ORDER BY " + type + " desc"
                    + " LIMIT " + Settings.getTopitems())) {
                Map<String, Integer> result = Maps.newHashMap();
                for (int i = 0; i < Settings.getTopitems(); i++) {
                    if (!resultSet.next()) {
                        return result;
                    }

                    PlayerStats stats = extractPlayerStats(resultSet);
                    if (!stats.isNew()) {
                        String entry = (i + 1) + ". " + stats.getPlayername();
                        result.put(entry, valueMapper.apply(stats));
                    }
                }

                return result;
            }
        } catch (SQLException ex) {
            logger.error("Error loading top list", ex);
        }

        return Collections.emptyMap();
    }

    private void registerEvents() {
        if (Bukkit.getPluginManager().isPluginEnabled("InSigns")) {
            //Register this listener if InSigns is available
            Bukkit.getPluginManager().registerEvents(new SignListener(plugin, this), plugin);
        }

        ReplaceManager replaceManager = ReplaceManager.getInstance();
        replaceManager.register(newVariable("kills", PlayerStats::getKills));
        replaceManager.register(newVariable("deaths", PlayerStats::getDeaths));
        replaceManager.register(newVariable("kdr", PlayerStats::getKdr));
        replaceManager.register(newVariable("mob-kills", PlayerStats::getMobkills));
        replaceManager.register(newVariable("killstreak", PlayerStats::getKillstreak));
        replaceManager.register(newVariable("current-streak", PlayerStats::getCurrentStreak));

        Bukkit.getPluginManager().registerEvents(new StatsListener(plugin, this), plugin);
    }

    private Replacer newVariable(String variable, Function<PlayerStats, Integer> fct) {
        return new Replacer(plugin, "kills")
                .scoreSupply(player -> getStats(player).map(fct).orElse(-1));
    }
}
