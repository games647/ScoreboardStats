package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;

public class PlayerListener implements Listener {

    /**
     * Tracks player deaths and kills
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent deathEvent) {
        final Player killed = deathEvent.getEntity();
        final Player killer = killed.getKiller();
        if (Settings.isPvpStats() && !Settings.isDisabledWorld(killed.getWorld())) {
            final PlayerCache killedcache = Database.getCacheIfAbsent(killed);
            if (killedcache != null) {
                killedcache.incrementDeaths();
            }

            final PlayerCache killercache = Database.getCacheIfAbsent(killer);
            if (killercache != null) {
                killercache.incrementKills();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent join) {
        //Add the player to the refresh queue and/or load the stats
        final Player player = join.getPlayer();
        Database.loadAccount(player.getName());
        ScoreboardStats.getInstance().getRefreshTask().addToQueue(player);
    }

    /**
     * Checks if the player moves in a scoreboard disabled world
     */
    @EventHandler(ignoreCancelled = true)
    public void onChange(PlayerChangedWorldEvent teleport) {
        final Player player = teleport.getPlayer();
        //Disable the scoreboard if the player is in a disabled world
        if (Settings.isDisabledWorld(player.getWorld())) {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}
