package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;

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
 *
 * http://dev.bukkit.org/bukkit-plugins/uskyblock/
 */
public class SkyblockVariables extends DefaultReplaceAdapter<uSkyBlockAPI> implements Listener {

    private static uSkyBlockAPI getCheckVersion(Plugin plugin) throws UnsupportedPluginException {
        if (plugin instanceof uSkyBlockAPI) {
            return (uSkyBlockAPI) plugin;
        } else {
            throw new UnsupportedPluginException("Your uSkyBlock version is outdated");
        }
    }

    private final ReplaceManager replaceManager;

    public SkyblockVariables(ReplaceManager replaceManager) {
        super(getCheckVersion(Bukkit.getPluginManager().getPlugin("uSkyBlock")), "island_level");

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replaceEvent.setScore(NumberConversions.round(getPlugin().getIslandLevel(player)));
        replaceEvent.setConstant(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(uSkyBlockScoreChangedEvent scoreChangeEvent) {
        Player player = scoreChangeEvent.getPlayer();
        int newLevel = NumberConversions.round(scoreChangeEvent.getScore().getScore());

        replaceManager.updateScore(player, "island_level", newLevel);
    }
}
