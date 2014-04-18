package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.avaje.ebean.validation.Pattern;
import com.avaje.ebean.validation.Range;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.bukkit.util.NumberConversions;

/**
 * Represents the stats from a player. The stats are kills, deaths, mobkills and
 * killstreak. All stats are annotated to be validated on runtime to be not invalid.
 *
 * Maybe these stats also have to be validated to be not null, so we can prevent
 * some special cases while using it especially for sql database, but this can also
 * occures on using it as file database due incorrect format or unexpected user
 * modifications.
 */
@Entity
@Table(name = "PlayerStats")
@EqualsAndHashCode
@ToString
public class PlayerStats {

    @Id
    //min 1 character
    @NotEmpty
    @NotNull
    //Tells ebean explicity that the string can only have 16 characters
    @Length(max = 16)
    @Pattern(regex = "^\\w{2,16}$")
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

    private transient int laststreak;
    private transient boolean changed;

    /**
     * Default constructor for ebean
     */
    public PlayerStats() {
        //do nothing
    }

    /**
     * Creates a new stats instance
     *
     * @param playername who have these stats
     */
    public PlayerStats(String playername) {
        this.playername = playername;
    }

    /**
     * Get the player name of these stats
     *
     * @return the player name of these stats
     */
    public String getPlayername() {
        return playername;
    }

    /**
     * Set the player name of these stats
     *
     * @param playername the player name of these stats
     */
    public void setPlayername(String playername) {
        this.playername = playername;
    }

    /**
     * Get the player kills
     *
     * @return the player kills
     */
    public int getKills() {
        return kills;
    }

    /**
     * Set the player kills
     *
     * @param kills the player kills
     */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * Get the deaths
     *
     * @return the deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Set the deaths
     *
     * @param deaths the deaths
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * Get the mob kills
     *
     * @return the mob kills
     */
    public int getMobkills() {
        return mobkills;
    }

    /**
     * Set the mob kills
     *
     * @param mobkills the mob kills
     */
    public void setMobkills(int mobkills) {
        this.mobkills = mobkills;
    }

    /**
     * Get the highest killstreak
     *
     * @return the highest killstreak
     */
    public int getKillstreak() {
        return killstreak;
    }

    /**
     * Set the highest killstreak
     *
     * @param killstreak the highest killstreak
     */
    public void setKillstreak(int killstreak) {
        this.killstreak = killstreak;
    }

    /**
     * Get the current killstreak
     *
     * @return current killstreak
     */
    public int getLaststreak() {
        return laststreak;
    }

    /**
     * Get the current kdr rounded
     *
     * @return the kill death ratio rounded to an integer
     */
    public int getKdr() {
        //We can't divide by zero
        if (deaths == 0) {
            return kills;
        } else {
            return NumberConversions.round((double) kills / (double) deaths);
        }
    }

    /**
     * Increment the kills
     */
    public void incrementKills() {
        changed = true;
        //We need to use this to trigger ebean
        setKills(kills + 1);

        laststreak++;
        if (laststreak > killstreak) {
            setKillstreak(laststreak);
        }
    }

    /**
     * Increment the mob kills
     */
    public void incrementMobKills() {
        changed = true;

        setMobkills(mobkills + 1);
    }

    /**
     * Increment the deaths
     */
    public void incrementDeaths() {
        changed = true;

        laststreak = 0;
        setDeaths(deaths + 1);
    }

    /**
     * Are the stats changed.
     *
     * @return if stats are changed
     */
    public boolean isChanged() {
        return changed;
    }
}
