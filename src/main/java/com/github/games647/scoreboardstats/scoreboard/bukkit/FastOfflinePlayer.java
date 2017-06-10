package com.github.games647.scoreboardstats.scoreboard.bukkit;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Since 1.7 Bukkit introduced CraftServer.getOfflinePlayer(String name).
 *
 * This method is blocking as it looks up the cache. So it could cause lags
 * on big servers. Because we only need the name this class is made.
 *
 * Bukkit already implemented methods to get around this problem, but the
 * scoreboard team isn't yet modified for methods, which can be called just
 * with a string.
 *
 * Servers <b>below 1.7</b> with use of this class doesn't need to search for the
 * player.
 *
 * @see org.bukkit.Bukkit#getOfflinePlayer(java.lang.String)
 */
public class FastOfflinePlayer implements OfflinePlayer {

    private final String playerName;

    /**
     * Creates a new instance based of this name
     *
     * @param playerName the player name
     */
    public FastOfflinePlayer(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public boolean isOnline() {
        //it's a fake player, so it won't be online
        return false;
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public UUID getUniqueId() {
        return UUID.nameUUIDFromBytes(playerName.getBytes(Charsets.UTF_8));
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public boolean isWhitelisted() {
        return false;
    }

    @Override
    public void setWhitelisted(boolean value) {
        //ignore
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public long getFirstPlayed() {
        return System.currentTimeMillis();
    }

    @Override
    public long getLastPlayed() {
        return System.currentTimeMillis();
    }

    @Override
    public boolean hasPlayedBefore() {
        return false;
    }

    @Override
    public Location getBedSpawnLocation() {
        return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {
        //ignore
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = Maps.newLinkedHashMap();

        result.put("UUID", getUniqueId().toString());
        result.put("name", playerName);

        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerName);
    }

    @Override
    public boolean equals(Object obj) {
        //check for null too
        if (!(obj instanceof FastOfflinePlayer)) {
            return false;
        }

        FastOfflinePlayer other = (FastOfflinePlayer) obj;
        return Objects.equal(playerName, other.playerName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("playerName", playerName)
                .toString();
    }
}
