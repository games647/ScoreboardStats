package me.games647.scoreboardstats.api.pvpstats;

import me.games647.scoreboardstats.api.Score;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public final class TempScoreDisapper implements Runnable {

    private final Player player;

    public TempScoreDisapper(final Player paramplayer) {
        this.player = paramplayer;
    }

    @Override
    public void run() {
        me.games647.scoreboardstats.listener.PlayerListener.list.remove(player.getName());
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        Score.createScoreboard(player);
    }
}
