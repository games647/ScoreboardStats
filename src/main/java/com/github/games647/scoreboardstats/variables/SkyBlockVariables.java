package com.github.games647.scoreboardstats.variables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import us.talabrek.ultimateskyblock.PlayerInfo;
import us.talabrek.ultimateskyblock.uSkyBlock;

public class SkyBlockVariables implements ReplaceManager.Replaceable {
    
    private uSkyBlock instance;

    public SkyBlockVariables() {
        initialize();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        final PlayerInfo playerInfo = instance.getActivePlayers().containsKey(player.getName()) ?
                instance.getActivePlayers().get(player.getName()) :
                instance.readPlayerFile(player.getName());

        if ("%island_level%".equals(variable)) {
            return playerInfo == null ? 0 : playerInfo.getIslandLevel();
        }

        return UNKOWN_VARIABLE;
    }

    private void initialize() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        final Plugin skyblockPlugin = pluginManager.getPlugin("uSkyBlock");
        final int version = Integer.parseInt(skyblockPlugin.getDescription().getVersion().replace(".", ""));
        if (version >= 200) {
            throw new UnsupportedPluginException();
        }

        instance = uSkyBlock.getInstance();
    }
}
