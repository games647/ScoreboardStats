package com.github.games647.scoreboardstats;

public final class UpdateThread implements Runnable {

    @Override
    public void run() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            final org.bukkit.scoreboard.Objective objective = player.getScoreboard().getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

            if (objective == null) {
                com.github.games647.scoreboardstats.scoreboard.ScoreboardManager.createScoreboard(player);
                continue;
            } else if (!objective.getName().startsWith("ScoreboardStats")) {
                continue;
            }

            com.github.games647.scoreboardstats.ScoreboardStats.getSettings().sendUpdate(player, false);
        }
    }
}
