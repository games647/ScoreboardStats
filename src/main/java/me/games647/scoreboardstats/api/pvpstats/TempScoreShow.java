package me.games647.scoreboardstats.api.pvpstats;

import static me.games647.scoreboardstats.ScoreboardStats.getInstance;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
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
        me.games647.scoreboardstats.listener.PlayerListener.list.add(player.getName());
        me.games647.scoreboardstats.api.Score.createTopListScoreboard(player);
        org.bukkit.Bukkit.getScheduler().runTaskLater(getInstance(), new TempScoreDisapper(this.player), getSettings().getTempdisapper() * 20L);
    }
}
