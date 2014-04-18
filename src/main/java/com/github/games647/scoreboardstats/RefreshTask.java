package com.github.games647.scoreboardstats;

import com.google.common.collect.ComparisonChain;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.bukkit.entity.Player;

/**
 * Handling all updates for a player in a performance optimized variant. This
 * class split the updates over the ticks much smoother.
 */
public class RefreshTask implements Runnable {

    private final ScoreboardStats plugin;

    private final Queue<DelayedElement> queue = new DelayQueue<DelayedElement>();
    private final Collection<Player> hidelist = Collections.newSetFromMap(new WeakHashMap<Player, Boolean>(30));

    RefreshTask(ScoreboardStats instance) {
        this.plugin = instance;
    }

    /**
     * Get a set of players who disabled the scoreboards.
     *
     * @return a set of players who disabled the scoreboards
     */
    public Collection<Player> getHidelist() {
        return hidelist;
    }

    @Override
    public void run() {
        //let the players update smoother
        for (int i = getNextUpdates(); i > 0; i--) {
            final DelayedElement poll = queue.poll();
            if (poll == null) {
                //cancel the loop if there are no more elements
                break;
            }

            final Player player = poll.getPlayer();
            if (player != null
                    && player.isOnline()
                    && Settings.isActiveWorld(player.getWorld())
                    && !hidelist.contains(player)) {
                plugin.getScoreboardManager().sendUpdate(player);
                addToQueue(player);
            }
        }
    }

    /**
     * Add a player to the queue for updating him.
     *
     * @param request the player that should be added.
     */
    public void addToQueue(Player request) {
        final DelayedElement element = new DelayedElement(request, Settings.getIntervall());
        queue.add(element);
    }

    /**
     * Clears the complete queue.
     */
    public void clear() {
        queue.clear();
    }

    private int getNextUpdates() {
        final int nextUpdates = queue.size() / 20;
        if (!queue.isEmpty() && nextUpdates < 1) {
            return 1;
        }

        return nextUpdates;
    }

    @EqualsAndHashCode(exclude = "startTime")
    @ToString
    private static class DelayedElement implements Delayed {

        private final long startTime;
        private final Player player;

        DelayedElement(Player player, int delay) {
            this.player = player;
            this.startTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            final long diff = startTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed delayed) {
            return ComparisonChain.start()
                    .compare(startTime, ((DelayedElement) delayed).startTime)
                    .result();
        }
    }
}
