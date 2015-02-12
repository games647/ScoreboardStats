package com.github.games647.scoreboardstats.variables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

/**
 * Replace all variables that are associated with the uSkyBlock plugin
 */
public class SkyblockVariables implements Replaceable {

    private final uSkyBlockAPI skyBlockAPI;

    public SkyblockVariables() {
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
}
