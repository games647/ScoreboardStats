package com.github.games647.scoreboardstats.pvpstats;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.util.NumberConversions;

/**
 * Represents the stats from a player. The stats are kills, deaths, mobkills and killstreak. All stats are annotated to
 * be validated on runtime to be not invalid.
 *
 * Maybe these stats also have to be validated to be not null, so we can prevent some special cases while using it
 * especially for SQL database, but this can also occurs on using it as file database due incorrect format or unexpected
 * user modifications.
 */
public class PlayerStats {

    private int id = -1;

    //is null in non-uuid compatible servers or isn't updated yet
    private UUID uuid;

    //isn't unique since 1.7 anymore
    private String playername;

    //You can't have negative stats
    private int kills;
    private int deaths;
    private int mobkills;
    private int killstreak;

    private long lastOnline;

    private transient int laststreak;
    private transient boolean modified;

    public PlayerStats(int id, UUID uuid, String playername,
            int kills, int deaths, int mobkills, int killstreak, long lastOnline) {
        this.id = id;
        this.uuid = uuid;
        this.playername = playername;
        this.kills = kills;
        this.deaths = deaths;
        this.mobkills = mobkills;
        this.killstreak = killstreak;
        this.lastOnline = lastOnline;
    }

    public PlayerStats() {

    }

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
     * Get the deaths
     *
     * @return the deaths
     */
    public int getDeaths() {
        return deaths;
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
     * Get the highest killstreak
     *
     * @return the highest killstreak
     */
    public int getKillstreak() {
        return killstreak;
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
     * Get the UNIX timestamp where this entry was last updated. This implies the last online value with a difference of
     * a couple of minutes from the cache.
     *
     * @return the timestamp this eBean was last updated; can be null
     */
    public long getLastOnline() {
        return lastOnline;
    }

    /**
     * Set the update timestamp value. This currently managed by eBean itself, which updates the value on every save.
     *
     * @param lastOnline the player was online; can be null
     */
    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    /**
     * Increment the kills
     */
    public void onKill() {
        modified = true;

        //We need to use this to trigger ebean
        kills++;

        laststreak++;
        if (laststreak > killstreak) {
            killstreak = laststreak;
        }
    }

    /**
     * Increment the mob kills
     */
    public void onMobKill() {
        modified = true;

        mobkills++;
    }

    /**
     * Increment the deaths
     */
    public void onDeath() {
        modified = true;

        laststreak = 0;
        deaths++;
    }

    public boolean isModified() {
        return modified;
    }

    public boolean isNew() {
        return id == -1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, playername);
    }

    @Override
    public boolean equals(Object obj) {
        //ignores also null
        if (obj instanceof PlayerStats) {
            PlayerStats other = (PlayerStats) obj;
            return id == other.id
                    && Objects.equals(uuid, other.uuid)
                    && Objects.equals(playername, other.playername);
        }

        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
