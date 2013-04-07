package me.games647.scoreboardstats.api;

import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import static org.bukkit.Bukkit.getOnlinePlayers;

public final class UpdateThread implements Runnable {

    @Override
    public void run() {
        for (org.bukkit.entity.Player player : getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            getSettings().sendUpdate(player);
        }
    }

}
