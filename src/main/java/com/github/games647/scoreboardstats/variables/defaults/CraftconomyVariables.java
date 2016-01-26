package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.AccountManager;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

public class CraftconomyVariables extends VariableReplaceAdapter<Plugin> {

    private final AccountManager accountManager = Common.getInstance().getAccountManager();
    private final CurrencyManager currencyManager = Common.getInstance().getCurrencyManager();

    public CraftconomyVariables() {
        super(Bukkit.getPluginManager().getPlugin("Craftconomy3"), "money_*");
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if (variable.startsWith("money_") && Common.isInitialized()) {
            Currency currency = currencyManager.getCurrency(variable.replace("money_", ""));
            double balance = accountManager.getAccount(player.getName(), false)
                    .getBalance(player.getWorld().getName(), currency.getName());
            replaceEvent.setScore(NumberConversions.round(balance));
        }
    }
}
