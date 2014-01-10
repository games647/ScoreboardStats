package com.github.games647.scoreboardstats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/* package */ class RefreshTask implements Runnable {

    private final ScoreboardStats pluginInstance;

    /* package */ RefreshTask(ScoreboardStats instance) {
        pluginInstance = instance;
    }

    @Override
    public void run() {
        for (final Player player: Bukkit.getOnlinePlayers()) {
            pluginInstance.getScoreboardManager().sendUpdate(player);
        }
    }
}
