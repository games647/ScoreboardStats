package com.github.games647.scoreboardstats.pvpstats;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalListeners;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class RemoveListener implements RemovalListener<String, PlayerCache> {

    protected static RemovalListener<String, PlayerCache> getNewInstace() {
        final RemoveListener listener = new RemoveListener();
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("ScoreboardStats-Saver %1$d").build();
        final ExecutorService executor = Executors.newCachedThreadPool(threadFactory);

        return RemovalListeners.asynchronous(listener, executor);
    }

    @Override
    public void onRemoval(RemovalNotification<String, PlayerCache> notification) {
        final String playerName = notification.getKey();
        final PlayerCache playerCache = notification.getValue();

        //There are no need to query the database
        if ((playerCache == null)
                || (playerCache.getKills() == 0)
                && (playerCache.getDeaths() == 0)
                && (playerCache.getMob() == 0)) {
            return;
        }

        PlayerStats stats = Database.getDatabaseInstance().find(PlayerStats.class).where().eq("playername", playerName).findUnique();
        if (stats == null) {
            stats = new PlayerStats();
            stats.setPlayername(playerName);
        }

        if ((stats.getDeaths() == playerCache.getDeaths())
                && (stats.getKills() == playerCache.getKills())
                && (stats.getMobkills() == playerCache.getMob())
                && (stats.getKillstreak() == playerCache.getStreak())) {
            return; //No dates have been changed so there is no need to save the dates.
        }

        stats.setDeaths(playerCache.getDeaths());
        stats.setKills(playerCache.getKills());
        stats.setMobkills(playerCache.getMob());
        stats.setKillstreak(playerCache.getStreak());
        Database.getDatabaseInstance().save(stats);
    }
}
