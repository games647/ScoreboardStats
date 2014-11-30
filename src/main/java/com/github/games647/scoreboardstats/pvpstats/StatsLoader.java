package com.github.games647.scoreboardstats.pvpstats;

import java.lang.ref.WeakReference;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class is used for loading the player stats.
 */
public class StatsLoader implements Runnable {

    private final Plugin plugin;
    private final boolean uuidSearch;
    private final boolean uuidCompatible;

    private final WeakReference<Player> weakPlayer;

    /**
     * Creates a new loader for a specific player
     *
     * @param plugin
     * @param uuidSearch should it be searched by uuid
     * @param uuidCompatible if server version is above 1.7.2 and so uses mojang uuids
     * @param player player instance
     */
    public StatsLoader(Plugin plugin, boolean uuidSearch, boolean uuidCompatible, Player player) {
        this.plugin = plugin;
        this.uuidSearch = uuidSearch;
        this.uuidCompatible = uuidCompatible;

        //don't prevent the garbage collection of this player if he logs out
        this.weakPlayer = new WeakReference<Player>(player);
    }

    @Override
    public void run() {
        final Player player = weakPlayer.get();
        if (player != null) {
            final PlayerStats stats = Database.loadAccount(uuidSearch ? player.getUniqueId() : player.getName());
            //update player name on every load, because it's changeable
            stats.setPlayername(player.getName());
            if (uuidCompatible) {
                //Set the uuid if the server is uuid compatible
                stats.setUuid(player.getUniqueId());
            }

            new BukkitRunnable() {

                @Override
                public void run() {
                    //possible not thread-safe, so reschedule it while setMetadata is thread-safe
                    if (player.isOnline()) {
                        //sets it only if the player is only
                        player.setMetadata("player_stats", new CachedPlayerStats(plugin, stats));
                    }
                }
            }.runTask(plugin);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
