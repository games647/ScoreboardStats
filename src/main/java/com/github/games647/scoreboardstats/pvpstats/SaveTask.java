package com.github.games647.scoreboardstats.pvpstats;

import org.bukkit.entity.Player;

public final class SaveTask implements Runnable {

    @Override
    public void run() {
        Database.saveAll(false);
    }
}
