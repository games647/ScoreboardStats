package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.AccountManager;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

@DefaultReplacer(plugin = "CraftConomy3")
public class CraftconomyVariables extends DefaultReplacers<Plugin> {

    private final AccountManager accountManager = Common.getInstance().getAccountManager();
    private final CurrencyManager currencyManager = Common.getInstance().getCurrencyManager();

    public CraftconomyVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        if (Common.isInitialized()) {
            for (String currencyName : currencyManager.getCurrencyNames()) {
                register("money_" + currencyName)
                        .scoreSupply(player -> {
                            Currency currency = currencyManager.getCurrency(currencyName);
                            double balance = accountManager.getAccount(player.getWorld().getName(), false)
                                    .getBalance(player.getWorld().getName(), currencyName);
                            return NumberConversions.round(balance);
                        });
            }
        }
    }
}
