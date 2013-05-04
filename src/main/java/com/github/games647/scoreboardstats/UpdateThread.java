package com.github.games647.scoreboardstats;

import org.bukkit.scoreboard.DisplaySlot;

public final class UpdateThread implements Runnable {

    @Override
    public void run() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null || player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().equals("ScoreboardStatsT")) {
                continue;
            }
            
            com.github.games647.scoreboardstats.ScoreboardStats.getSettings().sendUpdate(player, false);
        }
    }
}
