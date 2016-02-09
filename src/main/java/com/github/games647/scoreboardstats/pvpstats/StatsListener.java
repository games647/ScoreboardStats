package com.github.games647.scoreboardstats.pvpstats;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.config.Settings;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * If enabled this class counts the kills.
 */
public class StatsListener implements Listener {

    private final ScoreboardStats plugin;
    private final Database database;

    public StatsListener(ScoreboardStats plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    /**
     * Add the player account from the database in the cache.
     *
     * @param joinEvent the join event
     * @see Database
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        //removing old metadata which wasn't removed (which can lead to memory leaks)
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
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent quitEvent) {
        Player player = quitEvent.getPlayer();

        database.saveAsync(database.getCachedStats(player));

        //just remove our metadata to prevent memory leaks
        player.removeMetadata("player_stats", plugin);
    }

    /**
     * Tracks the mob kills.
     *
     * @param event the death event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        //killer is null if it's not a player
        Player killer = entity.getKiller();

        //Check if it's not player because we are already handling it
        if (entity.getType() != EntityType.PLAYER && Settings.isPvpStats()
                && Settings.isActiveWorld(entity.getWorld().getName())) {
            PlayerStats killercache = database.getCachedStats(killer);
            if (killercache != null) {
                //If the cache entry is loaded and the player isn't null, increase the mob kills
                killercache.onMobKill();
                plugin.getReplaceManager().updateScore(killer, "mob", killercache.getMobkills());
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
        Player killed = deathEvent.getEntity();
        //killer is null if it's not a player
        Player killer = killed.getKiller();
        if (Settings.isPvpStats() && Settings.isActiveWorld(killed.getWorld().getName())) {
            PlayerStats killedcache = database.getCachedStats(killed);
            if (killedcache != null) {
                killedcache.onDeath();
                plugin.getReplaceManager().updateScore(killed, "deaths", killedcache.getDeaths());
                plugin.getReplaceManager().updateScore(killed, "kdr", killedcache.getKdr());
                //will reset
                plugin.getReplaceManager().updateScore(killed, "current_streak", killedcache.getLaststreak());
            }

            PlayerStats killercache = database.getCachedStats(killer);
            if (killercache != null) {
                killercache.onKill();
                plugin.getReplaceManager().updateScore(killer, "kills", killercache.getKills());
                plugin.getReplaceManager().updateScore(killer, "kdr", killercache.getKdr());
                //maybe the player reaches a new high score
                plugin.getReplaceManager().updateScore(killer, "killstreak", killercache.getKillstreak());
                plugin.getReplaceManager().updateScore(killer, "current_streak", killercache.getLaststreak());
            }
        }
    }
}
