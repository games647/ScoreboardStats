package me.games647.scoreboardstats.api.pvpstats;

import me.games647.scoreboardstats.ScoreboardStats;
import me.games647.scoreboardstats.api.Score;
import me.games647.scoreboardstats.listener.PlayerListener;
import org.bukkit.entity.Player;

public final class TempScoreShow implements Runnable {

    private static String checkLength(final String check) {
        if (check.length() < 15) {
            return check;
        }

        return check.substring(0, 14);
    }

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
        final java.util.Map<String, Integer> top = Database.getTop();
        Score.createScoreboard(player, false);

        for (String key : top.keySet()) {
            Score.sendScore(
                    ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection
                    , String.format("%s%s", ScoreboardStats.getSettings().getTempcolor(), checkLength(key))
                    , top.get(key)
                    , false);
        }

        org.bukkit.Bukkit.getScheduler().runTaskLater(ScoreboardStats.getInstance(), new TempScoreDisapper(this.player), ScoreboardStats.getSettings().getTempdisapper() * 20L);
    }
}
