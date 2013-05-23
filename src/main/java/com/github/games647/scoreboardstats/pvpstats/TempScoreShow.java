package com.github.games647.scoreboardstats.pvpstats;

import com.github.games647.scoreboardstats.scoreboard.SbManager;
import org.bukkit.entity.Player;

public final class TempScoreShow implements Runnable {

    private final Player player;

    public TempScoreShow(final Player paramplayer) {
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
