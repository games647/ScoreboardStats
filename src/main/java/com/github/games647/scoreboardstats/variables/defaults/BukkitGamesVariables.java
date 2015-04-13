package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import de.ftbastler.bukkitgames.api.PlayerBuyKitEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BukkitGamesVariables extends VariableReplaceAdapter<Plugin> implements Listener {

    private final ReplaceManager replaceManager;
    private final BukkitGamesAPI bukkitGamesAPI = BukkitGamesAPI.getApi();

    public BukkitGamesVariables(ReplaceManager replaceManager) {
        super(Bukkit.getPluginManager().getPlugin("BukkitGames"), "coins");

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replaceEvent.setConstant(true);
        replaceEvent.setScore(bukkitGamesAPI.getPlayerBalance(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onKitBuy(PlayerBuyKitEvent buyKitEvent) {
        final Player player = buyKitEvent.getPlayer();
        final int newBalance = bukkitGamesAPI.getPlayerBalance(player) - buyKitEvent.getKitCost();
        replaceManager.updateScore(player, "coins", newBalance);
    }
}
