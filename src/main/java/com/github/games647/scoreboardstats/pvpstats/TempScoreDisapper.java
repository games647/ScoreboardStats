package com.github.games647.scoreboardstats.pvpstats;

import com.github.games647.scoreboardstats.scoreboard.SbManager;
import org.bukkit.entity.Player;

public final class TempScoreDisapper implements Runnable {

    private final Player player;

    public TempScoreDisapper(final Player paramplayer) {
        player = paramplayer;
    }

    @Override
    public void run() {
        SbManager.createScoreboard(player);
    }
}
