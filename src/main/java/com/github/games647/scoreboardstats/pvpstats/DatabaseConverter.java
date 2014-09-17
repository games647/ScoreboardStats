package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlUpdate;

import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

/**
 * Converts player stats from non-uuid compatible version to uuid compatible
 * version and back. Currently it just converts the database to the new
 * structure until we now how uuids and name-changes are handled in servers
 * below 1.7.2
 *
 * @see Database
 */
public class DatabaseConverter {

    //reserved for later use
//    private static final String USERNAME_DATA_TIME = "https://api.mojang.com/users/profiles/minecraft/%USERNAME%?at=%TIMESTAMP%";

    private static final String TEST_TABLE = "SELECT 1 FROM `%table%` LIMIT 1";

    private final EbeanServer databaseServer;

    public DatabaseConverter(EbeanServer databaseServer) {
        this.databaseServer = databaseServer;
    }

    public void convertNewDatabaseSystem() {
        // Some systems ingore uppercase other don't
        convert("PlayerStats");
        convert("playerstats");
    }

    private void convert(String from) {
        if (existTable(from)) {
            databaseServer.beginTransaction();
            try {
                final SqlUpdate convert = databaseServer.createSqlUpdate("INSERT INTO `player_stats` "
                        + "(`playername`, `kills`, `deaths`, `mobkills`, `killstreak`, `last_online`) "
                        + "SELECT `playername`, `kills`, `deaths`, `mobkills`, `killstreak`, ? "
                        + "FROM `" + from + "`");

                //sql parameters begins with one
                convert.setParameter(1, new Date(System.currentTimeMillis()));
                final int affectedRows = convert.execute();
                final SqlUpdate dropTable = databaseServer.createSqlUpdate("DROP TABLE `" + from + "`");
                dropTable.execute();
                //a drop have to commited and should be rollbackable if process wasn't complete
                databaseServer.commitTransaction();

                Logger.getLogger("ScoreboardStats")
                        .log(Level.INFO, "Successfully converted {0} into new table structure", affectedRows);
            } finally {
                databaseServer.endTransaction();
            }
        }
    }

//    public void convertUUIDs() {
//        try {
//            final List<PlayerStats> buffer = databaseServer.find(PlayerStats.class)
//                    .select("id, playername")
//                    .where().isNull("uuid")
//                    .setMaxRows(1024)
//                    .findList();
//
//            final Map<String, Integer> map = Maps.newHashMapWithExpectedSize(1024);
//            for (PlayerStats playerStats : buffer) {
//                map.put(playerStats.getPlayername(), playerStats.getId());
//            }
//
//            //this is a blocking internet call
//            final Map<String, UUID> call = new UUIDFetcher(map.keySet()).call();
//
//        } catch (Exception ex) {
//            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, "Unable to convert to uuids", ex);
//        }
//    }
//
//    public void convertUUIDsAsync(ExecutorService executorService) {
//
//    }

    private boolean existTable(String tableName) {
        try {
            final SqlQuery query = databaseServer.createSqlQuery(TEST_TABLE.replace("%table%", tableName));
            query.findUnique();
            return true;
        } catch (PersistenceException exception) {
            return false;
        }
    }
}
