package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Version;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.NumberConversions;

/**
 * Replace the economy variable with Vault.
 */
public class VaultVariables implements Replaceable {

    private final Economy economy;

    /**
     * Creates a new vault replacer
     */
    public VaultVariables() {
        checkVersion();

        final RegisteredServiceProvider<Economy> economyProvider = Bukkit
                .getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            throw new UnsupportedPluginException("Couldn't find an economy plugin");
        } else {
            economy = economyProvider.getProvider();
        }
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%money%".equals(variable)) {
            return NumberConversions.round(economy.getBalance(player, player.getWorld().getName()));
        }

        return UNKOWN_VARIABLE;
    }

    private void checkVersion() {
        final Plugin vaultPlugin = Bukkit.getPluginManager().getPlugin("Vault");
        final String version = vaultPlugin.getDescription().getVersion();
        final int end = version.indexOf('-');
        final String cleanVersion = version.substring(0, end == -1 ? version.length() : end);
        if (Version.compare("1.4.1", cleanVersion) < 0) {
            throw new UnsupportedPluginException("You have an outdated version of Vault. Please update it");
        }
    }
}
