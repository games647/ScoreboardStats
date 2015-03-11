package com.github.games647.scoreboardstats.pvpstats;

import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;
import com.avaje.ebean.validation.Pattern;
import com.avaje.ebean.validation.Range;
import com.google.common.base.Objects;

import java.sql.Timestamp;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.util.NumberConversions;

/**
 * Represents the stats from a player. The stats are kills, deaths, mobkills and
 * killstreak. All stats are annotated to be validated on runtime to be
 * not invalid.
 *
 * Maybe these stats also have to be validated to be not null, so we can prevent
 * some special cases while using it especially for SQL database, but this can
 * also occurs on using it as file database due incorrect format or
 * unexpected user modifications.
 */
@Entity
@Table(name = "player_stats")
public class PlayerStats {

    @Id
    @GeneratedValue
    private int id;

    @Nullable
    //is null in non-uuid compatible servers or isn't updated yet
    @Column(unique = true)
    private UUID uuid;

    //isn't unique since 1.7 anymore
    @NotNull
    //Tells ebean explicity that the string can only have 16 characters
    @Length(min = 2, max = 16)
    @Pattern(regex = "^\\w{2,16}$")
    private String playername = "";

    //You can't have negative stats
    @NotNull
    @Range(min = 0)
    private int kills;

    @NotNull
    @Range(min = 0)
    private int deaths;

    @NotNull
    @Range(min = 0)
    private int mobkills;

    @NotNull
    @Range(min = 0)
    private int killstreak;

    //in mysql this will be saved as datetime, but we actually only need date to calculate the difference
    @UpdatedTimestamp
    //can be null if the instance is just created and not in the database
    //ebean currently defines this column as not null
    private Timestamp lastOnline;

    private transient int laststreak;

    /**
     * Get the auto incrementing id
     *
     * @return the auto incrementing id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id. Should be only be used by eBean
     *
     * @param id database id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the UUID of this player. The database represents this one as string
     *
     * @return the UUID of the player
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of this player
     *
     * @param uuid the Mojang UUID for this player if onlinemode
     *
     * @see org.bukkit.Bukkit#getOnlineMode()
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
     * Get the current kill-death-ratio rounded
     *
     * @return the kill death ratio rounded to an integer
     */
    public int getKdr() {
        //We can't divide by zero
        if (deaths == 0) {
            return kills;
        } else {
            //Triggers float division to have decimals
            return NumberConversions.round(kills / (float) deaths);
        }
    }

    /**
     * Increment the kills
     */
    public void incrementKills() {
        //We need to use this to trigger ebean
        setKills(kills + 1);

        laststreak++;
        if (laststreak > killstreak) {
            //triggers a change for ebean
            setKillstreak(laststreak);
        }
    }

    /**
     * Increment the mob kills
     */
    public void incrementMobKills() {
        //triggers a change for ebean
        setMobkills(mobkills + 1);
    }

    /**
     * Increment the deaths
     */
    public void incrementDeaths() {
        laststreak = 0;
        //triggers a change for ebean
        setDeaths(deaths + 1);
    }

    /**
     * Get the UNIX timestamp where this entry was last updated. This implies
     * the last online value with a difference of a couple of minutes from
     * the cache.
     *
     * @return the timestamp this eBean was last updated; can be null
     */
    public Timestamp getLastOnline() {
        return lastOnline;
    }

    /**
     * Set the update timestamp value. This currently managed by eBean itself,
     * which updates the value on every save.
     *
     * @param lastOnline the player was online; can be null
     */
    public void setLastOnline(Timestamp lastOnline) {
        this.lastOnline = lastOnline;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, uuid, playername, lastOnline
                , kills, deaths, killstreak, laststreak, mobkills);
    }

    @Override
    public boolean equals(Object obj) {
        //ignores also null
        if (obj instanceof PlayerStats) {
            final PlayerStats other = (PlayerStats) obj;
            return id == other.id
                    && Objects.equal(uuid, other.uuid)
                    && Objects.equal(playername, other.playername)
                    && Objects.equal(lastOnline, other.lastOnline)
                    //don't wrap to primitive wrappers objects
                    && kills == other.kills
                    && deaths == other.deaths
                    && killstreak == other.killstreak
                    && laststreak == other.laststreak
                    && mobkills == other.mobkills;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
