package com.github.games647.scoreboardstats.variables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

import us.talabrek.ultimateskyblock.api.event.uSkyBlockScoreChangedEvent;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

/**
 * Replace all variables that are associated with the uSkyBlock plugin
 */
public class SkyblockVariables implements Replaceable, Listener {

    private final ReplaceManager replaceManager;
    private final uSkyBlockAPI skyBlockAPI;

    public SkyblockVariables(ReplaceManager replaceManager) {
        this.replaceManager = replaceManager;

        final Plugin plugin = Bukkit.getPluginManager().getPlugin("uSkyBlock");
        if (plugin instanceof uSkyBlockAPI) {
            skyBlockAPI = (uSkyBlockAPI) plugin;
        } else {
            throw new UnsupportedPluginException("Your uSkyBlock version is outdated");
        }
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%island_level%".equals(variable)) {
            return NumberConversions.round(skyBlockAPI.getIslandLevel(player));
        }

        return UNKOWN_VARIABLE;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(uSkyBlockScoreChangedEvent scoreChangeEvent) {
        final Player player = scoreChangeEvent.getPlayer();
        final int newLevel = NumberConversions.round(scoreChangeEvent.getScore().getScore());

        replaceManager.updateScore(player, "%island_level%", newLevel);
    }
}
