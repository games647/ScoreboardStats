package com.github.games647.scoreboardstats.variables;

import me.sirfaizdat.prison.ranks.Ranks;
import me.sirfaizdat.prison.ranks.UserInfo;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PrisonVariables implements Replaceable {

	Economy eco;
	
	public PrisonVariables() {
		final RegisteredServiceProvider<Economy> economyProvider = Bukkit
                .getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            throw new UnsupportedPluginException("Couldn't find an economy plugin");
        } else {
            eco = economyProvider.getProvider();
        }
	}
	
	@Override
	public int getScoreValue(Player player, String variable) {
		UserInfo userInfo = Ranks.i.getUserInfo(player.getName());
		if("%moneyNeeded%".equals(variable)) {
			return (int) Math.round(userInfo.getNextRank().getPrice() - eco.getBalance(player));
		} 
		if("%currentBal%".equals(variable)) {
			return (int) Math.round(eco.getBalance(player));
		}
		return UNKOWN_VARIABLE;
	}

	
	
}
