package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import de.ftbastler.bukkitgames.api.BukkitGamesAPI;
import de.ftbastler.bukkitgames.api.PlayerBuyKitEvent;

import org.bukkit.plugin.Plugin;

@DefaultReplacer(plugin = "BukkitGames")
public class BukkitGamesVariables extends DefaultReplacers<Plugin> {

    private final BukkitGamesAPI bukkitGamesAPI = BukkitGamesAPI.getApi();

    public BukkitGamesVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("coins")
                .scoreSupply(bukkitGamesAPI::getPlayerBalance)
                .eventScore(PlayerBuyKitEvent.class, event -> {
                    int oldBalance = bukkitGamesAPI.getPlayerBalance(event.getPlayer());
                    return oldBalance - event.getKitCost();
                });
    }
}
