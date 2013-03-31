package me.games647.scoreboardstats.api;

import com.avaje.ebean.validation.NotNull;

@javax.persistence.Entity
@javax.persistence.Table(name = "PlayerStats")
public class PlayerStats implements java.io.Serializable {

    @javax.persistence.Id
    @NotNull
    private String playername;

    @NotNull
    private int kills;

    @NotNull
    private int deaths;

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
}
