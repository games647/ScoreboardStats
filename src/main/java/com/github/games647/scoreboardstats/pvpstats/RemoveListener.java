package com.github.games647.scoreboardstats.pvpstats;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalListeners;
import com.google.common.cache.RemovalNotification;

import java.util.concurrent.ExecutorService;

/* package */ class RemoveListener implements RemovalListener<String, PlayerCache> {

    protected static RemovalListener<String, PlayerCache> newInstace(ExecutorService executor) {
        final RemoveListener listener = new RemoveListener();

        //Return an asynch removallistener
        return RemovalListeners.asynchronous(listener, executor);
    }

    @Override
    public void onRemoval(RemovalNotification<String, PlayerCache> notification) {
        final String playerName = notification.getKey();
        final PlayerCache playerCache = notification.getValue();

        //There are no need to query the database
        if (playerCache == null || !playerCache.hasChanged()) {
            return;
        }

        //Find the stats based on the player name
        PlayerStats stats = Database.getDatabaseInstance().find(PlayerStats.class)
                .where().eq("playername", playerName).findUnique();
        if (stats == null) {
            //The player doesn't exist in the database
            stats = new PlayerStats();
            stats.setPlayername(playerName);
        }

        //Set all changed stuff
        stats.setDeaths(playerCache.getDeaths());
        stats.setKills(playerCache.getKills());
        stats.setMobkills(playerCache.getMob());
        stats.setKillstreak(playerCache.getHighestStreak());
        //Save the stats to the database
        Database.getDatabaseInstance().save(stats);
    }
}
