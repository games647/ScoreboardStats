package com.github.games647.scoreboardstats;

import com.github.games647.variables.Other;

final class UpdateThread implements Runnable {

    @Override
    public void run() {
        for (final org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            final org.bukkit.scoreboard.Objective objective = player.getScoreboard().getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

            if (objective == null || objective.getName().equals(Other.PLUGIN_NAME)) {
                MainClass.getSettings().sendUpdate(player, false);
            }
        }
    }
}
