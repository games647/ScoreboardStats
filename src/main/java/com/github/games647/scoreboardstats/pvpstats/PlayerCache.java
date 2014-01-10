package com.github.games647.scoreboardstats.pvpstats;

public class PlayerCache {

    private int kills;
    private int mob;
    private int deaths;

    private int streak;
    private int laststreak;

    public PlayerCache(int paramkills, int parammob, int paramdeaths, int paramstreak) {
        kills   = paramkills;
        mob     = parammob;
        deaths  = paramdeaths;
        streak  = paramstreak;
    }

    public PlayerCache() {
        //Do nothing, because all variables are automatically init as 0
    }

    public void onKill() {
        kills++;
        laststreak++;
    }

    public void increaseMobKills() {
        mob++;
    }

    public void onDeath() {
        if (laststreak > streak) {
            streak = laststreak;
        }

        laststreak = 0;
        deaths++;
    }

    public int getKills() {
        return kills;
    }

    public int getMob() {
        return mob;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getStreak() {
        return streak;
    }

    public int getLaststreak() {
        return laststreak;
    }
}
