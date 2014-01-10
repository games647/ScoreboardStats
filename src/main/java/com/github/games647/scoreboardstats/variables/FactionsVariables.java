package com.github.games647.scoreboardstats.variables;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;

import org.bukkit.entity.Player;

public class FactionsVariables implements ReplaceManager.Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        final UPlayer uplayer = UPlayer.get(player);
        if ("%power%".equals(variable)) {
            return uplayer.getPowerRounded();
        }

        final Faction faction = uplayer.getFaction();
        if (faction != null) {
            if ("%f_power%".equals(variable)) {
                return faction.getPowerRounded();
            }

            if ("%members%".equals(variable)) {
                return faction.getUPlayers().size();
            }

            if ("%members_online%".equals(variable)) {
                return faction.getOnlinePlayers().size();
            }
        }

        return UNKOWN_VARIABLE;
    }
}
