package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.avaje.ebean.validation.Range;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PlayerStats")
public class PlayerStats {

    @Id
    @NotEmpty
    @NotNull
    @Length(max = 16) //A minecraft name cannot be longer than 16
    private String playername;

    @Range(min = 0)
    private int kills;

    @Range(min = 0)
    private int deaths;

    @Range(min = 0)
    private int mobkills;

    @Range(min = 0)
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
}
