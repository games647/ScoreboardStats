package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.scoreboard.SbManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

final class RefreshTask implements Runnable {

    @Override
    public void run() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

            if (objective == null) {
                SbManager.createScoreboard(player);
            } else {
                Settings.sendUpdate(player, false);
            }
        }
    }
}
