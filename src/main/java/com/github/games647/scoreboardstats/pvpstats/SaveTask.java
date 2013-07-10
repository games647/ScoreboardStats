package com.github.games647.scoreboardstats.pvpstats;

import org.bukkit.entity.Player;

public final class SaveTask implements Runnable {

    private final Player player;

    public SaveTask(Player paramplayer) {
        this.player = paramplayer;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            Database.saveAccount(player.getName(), true);
        }
    }
}
