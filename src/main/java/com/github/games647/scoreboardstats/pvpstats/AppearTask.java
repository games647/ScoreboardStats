package com.github.games647.scoreboardstats.pvpstats;

import org.bukkit.entity.Player;

public final class AppearTask implements Runnable {

    private final Player player;

    public AppearTask(final Player paramplayer) {
        player = paramplayer;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            Database.saveAccount(player.getName(), false);
            com.github.games647.scoreboardstats.scoreboard.SbManager.createTopListScoreboard(player);
        }
    }
}
