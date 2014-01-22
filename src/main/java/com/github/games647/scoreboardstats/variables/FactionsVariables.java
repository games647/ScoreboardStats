package com.github.games647.scoreboardstats.variables;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;

import org.bukkit.entity.Player;

public class FactionsVariables implements ReplaceManager.Replaceable {

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
}
