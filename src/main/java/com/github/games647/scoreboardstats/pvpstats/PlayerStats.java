package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.avaje.ebean.validation.Range;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "PlayerStats")
@EqualsAndHashCode
@ToString(includeFieldNames=true)
public class PlayerStats {

    @Id
    @NotEmpty
    @NotNull
    @Length(max = 16)
    //A minecraft name cannot be longer than 16
    private String playername;

    //You can't have negative stats
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

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getMobkills() {
        return mobkills;
    }

    public void setMobkills(int mobkills) {
        this.mobkills = mobkills;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public void setKillstreak(int killstreak) {
        this.killstreak = killstreak;
    }
}
