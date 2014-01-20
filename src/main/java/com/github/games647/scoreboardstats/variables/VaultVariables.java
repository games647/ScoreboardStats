package com.github.games647.scoreboardstats.variables;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultVariables implements ReplaceManager.Replaceable {

    private Economy economy;

    public VaultVariables() {
        setupEconomy();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%money%".equals(variable)) {
            return (int) Math.round(economy.getBalance(player.getName()));
        }

        return UNKOWN_VARIABLE;
    }

    private boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit
                .getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return economy != null;
    }
}
