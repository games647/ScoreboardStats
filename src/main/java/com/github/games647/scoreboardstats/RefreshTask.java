package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.scoreboard.SbManager;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

final class RefreshTask implements Runnable {

    @Override
    public void run() {
        for (final org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

            if (objective == null) {
                SbManager.createScoreboard(player);
            } else {
                Settings.sendUpdate(player, false);
            }
        }
    }
}
