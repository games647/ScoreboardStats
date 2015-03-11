package com.github.games647.scoreboardstats.pvpstats;

import com.github.games647.scoreboardstats.config.Settings;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * If enabled this class counts the kills.
 */
public class StatsListener implements Listener {

    private final Plugin plugin;
    private final Database database;

    public StatsListener(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    /**
     * Add the player account from the database in the cache.
     *
     * @param joinEvent the join event
     * @see Database
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();
        player.removeMetadata("player_stats", plugin);

        //load the pvpstats if activated
        database.loadAccountAsync(player);
    }

    /**
     * Saves the stats to database if the player leaves
     *
     * @param quitEvent leave event
     * @see Database
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        final Player player = quitEvent.getPlayer();
        for (MetadataValue metadataValue : player.getMetadata("player_stats")) {
            //just remove our metadata
            database.saveAsync((PlayerStats) metadataValue.value());
        }

        player.removeMetadata("player_stats", plugin);
    }

    /**
     * Tracks the mob kills.
     *
     * @param event the death event
     * @see PlayerListener#onDeath(org.bukkit.event.entity.PlayerDeathEvent)
     */
    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        //killer is null if it's not a player
        final Player killer = entity.getKiller();

        //Check if it's not player because we are already handling it
        if (entity.getType() != EntityType.PLAYER && Settings.isPvpStats()
                && Settings.isActiveWorld(entity.getWorld().getName())) {
            final PlayerStats killercache = database.getCachedStats(killer);
            if (killercache != null) {
                //If the cache entry is loaded and the player isn't null, increase the mob kills
                killercache.incrementMobKills();
            }
        }
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
            final PlayerStats killedcache = database.getCachedStats(killed);
            if (killedcache != null) {
                killedcache.incrementDeaths();
            }

            final PlayerStats killercache = database.getCachedStats(killer);
            if (killercache != null) {
                killercache.incrementKills();
            }
        }
    }
}
