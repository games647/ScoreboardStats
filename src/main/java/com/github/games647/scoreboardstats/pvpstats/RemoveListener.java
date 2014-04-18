package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.EbeanServer;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalListeners;
import com.google.common.cache.RemovalNotification;

import java.util.concurrent.Executor;

/**
 * Listener for removing the cache elements.
 */
/* package */ class RemoveListener implements RemovalListener<String, PlayerStats> {

    /**
     * Return an asynch removallistener
     */
    static RemovalListener<String, PlayerStats> newInstace(Executor executor) {
        return RemovalListeners.asynchronous(new RemoveListener(), executor);
    }

    @Override
    public void onRemoval(RemovalNotification<String, PlayerStats> notification) {
        final PlayerStats stats = notification.getValue();
        final EbeanServer databaseInstance = Database.getDatabaseInstance();
        if (stats != null && stats.isChanged() && databaseInstance != null) {
            //Save the stats to the database
            databaseInstance.save(stats);
        }
    }
}
