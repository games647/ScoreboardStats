package com.github.games647.scoreboardstats.pvp;

import com.github.games647.scoreboardstats.config.Settings;
import com.github.games647.scoreboardstats.variables.ReplaceManager;

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
        if (entity.getType() != EntityType.PLAYER && Settings.isActiveWorld(entity.getWorld().getName())) {
            PlayerStats killercache = database.getCachedStats(killer);
            if (killercache != null) {
                //If the cache entry is loaded and the player isn't null, increase the mob kills
                killercache.onMobKill();
                ReplaceManager.getInstance().updateScore(killer, "mob", killercache.getMobkills());
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
        if (killed.equals(killer)) {
            return;
        }

        if (Settings.isActiveWorld(killed.getWorld().getName())) {
            PlayerStats killedcache = database.getCachedStats(killed);
            if (killedcache != null) {
                killedcache.onDeath();
                ReplaceManager.getInstance().updateScore(killed, "deaths", killedcache.getDeaths());
                ReplaceManager.getInstance().updateScore(killed, "kdr", killedcache.getKdr());
                //will reset
                ReplaceManager.getInstance().updateScore(killed, "current_streak", killedcache.getLaststreak());
            }

            PlayerStats killercache = database.getCachedStats(killer);
            if (killercache != null) {
                killercache.onKill();
                ReplaceManager.getInstance().updateScore(killer, "kills", killercache.getKills());
                ReplaceManager.getInstance().updateScore(killer, "kdr", killercache.getKdr());
                //maybe the player reaches a new high score
                ReplaceManager.getInstance().updateScore(killer, "killstreak", killercache.getKillstreak());
                ReplaceManager.getInstance().updateScore(killer, "current_streak", killercache.getLaststreak());
            }
        }
    }
}
