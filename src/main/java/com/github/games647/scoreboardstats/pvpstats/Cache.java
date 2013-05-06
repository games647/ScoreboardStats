package com.github.games647.scoreboardstats.pvpstats;

public final class Cache {

    private int kills, mob, deaths, streak, laststreak;

    public Cache(final int paramkills, final int parammob, final int paramdeaths, final int paramstreak) {
        this.kills = paramkills;
        this.mob = parammob;
        this.deaths = paramdeaths;
        this.streak = paramstreak;
    }

    public Cache() {
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
        this.kills++;
        this.laststreak++;
    }

    public void increaseMob() {
        this.mob++;
    }

    public int getLastStreak() {
        return laststreak;
    }

    public void onDeath() {
        if (laststreak > streak) {
            this.streak = this.laststreak;
        }

        laststreak = 0;
        this.deaths++;
    }
}
