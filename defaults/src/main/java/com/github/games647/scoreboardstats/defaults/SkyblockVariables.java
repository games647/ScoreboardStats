package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.github.games647.scoreboardstats.variables.UnsupportedPluginException;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;
import us.talabrek.ultimateskyblock.api.event.uSkyBlockScoreChangedEvent;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

/**
 * Replace all variables that are associated with the uSkyBlock plugin
 *
 * https://dev.bukkit.org/bukkit-plugins/uskyblock/
 */
@DefaultReplacer(plugin = "uSkyblock")
public class SkyblockVariables extends DefaultReplacers<uSkyBlockAPI> {

    private static uSkyBlockAPI getCheckVersion(Plugin plugin) throws UnsupportedPluginException {
        if (plugin instanceof uSkyBlockAPI) {
            return (uSkyBlockAPI) plugin;
        } else {
            throw new UnsupportedPluginException("Your uSkyBlock version is outdated");
        }
    }

    public SkyblockVariables(ReplacerAPI replaceManager, uSkyBlockAPI plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("island_level")
                .scoreSupply(player -> NumberConversions.round(plugin.getIslandLevel(player)))
                .eventScore(uSkyBlockScoreChangedEvent.class, this::getNewScore);
    }

    private int getNewScore(uSkyBlockScoreChangedEvent changedEvent) {
        return NumberConversions.round(changedEvent.getScore().getScore());
    }
}
