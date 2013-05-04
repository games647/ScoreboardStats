package com.github.games647.scoreboardstats.pvpstats;

@javax.persistence.Entity
@javax.persistence.Table(name = "PlayerStats")
public class PlayerStats implements java.io.Serializable {

    @javax.persistence.Id
    @com.avaje.ebean.validation.NotNull
    @com.avaje.ebean.validation.NotEmpty
    private String playername;
    private int kills, deaths, mobkills, killstreak; //Add killStreaks

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(final String paramplayername) {
        this.playername = paramplayername;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(final int paramkills) {
        this.kills = paramkills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(final int paramdeaths) {
        this.deaths = paramdeaths;
    }

    public int getMobkills() {
        return mobkills;
    }

    public void setMobkills(final int parammobkills) {
        this.mobkills = parammobkills;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public void setKillstreak(final int paramkillstreak) {
        this.killstreak = paramkillstreak;
    }
}
