package com.github.games647.scoreboardstats.listener;

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

/**
 * Listening to players events.
 */
public class PlayerListener implements Listener {

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
     * Add the player to the refresh queue and load if stats is enable the account
     * from the database in the cache.
     *
     * @param join the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent join) {
        final Player player = join.getPlayer();
        //Add the player to the refresh queue and/or load the stats
        Database.loadAccount(player);
        ScoreboardStats.getInstance().getRefreshTask().addToQueue(player);
    }

    /**
     * Check if the player moves in a scoreboard disabled world
     * 
     * @param teleport the teleport event
     */
    @EventHandler(ignoreCancelled = true)
    public void onChange(PlayerChangedWorldEvent teleport) {
        final Player player = teleport.getPlayer();
        if (Settings.isActiveWorld(player.getWorld())) {
            //Activate the scoreboard if it was disabled
            if (!Settings.isActiveWorld(teleport.getFrom())) {
                ScoreboardStats.getInstance().getRefreshTask().addToQueue(player);
            }
        } else if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null
                && player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().startsWith("Stats")) {
            //Disable the scoreboard if the player goes in a disabled world
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}
