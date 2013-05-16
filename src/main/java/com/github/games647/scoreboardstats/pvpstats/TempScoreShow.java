package com.github.games647.scoreboardstats.pvpstats;

import org.bukkit.entity.Player;

public final class TempScoreShow implements Runnable {

    private final Player player;

    public TempScoreShow(final Player paramplayer) {
        this.player = paramplayer;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            return;
        }

        Database.saveAccount(player.getName(), false);
        com.github.games647.scoreboardstats.scoreboard.ScoreboardManager.createTopListScoreboard(player);
    }
}
