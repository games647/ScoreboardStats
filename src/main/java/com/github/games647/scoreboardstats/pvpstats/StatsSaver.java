package com.github.games647.scoreboardstats.pvpstats;

public class StatsSaver implements Runnable{

    private final PlayerStats stats;
    private final Database statsDatabase;

    public StatsSaver(PlayerStats toSave, Database statsDatabase) {
        this.stats = toSave;
        this.statsDatabase = statsDatabase;
    }

    @Override
    public void run() {
        statsDatabase.save(stats);
    }
}
