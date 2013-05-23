package com.github.games647.scoreboardstats.pvpstats;

public final class PlayerCache {

    private int kills;
    private int mob;
    private int deaths;
    private int streak;
    private int laststreak;

    public PlayerCache(final int paramkills, final int parammob, final int paramdeaths, final int paramstreak) {
        kills = paramkills;
        mob = parammob;
        deaths = paramdeaths;
        streak = paramstreak;
    }

    public PlayerCache() {
       //Do nothing, because all variables are automatically init as 0
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

    public void onKill() {
        kills++;
        laststreak++;
    }

    public void increaseMob() {
        mob++;
    }

    public int getLastStreak() {
        return laststreak;
    }

    public void onDeath() {
        if (laststreak > streak) {
            streak = laststreak;
        }

        laststreak = 0;
        deaths++;
    }
}
