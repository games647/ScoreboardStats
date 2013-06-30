package com.github.games647.scoreboardstats;

final class RefreshTask implements Runnable {

    @Override
    public void run() {
        for (final org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            final org.bukkit.scoreboard.Objective objective = player.getScoreboard().getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

            if (objective == null) {
                com.github.games647.scoreboardstats.scoreboard.SbManager.createScoreboard(player);
            } else {
                Settings.sendUpdate(player, false);
            }
        }
    }
}
