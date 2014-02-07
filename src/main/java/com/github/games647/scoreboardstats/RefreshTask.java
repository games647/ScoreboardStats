package com.github.games647.scoreboardstats;

import com.google.common.collect.ComparisonChain;

import java.util.Queue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import lombok.EqualsAndHashCode;

import org.bukkit.entity.Player;

/*
 * Handling all updates for a player in a performance optimized variant. This
 * class split the updates over the ticks much smoother.
 */
public class RefreshTask implements Runnable {

    private final ScoreboardStats pluginInstance;
    private final Queue<DelayedElement> queue = new DelayQueue<DelayedElement>();

    public RefreshTask(ScoreboardStats instance) {
        this.pluginInstance = instance;
    }

    @Override
    public void run() {
        //let the players update smoother
        for (int i = getNextUpdates(); i > 0; i--) {
            final DelayedElement poll = queue.poll();
            if (poll == null) {
                //cancel the loop if there are no more elements
                break;
            } else if (poll.getPlayer().isOnline()) {
                final Player player = poll.getPlayer();
                pluginInstance.getScoreboardManager().sendUpdate(player);
                addToQueue(player);
            }
        }
    }

    /*
     * Add a player to the queue for updating him.
     */
    public void addToQueue(Player request) {
        queue.add(new DelayedElement(request, Settings.getIntervall()));
    }

    private int getNextUpdates() {
        final int nextUpdates = queue.size() / 20;
        if (queue.isEmpty()) {
            return 0;
        }

        if (nextUpdates < 1) {
            return 1;
        }

        return nextUpdates;
    }

    @EqualsAndHashCode
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
