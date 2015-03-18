package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.TicksPerSecondTask;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

/**
 * Replace all Bukkit variables which are the same for players. Currently
 * there is no good way to mark variables as global
 */
public class BukkitGlobalVariables extends VariableReplaceAdapter<Plugin> implements Listener {

    private final ReplaceManager replaceManager;

    public BukkitGlobalVariables(ReplaceManager replaceManager) {
        super(null, "", true, false, false, "tps", "online", "max_player");

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("tps".equals(variable)) {
            replaceEvent.setScore(NumberConversions.round(TicksPerSecondTask.getLastTicks()));
            return;
        }

        if ("online".equals(variable)) {
            replaceEvent.setScore(Bukkit.getOnlinePlayers().length);
            replaceEvent.setConstant(true);
            return;
        }

        if ("max_player".equals(variable)) {
            replaceEvent.setScore(Bukkit.getMaxPlayers());
            replaceEvent.setConstant(true);
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent joinEvent) {
        replaceManager.updateScore("online", Bukkit.getOnlinePlayers().length + 1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        replaceManager.updateScore("online", Bukkit.getOnlinePlayers().length - 1);
    }
}
