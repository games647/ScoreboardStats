package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.config.Settings;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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
     * Add the player to the refresh queue
     *
     * @param joinEvent the join event
     * @see com.github.games647.scoreboardstats.RefreshTask
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        //add it to the refresh queue
        plugin.getRefreshTask().addToQueue(joinEvent.getPlayer());
    }

    /**
     * Check if the player moves in a scoreboard disabled world
     *
     * @param worldChange the teleport event
     * @see com.github.games647.scoreboardstats.RefreshTask
     */
    //ignore cancelled events
    @EventHandler(ignoreCancelled = true)
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
