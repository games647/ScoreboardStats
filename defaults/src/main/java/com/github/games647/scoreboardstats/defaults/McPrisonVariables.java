package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;

import me.sirfaizdat.prison.ranks.Ranks;
import me.sirfaizdat.prison.ranks.UserInfo;
import me.sirfaizdat.prison.ranks.events.BalanceChangeEvent;
import me.sirfaizdat.prison.ranks.events.DemoteEvent;
import me.sirfaizdat.prison.ranks.events.RankupEvent;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Replace all variables that are associated with the prison plugin
 * <p>
 * https://dev.bukkit.org/bukkit-plugins/mcprison/
 */
@DefaultReplacer(plugin = "Prison")
public class McPrisonVariables extends DefaultReplacers<Plugin> {

    private final Economy eco;

    public McPrisonVariables(ReplacerAPI replaceManager, Plugin plugin) throws UnsupportedPluginException {
        super(replaceManager, plugin);

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            throw new UnsupportedPluginException("Couldn't find an economy plugin");
        } else {
            eco = economyProvider.getProvider();
        }
    }

    @Override
    public void register() {
        register("moneyNeeded").scoreSupply(this::getMoneyNeeded)
                .eventScore(RankupEvent.class, event -> getMoneyNeeded(event.getPlayer()))
                .eventScore(BalanceChangeEvent.class, event -> getMoneyNeeded(event.getPlayer()))
                .eventScore(DemoteEvent.class, event -> getMoneyNeeded(event.getPlayer()));
    }

    private int getMoneyNeeded(Player player) {
        UserInfo userInfo = Ranks.i.getUserInfo(player.getName());
        return (int) (userInfo.getNextRank().getPrice() - eco.getBalance(player));
    }
}
