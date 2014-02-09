package com.github.games647.scoreboardstats.pvpstats;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalListeners;
import com.google.common.cache.RemovalNotification;

import java.util.concurrent.ExecutorService;

/* package */ class RemoveListener implements RemovalListener<String, PlayerStats> {

    protected static RemovalListener<String, PlayerStats> newInstace(ExecutorService executor) {
        final RemoveListener listener = new RemoveListener();

        //Return an asynch removallistener
        return RemovalListeners.asynchronous(listener, executor);
    }

    @Override
    public void onRemoval(RemovalNotification<String, PlayerStats> notification) {
        final PlayerStats stats = notification.getValue();
        if (stats != null) {
            //Save the stats to the database
            Database.getDatabaseInstance().save(stats);
        }
    }
}
