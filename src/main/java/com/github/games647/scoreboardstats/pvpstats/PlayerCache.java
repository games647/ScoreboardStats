package com.github.games647.scoreboardstats.pvpstats;

import lombok.Getter;
import lombok.ToString;

@ToString(includeFieldNames = true)
public final class PlayerCache {

    @Getter private int kills;
    @Getter private int mob;
    @Getter private int deaths;

    @Getter private int streak;
    @Getter private int laststreak;

    @Getter private boolean remove;

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

    public void increaseMob() {
        mob++;
    }

    public void onDeath() {
        if (laststreak > streak) {
            streak = laststreak;
        }

        laststreak = 0;
        deaths++;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }
}
