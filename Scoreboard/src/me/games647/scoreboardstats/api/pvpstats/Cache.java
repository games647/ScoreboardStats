package me.games647.scoreboardstats.api.pvpstats;

public final class Cache {

    private int kills, mob, deaths;

    public Cache(final int paramkills, final int parammob, final int paramdeaths) {
        this.kills = paramkills;
        this.mob = parammob;
        this.deaths = paramdeaths;
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

    public void increaseKills() {
        this.kills++;
    }

    public void increaseMob() {
        this.mob++;
    }

    public void increaseDeaths() {
        this.deaths++;
    }
}
