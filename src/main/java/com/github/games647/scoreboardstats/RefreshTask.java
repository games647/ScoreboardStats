package com.github.games647.scoreboardstats;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    private final Map<Player, int[]> queue = Maps.newHashMapWithExpectedSize(100);

    RefreshTask(ScoreboardStats instance) {
        this.plugin = instance;
    }

    @Override
    public void run() {
        //let the players update smoother
        final Set<Map.Entry<Player, int[]>> entrySet = queue.entrySet();
        int nextUpdates = getNextUpdates();
        for (final Iterator<Map.Entry<Player, int[]>> it = entrySet.iterator(); it.hasNext();) {
            final Map.Entry<Player, int[]> entry = it.next();

            final Player player = entry.getKey();
            final int[] valueArray = entry.getValue();
            final int value = valueArray[0];
            if (value == 0) {
                //We will check if the player is online and remove it from queue if not so we can prevent memory leaks
                if (player == null || !player.isOnline()) {
                    it.remove();
                } else if (nextUpdates != 0) {
                    //Smoother refreshing; limit the updates
                    plugin.getScoreboardManager().sendUpdate(player);
                    valueArray[0] = 20 * Settings.getIntervall();
                    nextUpdates--;
                }
            } else {
                valueArray[0]--;
            }
        }
    }

    /**
     * Add a player to the queue for updating him.
     *
     * @param request the player that should be added.
     */
    public void addToQueue(Player request) {
        if (!queue.containsKey(request)) {
            queue.put(request, new int[] {20 * Settings.getIntervall()});
        }
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
        final int nextUpdates = queue.size() / 20;
        if (nextUpdates <= 0) {
            return 1;
        }

        return nextUpdates;
    }
}
