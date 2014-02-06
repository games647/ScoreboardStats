package com.github.games647.scoreboardstats;

public class TicksPerSecondTask implements Runnable {

    private static double lastTicks = 20.0D;

    public static double getLastTicks() {
        return lastTicks;
    }

    private long lastTime;

    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();
        final long difference = currentTime - lastTime;

        lastTicks = 20 * 1000.0 / difference;
        lastTime = currentTime;
    }
}
