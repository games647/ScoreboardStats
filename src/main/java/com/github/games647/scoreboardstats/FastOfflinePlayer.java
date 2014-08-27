package com.github.games647.scoreboardstats;

import com.google.common.base.Charsets;

import java.util.Map;
import java.util.UUID;

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
 * Servers below 1.7 with use of this class doesn't need to search for the
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
    public void setBanned(boolean banned) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWhitelisted() {
        return false;
    }

    @Override
    public void setWhitelisted(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Player getPlayer() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }
}
