package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

/**
 * Listening to players events.
 */
public class PlayerListener implements Listener {

    private final ScoreboardStats plugin;

    /**
     * Creates a new player listener
     *
     * @param plugin ScoreboardStats plugin
     */
    public PlayerListener(ScoreboardStats plugin) {
        this.plugin = plugin;
    }

    /**
     * Add the player to the refresh queue and load if stats is enabled
     * the account from the database in the cache.
     *
     * @param joinEvent the join event
     * @see Database
     * @see RefreshTask
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();
        //load the pvpstats if activated
        Database.loadAccountAsync(player);
        //add it to the refresh queue
        plugin.getRefreshTask().addToQueue(player);
    }

    /**
     * Saves the stats to database if the player laves
     *
     * @param quitEvent leave event
     * @see Database
     * @see RefreshTask
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        //gladly the event will be cancelled on every player quit
        final Player player = quitEvent.getPlayer();

        final List<MetadataValue> metadata = player.getMetadata("player_stats");
        //can be null if that metadata doesn't exist
        if (metadata != null) {
            for (MetadataValue metadataValue : metadata) {
                //just remove our metadata
                if (metadataValue.getOwningPlugin().equals(plugin)) {
                    metadataValue.invalidate();
                }
            }
        }

        player.removeMetadata("player_stats", plugin);
    }

    /**
     * Tracks player deaths and kills
     *
     * @param deathEvent the death event.
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent deathEvent) {
        final Player killed = deathEvent.getEntity();
        //killer is null if it's not a player
        final Player killer = killed.getKiller();
        if (Settings.isPvpStats() && Settings.isActiveWorld(killed.getWorld().getName())) {
            final PlayerStats killedcache = Database.getCachedStats(killed);
            if (killedcache != null) {
                killedcache.incrementDeaths();
            }

            final PlayerStats killercache = Database.getCachedStats(killer);
            if (killercache != null) {
                killercache.incrementKills();
            }
        }
    }


    /**
     * Check if the player moves in a scoreboard disabled world
     *
     * @param worldChange the teleport event
     * @see com.github.games647.scoreboardstats.RefreshTask
     */
    //ignore cancelled events
    @EventHandler(ignoreCancelled = true)
    public void onChange(PlayerChangedWorldEvent worldChange) {
        final Player player = worldChange.getPlayer();
        //new world
        if (Settings.isActiveWorld(player.getWorld().getName())) {
            //old world
            if (!Settings.isActiveWorld(worldChange.getFrom().getName())) {
                //Activate the scoreboard if it was disabled
                plugin.getRefreshTask().addToQueue(player);
            }
        } else {
            //Disable the scoreboard if the player goes into a disabled world
            plugin.getRefreshTask().remove(player);
            plugin.getScoreboardManager().unregister(player);
        }
    }
}
