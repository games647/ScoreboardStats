package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.NumberConversions;

/**
 * Replace the economy variable with Vault.
 * <p>
 * https://dev.bukkit.org/bukkit-plugins/vault/
 *
 * @see Economy
 */
@DefaultReplacer(plugin = "Vault")
public class VaultVariables extends DefaultReplacers<Plugin> {

    private final Economy economy;

    public VaultVariables(ReplacerAPI replaceManager, Plugin plugin) throws UnsupportedPluginException {
        super(replaceManager, plugin);

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            //check if an economy plugin is installed otherwise it would throw a exception if the want to replace
            throw new UnsupportedPluginException("Cannot find an economy plugin");
        } else {
            economy = economyProvider.getProvider();
        }
    }

    @Override
    public void register() {
        register("money").scoreSupply(this::getBalance);
    }

    private int getBalance(Player player) {
        return NumberConversions.round(economy.getBalance(player, player.getWorld().getName()));
    }
}
