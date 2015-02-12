package com.github.games647.scoreboardstats.pvpstats;


public class StatsSaver implements Runnable{

    private final PlayerStats stats;

    public StatsSaver(PlayerStats toSave) {
        this.stats = toSave;
    }

    @Override
    public void run() {
        Database.save(stats);
    }
}
