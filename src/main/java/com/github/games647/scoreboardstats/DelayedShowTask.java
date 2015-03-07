package com.github.games647.scoreboardstats;

import org.bukkit.entity.Player;

/**
 * Scheduled appear task
 */
public class DelayedShowTask implements Runnable {

    private final SbManager scoreboardManager;

    private final Player player;
    private final boolean action;

    /**
     * Creates a new scheduled appear of the normal scoreboard or the temp scoreboard
     *
     * @param player the specific player
     * @param action if the temp scoreboard be displayed
     * @param scoreboardManager the associated sbManager
     */
    public DelayedShowTask(Player player, boolean action, SbManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
        this.player = player;
        this.action = action;
    }

    @Override
    public void run() {
        if (action) {
            scoreboardManager.createTopListScoreboard(player);
        } else {
            scoreboardManager.createScoreboard(player);
        }
    }

}
