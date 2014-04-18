package com.github.games647.scoreboardstats.variables;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.NumberConversions;

/**
 * Replace the economy variable with Vault.
 */
public class VaultVariables implements ReplaceManager.Replaceable {

    private final Economy economy;

    /**
     * Creates a new vault replacer
     */
    public VaultVariables() {
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit
                .getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        } else {
            throw new UnsupportedPluginException("Couldn't find an economy plugin");
        }
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%money%".equals(variable)) {
            return NumberConversions.round(economy.getBalance(player.getName()));
        }

        return UNKOWN_VARIABLE;
    }
}
