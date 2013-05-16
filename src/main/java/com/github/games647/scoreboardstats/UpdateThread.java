package com.github.games647.scoreboardstats;

public final class UpdateThread implements Runnable {

    @Override
    public void run() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            com.github.games647.scoreboardstats.ScoreboardStats.getSettings().sendUpdate(player, false);
        }
    }
}
