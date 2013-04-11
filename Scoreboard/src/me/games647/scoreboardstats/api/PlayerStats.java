package me.games647.scoreboardstats.api;

@javax.persistence.Entity
@javax.persistence.Table(name = "PlayerStats")
public class PlayerStats implements java.io.Serializable {

    @javax.persistence.Id
    @com.avaje.ebean.validation.NotNull
    @com.avaje.ebean.validation.NotEmpty
    private String playername;

    private int kills, deaths, mobkills; //Add killStreaks

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(final String playername) {
        this.playername = playername;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(final int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(final int deaths) {
        this.deaths = deaths;
    }

    public int getMobkills() {
        return mobkills;
    }

    public void setMobkills(final int mobkills) {
        this.mobkills = mobkills;
    }
}
