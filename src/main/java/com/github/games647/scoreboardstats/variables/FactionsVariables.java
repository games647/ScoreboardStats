package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Version;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.entity.UPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Replace all variables that are associated with the faction plugin
 */
public class FactionsVariables implements ReplaceManager.Replaceable {

    private final boolean newVersion;

    /**
     * Creates a new faction replacer
     */
    public FactionsVariables() {
        final Plugin factionsPlugin = Bukkit.getPluginManager().getPlugin("Factions");
        final String version = factionsPlugin.getDescription().getVersion();
        newVersion = Version.compare(version, "2") >= 0;
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        if (newVersion) {
            return getNewFactionScore(player, variable);
        } else {
            return getOldFactionScore(player, variable);
        }
    }

    private int getNewFactionScore(Player player, String variable) {
        //If factions doesn't track the player yet return -1
        final UPlayer uplayer = UPlayer.get(player);
        if ("%power%".equals(variable)) {
            return uplayer == null ? -1 : uplayer.getPowerRounded();
        }

        final com.massivecraft.factions.entity.Faction faction = uplayer == null ? null : uplayer.getFaction();
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

    private int getOldFactionScore(Player player, String variable) {
        //If factions doesn't track the player yet return -1
        final FPlayer fPlayer = FPlayers.i.get(player);
        if ("%power%".equals(variable)) {
            return fPlayer == null ? -1 : fPlayer.getPowerRounded();
        }

        final com.massivecraft.factions.Faction faction = fPlayer == null ? null : fPlayer.getFaction();
        if ("%f_power%".equals(variable)) {
            return faction == null ? -1 : faction.getPowerRounded();
        }

        if ("%members%".equals(variable)) {
            return faction == null ? -1 : faction.getFPlayers().size();
        }

        if ("%members_online%".equals(variable)) {
            return faction == null ? -1 : faction.getOnlinePlayers().size();
        }

        return UNKOWN_VARIABLE;
    }
}
