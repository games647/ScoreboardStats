package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.event.PlayerPointsEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerPointsVariables extends VariableReplaceAdapter<PlayerPoints> implements Listener {

    private final ReplaceManager replaceManager;

    public PlayerPointsVariables(ReplaceManager replaceManager) {
        super((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints"));

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("points".equals(variable)) {
            final int balance = getPlugin().getAPI().look(player.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPointsChange(PlayerPointsEvent changeEvent) {
        final Player player = Bukkit.getPlayer(changeEvent.getPlayerId());
        if (player != null) {
            final int lastBal = getPlugin().getAPI().look(changeEvent.getPlayerId());
            replaceManager.updateScore(player, "points", lastBal + changeEvent.getChange());
        }
    }
}
