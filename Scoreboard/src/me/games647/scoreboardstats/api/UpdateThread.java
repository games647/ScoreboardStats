package me.games647.scoreboardstats.api;

public final class UpdateThread implements Runnable {

    @Override
    public void run() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            me.games647.scoreboardstats.ScoreboardStats.getSettings().sendUpdate(player);
        }
    }
}
