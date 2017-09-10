package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;

import me.sirfaizdat.prison.ranks.Ranks;
import me.sirfaizdat.prison.ranks.UserInfo;
import me.sirfaizdat.prison.ranks.events.BalanceChangeEvent;
import me.sirfaizdat.prison.ranks.events.DemoteEvent;
import me.sirfaizdat.prison.ranks.events.RankupEvent;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Replace all variables that are associated with the prison plugin
 *
 * https://dev.bukkit.org/bukkit-plugins/mcprison/
 */
public class McPrisonVariables extends DefaultReplaceAdapter<Plugin> {

    private final ReplaceManager replaceManager;

    private final Economy eco;

    public McPrisonVariables(ReplaceManager replaceManager) {
        super(Bukkit.getPluginManager().getPlugin("Prison"), "moneyNeeded");

        this.replaceManager = replaceManager;

        RegisteredServiceProvider<Economy> economyProvider = Bukkit
                .getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            throw new UnsupportedPluginException("Couldn't find an economy plugin");
        } else {
            eco = economyProvider.getProvider();
        }
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replaceEvent.setScore(getMoneyNeeded(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBalanceChange(BalanceChangeEvent balanceChangeEvent) {
        Player player = balanceChangeEvent.getPlayer();
        replaceManager.updateScore(player, "moneyNeeded", getMoneyNeeded(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onRankup(RankupEvent rankupEvent) {
        Player player = rankupEvent.getPlayer();
        replaceManager.updateScore(player, "moneyNeeded", getMoneyNeeded(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDemoteEvent(DemoteEvent demoteEvent) {
        Player player = demoteEvent.getPlayer();
        replaceManager.updateScore(player, "moneyNeeded", getMoneyNeeded(player));
    }

    private int getMoneyNeeded(Player player) {
        UserInfo userInfo = Ranks.i.getUserInfo(player.getName());
        return (int) (userInfo.getNextRank().getPrice() - eco.getBalance(player));
    }
}
