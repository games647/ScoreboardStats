package me.games647.scoreboardstats.api.pvpstats;

import me.games647.scoreboardstats.ScoreboardStats;
import me.games647.scoreboardstats.api.Score;
import me.games647.scoreboardstats.listener.PlayerListener;
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
        PlayerListener.list.add(player.getName());
        Score.createTopListScoreboard(player);
        org.bukkit.Bukkit.getScheduler().runTaskLater(ScoreboardStats.getInstance(), new TempScoreDisapper(this.player), ScoreboardStats.getSettings().getTempdisapper() * 20L);
    }
}
