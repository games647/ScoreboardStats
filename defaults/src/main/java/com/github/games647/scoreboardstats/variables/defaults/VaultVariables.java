package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.NumberConversions;

/**
 * Replace the economy variable with Vault.
 *
 * https://dev.bukkit.org/bukkit-plugins/vault/
 *
 * @see Economy
 */
public class VaultVariables extends DefaultReplaceAdapter<Plugin> {

    private final Economy economy;
    private final Chat chat;

    /**
     * Creates a new vault replacer
     */
    public VaultVariables() {
        super(Bukkit.getPluginManager().getPlugin("Vault"), "money", "playerInfo_*");

        //Check if the server has Vault above 1.4.1 installed, because there they introduced UUID support
        checkVersionException("1.4.1");

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            //check if an economy plugin is installed otherwise it would throw a exception if the want to replace
            throw new UnsupportedPluginException("Cannot find an economy plugin");
        } else {
            economy = economyProvider.getProvider();
        }

        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (chatProvider == null) {
            chat = null;
        } else {
            chat = chatProvider.getProvider();
        }
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("money".equals(variable)) {
            double balance = economy.getBalance(player, player.getWorld().getName());
            replaceEvent.setScore(NumberConversions.round(balance));
        } else if (variable.startsWith("playerInfo_") && chat != null) {
            int playerInfo = chat.getPlayerInfoInteger(player, variable.replace("playerInfo_", ""), -1);
            replaceEvent.setScore(playerInfo);
        }
    }
}
