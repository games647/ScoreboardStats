package com.github.games647.scoreboardstats.pvpstats;

import java.time.Instant;
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

    private final UUID uuid;
    private String playername;

    //You can't have negative stats
    private int kills;
    private int deaths;
    private int mobkills;
    private int killstreak;

    private Instant lastOnline;

    private int laststreak;

    public PlayerStats(int id, UUID uuid, String playername,
            int kills, int deaths, int mobkills, int killstreak, Instant lastOnline) {
        this(uuid, playername);

        this.id = id;
        this.kills = kills;
        this.deaths = deaths;
        this.mobkills = mobkills;
        this.killstreak = killstreak;
        this.lastOnline = lastOnline;
    }

    public PlayerStats(UUID uuid, String playername) {
        this.uuid = uuid;
        this.playername = playername;
    }

    /**
     * Get the auto incrementing id
     *
     * @return the auto incrementing id
     */
    public int getId() {
        return id;
    }

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
     * Get the UNIX timestamp where this entry was last updated.
     *
     * @return the timestamp this was last saved; can be null
     */
    @Deprecated
    public long getLastOnline() {
        return lastOnline.toEpochMilli();
    }

    public Instant getLastOnlineDate() {
        return lastOnline;
    }

    public void setLastOnline(Instant lastOnline) {
        this.lastOnline = lastOnline;
    }

    /**
     * Set the update timestamp value. This value is updated on every save.
     *
     * @param lastOnline the player was online; can be null
     */
    @Deprecated
    public void setLastOnline(long lastOnline) {
        this.lastOnline = Instant.ofEpochMilli(lastOnline);
    }

    /**
     * Increment the kills
     */
    public void onKill() {
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
        mobkills++;
    }

    /**
     * Increment the deaths
     */
    public void onDeath() {
        laststreak = 0;
        deaths++;
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
