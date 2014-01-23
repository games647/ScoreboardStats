package com.github.games647.scoreboardstats.variables;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class FactionsVariables implements ReplaceManager.Replaceable {

    public FactionsVariables() {
        checkVersion();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        //If factions doesn't track the player yet return -1
        final UPlayer uplayer = UPlayer.get(player);
        if ("%power%".equals(variable)) {
            return uplayer == null ? -1 : uplayer.getPowerRounded();
        }

        final Faction faction = uplayer == null ? null : uplayer.getFaction();
        if ("%f_power%".equals(variable)) {
            return faction == null ? -1 : faction.getPowerRounded();
        }

        if ("%members%".equals(variable)) {
            return faction == null ? -1 : faction.getUPlayers().size();
        }

        if ("%members_online%".equals(variable)) {
            return faction == null ? -1 : faction.getOnlinePlayers().size();
        }

        return UNKOWN_VARIABLE;
    }

    private void checkVersion() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        final Plugin clansPlugin = pluginManager.getPlugin("Factions");
        
        final String versionString = clansPlugin.getDescription().getVersion().replace(".", "");
        final int version = Integer.parseInt(versionString);
        if (version < 200) {
            throw new UnsupportedPluginException();
        }
    }
}
