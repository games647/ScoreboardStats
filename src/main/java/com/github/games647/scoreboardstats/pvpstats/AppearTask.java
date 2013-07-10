package com.github.games647.scoreboardstats.pvpstats;

import com.github.games647.scoreboardstats.scoreboard.SbManager;

import org.bukkit.entity.Player;

public final class AppearTask implements Runnable {

    private final Player player;

    public AppearTask(Player paramplayer) {
        player = paramplayer;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            Database.saveAccount(player.getName(), false);
            SbManager.createTopListScoreboard(player);
        }
    }
}
