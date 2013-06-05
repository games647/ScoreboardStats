package com.github.games647.scoreboardstats.pvpstats;

import com.github.games647.variables.Data;

@javax.persistence.Entity
@javax.persistence.Table(name = "PlayerStats")
public class PlayerStats {

    @javax.persistence.Id
    @com.avaje.ebean.validation.NotEmpty
    @com.avaje.ebean.validation.NotNull
    private String playername;

    private int kills;
    private int deaths;
    private int mobkills;
    private int killstreak;

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(final String paramplayername) {
        playername = paramplayername;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(final int paramkills) {
        kills = paramkills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(final int paramdeaths) {
        deaths = paramdeaths;
    }

    public int getMobkills() {
        return mobkills;
    }

    public void setMobkills(final int parammobkills) {
        mobkills = parammobkills;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public void setKillstreak(final int paramkillstreak) {
        killstreak = paramkillstreak;
    }

	public Integer get(String table) {
		if(table.equals(Data.COL_KILL)) return kills;
		if(table.equals(Data.COL_MOB)) return mobkills;
		if (table.equals(Data.COL_KILLSTREAK)) return killstreak;
		return -1;
	}
}
