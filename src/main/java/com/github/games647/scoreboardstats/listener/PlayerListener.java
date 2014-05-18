package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.RefreshTask;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

/**
 * Listening to players events.
 */
public class PlayerListener implements Listener {

    /**
     * Add the player to the refresh queue and load if stats is enable the account
     * from the database in the cache.
     *
     * @param joinEvent the join event
     * @see Database
     * @see RefreshTask
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();
        //Add the player to the refresh queue and/or load the stats
        Database.loadAccount(player);
        ScoreboardStats.getInstance().getRefreshTask().addToQueue(player);
    }

    /**
     * Tracks player deaths and kills
     *
     * @param deathEvent the death event.
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent deathEvent) {
        final Player killed = deathEvent.getEntity();
        final Player killer = killed.getKiller();
        if (Settings.isPvpStats() && Settings.isActiveWorld(killed.getWorld())) {
            final PlayerStats killedcache = Database.getCacheIfAbsent(killed);
            if (killedcache != null) {
                killedcache.incrementDeaths();
            }

            final PlayerStats killercache = Database.getCacheIfAbsent(killer);
            if (killercache != null) {
                killercache.incrementKills();
            }
        }
    }


    /**
     * Check if the player moves in a scoreboard disabled world
     *
     * @param worldChange the teleport event
     * @see RefreshTask
     */
    @EventHandler(ignoreCancelled = true)
    public void onChange(PlayerChangedWorldEvent worldChange) {
        final Player player = worldChange.getPlayer();
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (Settings.isActiveWorld(player.getWorld())) {
            if (!Settings.isActiveWorld(worldChange.getFrom())) {
                //Activate the scoreboard if it was disabled
                ScoreboardStats.getInstance().getRefreshTask().addToQueue(player);
            }
        } else if (objective != null && objective.getName().startsWith("Stats")) {
            //Disable the scoreboard if the player goes in a disabled world
            ScoreboardStats.getInstance().getRefreshTask().remove(player);
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}
