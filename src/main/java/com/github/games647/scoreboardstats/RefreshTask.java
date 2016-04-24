package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.config.Settings;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.entity.Player;

/**
 * Handling all updates for a player in a performance optimized variant. This
 * class split the updates over the ticks much smoother.
 *
 * @see SbManager
 * @see com.github.games647.scoreboardstats.variables.ReplaceManager
 */
public class RefreshTask implements Runnable {

    private final ScoreboardStats plugin;

    //Prevent duplicate entries and is faster than the delay queue
    private final Map<Player, MutableInt> queue = Maps.newHashMapWithExpectedSize(100);

    private int nextGlobalUpdate = 20 * Settings.getInterval();

    /**
     * Initialize refresh task
     *
     * @param instance ScoreboardStats instance
     */
    public RefreshTask(ScoreboardStats instance) {
        this.plugin = instance;
    }

    @Override
    public void run() {
        //let the players update smoother
        Set<Map.Entry<Player, MutableInt>> entrySet = queue.entrySet();
        int remainingUpdates = getNextUpdates();
        for (Iterator<Map.Entry<Player, MutableInt>> it = entrySet.iterator(); it.hasNext();) {
            Map.Entry<Player, MutableInt> entry = it.next();

            Player player = entry.getKey();
            MutableInt remanigTicks = entry.getValue();
            if (remanigTicks.intValue() == 0) {
                if (remainingUpdates != 0) {
                    //Smoother refreshing; limit the updates
                    plugin.getScoreboardManager().onUpdate(player);
                    remanigTicks.setValue(20 * Settings.getInterval());
                    remainingUpdates--;
                }
            } else {
                remanigTicks.decrement();
            }
        }

        nextGlobalUpdate--;
        if (nextGlobalUpdate == 0) {
            nextGlobalUpdate = 20 * Settings.getInterval();
            //update globals
            plugin.getReplaceManager().updateGlobals();
        }
    }

    /**
     * Add a player to the queue for updating him.
     *
     * @param request the player that should be added.
     * @return true if it was successfully queued
     */
    public boolean addToQueue(Player request) {
        boolean alreadyQueued = queue.containsKey(request);
        if (!alreadyQueued) {
            //check if it isn't already in the queue
            queue.put(request, new MutableInt(20 * Settings.getInterval()));
        }

        return !alreadyQueued;
    }

    /**
     * Checks if the player is in the refresh queue.
     *
     * @param request player instance
     * @return true if the player is in the refresh queue.
     */
    public boolean contains(Player request) {
        return queue.containsKey(request);
    }

    /**
     * Explicity removes the player from the refresh queue.
     *
     * @param request player who should be removed
     * @return if the last entry exists
     */
    public boolean remove(Player request) {
        return queue.remove(request) != null;
    }

    /**
     * Clears the complete queue.
     */
    public void clear() {
        queue.clear();
    }

    private int getNextUpdates() {
        int nextUpdates = queue.size() / 20;
        if (nextUpdates <= 0) {
            //just update minimum one player per tick. Otherwise servers with not much players
            //won't receive any updates
            return 1;
        }

        return nextUpdates;
    }
}
