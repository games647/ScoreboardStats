package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.config.Settings;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        if (Settings.isActiveWorld(player.getWorld().getName())) {
            //add it to the refresh queue
            plugin.getRefreshTask().addToQueue(joinEvent.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent quitEvent) {
        Player player = quitEvent.getPlayer();
        plugin.getRefreshTask().remove(player);
        plugin.getScoreboardManager().unregister(player);
    }

    /**
     * Check if the player moves in a scoreboard disabled world
     *
     * @param worldChange the teleport event
     * @see com.github.games647.scoreboardstats.RefreshTask
     */
    //ignore cancelled events
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onWorldChange(PlayerChangedWorldEvent worldChange) {
        Player player = worldChange.getPlayer();
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
