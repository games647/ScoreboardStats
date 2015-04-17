package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.Version;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

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
 * http://dev.bukkit.org/bukkit-plugins/vault/
 *
 * @see Economy
 */
public class VaultVariables extends VariableReplaceAdapter<Plugin> {

    private final Economy economy;
    private final Chat chat;

    /**
     * Creates a new vault replacer
     */
    public VaultVariables() {
        super(Bukkit.getPluginManager().getPlugin("Vault"), "money", "playerInfo_*");

        checkVersion();

        final RegisteredServiceProvider<Economy> economyProvider = Bukkit
                .getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            //check if an economy plugin is installed otherwise it would throw a exception if the want to replace
            throw new UnsupportedPluginException("Cannot find an economy plugin");
        } else {
            economy = economyProvider.getProvider();
        }

        final RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (chatProvider == null) {
            chat = null;
        } else {
            chat = chatProvider.getProvider();
        }
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("money".equals(variable)) {
            final double balance = economy.getBalance(player, player.getWorld().getName());
            replaceEvent.setScore(NumberConversions.round(balance));
        } else if (variable.startsWith("playerInfo_") && chat != null) {
            final int playerInfo = chat.getPlayerInfoInteger(player, variable.replace("playerInfo_", ""), -1);
            replaceEvent.setScore(playerInfo);
        }
    }

    /**
     * Check if the server has Vault above 1.4.1 installed, because there they
     * introduced UUID support, but this doesn't make Vault incompatible with
     * older Minecraft versions
     *
     * @see Economy#getBalance(org.bukkit.OfflinePlayer)
     */
    private void checkVersion() {
        final String version = getPlugin().getDescription().getVersion();
        int end = version.indexOf('-');
        if (end == -1) {
            end = version.length();
        }

        final String cleanVersion = version.substring(0, end);
        if (Version.compare("1.4.1", cleanVersion) < 0) {
            throw new UnsupportedPluginException("You have an outdated version of Vault. Please update it");
        }
    }
}
