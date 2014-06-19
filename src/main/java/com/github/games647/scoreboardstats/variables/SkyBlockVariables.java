package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import us.talabrek.ultimateskyblock.PlayerInfo;
import us.talabrek.ultimateskyblock.uSkyBlock;

/**
 * Replace all variables that are associated with the skyblock plugin
 */
public class SkyBlockVariables implements ReplaceManager.Replaceable {

    private final uSkyBlock instance;

    /**
     * Creates a new skyblock replacer
     */
    public SkyBlockVariables() {
        final Plugin skyblockPlugin = Bukkit.getPluginManager().getPlugin("uSkyBlock");
        final String version = skyblockPlugin.getDescription().getVersion();
        if (Version.compare("2", version) <= 0) {
            throw new UnsupportedPluginException("Version over 2.0 is not supported");
        }

        instance = uSkyBlock.getInstance();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        final PlayerInfo playerInfo = instance.getActivePlayers().get(player.getName());
        if ("%island_level%".equals(variable)) {
            return playerInfo == null ? 0 : playerInfo.getIslandLevel();
        }

        return UNKOWN_VARIABLE;
    }
}
