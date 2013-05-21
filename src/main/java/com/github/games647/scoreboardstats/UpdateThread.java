package com.github.games647.scoreboardstats;

import com.github.games647.variables.VariableList;

public final class UpdateThread implements Runnable {

    @Override
    public void run() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            final org.bukkit.scoreboard.Objective objective = player.getScoreboard().getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

            if (objective == null || objective.getName().equals(VariableList.PLUGIN_NAME)) {
                com.github.games647.scoreboardstats.ScoreboardStats.getSettings().sendUpdate(player, false);
            }
        }
    }
}
