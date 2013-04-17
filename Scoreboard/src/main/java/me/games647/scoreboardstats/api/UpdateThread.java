package me.games647.scoreboardstats.api;

import me.games647.scoreboardstats.listener.PlayerListener;

public final class UpdateThread implements Runnable {

    @Override
    public void run() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            if ((!player.isOnline()) || (PlayerListener.list.contains(player.getName()))) {
                continue;
            }
            me.games647.scoreboardstats.ScoreboardStats.getSettings().sendUpdate(player);
        }
    }
}
