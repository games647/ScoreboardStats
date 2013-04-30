package me.games647.scoreboardstats.api.pvpstats;

import org.bukkit.entity.Player;

public final class TempScoreDisapper implements Runnable {

    private final Player player;

    public TempScoreDisapper(final Player paramplayer) {
        this.player = paramplayer;
    }

    @Override
    public void run() {
        me.games647.scoreboardstats.listener.PlayerListener.list.remove(player.getName());
        player.getScoreboard().clearSlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        me.games647.scoreboardstats.api.Score.createScoreboard(player);
    }
}
