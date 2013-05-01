package me.games647.scoreboardstats.api.pvpstats;

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
        me.games647.scoreboardstats.api.Score.createTopListScoreboard(player);
    }
}
