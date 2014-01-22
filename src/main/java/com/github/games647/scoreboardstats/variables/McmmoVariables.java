package com.github.games647.scoreboardstats.variables;

import com.gmail.nossr50.api.ExperienceAPI;

import java.util.Locale;

import org.bukkit.entity.Player;

public class McmmoVariables implements ReplaceManager.Replaceable {

    private final String[] skillType = new String[] {"%woodcutting%"
            , "%acrobatics%"
            , "%axes%"
            , "%excavation%"
            , "%fishing%"
            , "%herbalism%"
            , "%mining%"
            , "%repair%"
            , "%smelting%"
            , "%swords%"
            , "%taming%"
            , "%unarmed%"
            , "%archery%"};

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%powlvl%".equals(variable)) {
            return ExperienceAPI.getPowerLevel(player);
        }

        for (String variableType : skillType) {
            if (variableType.equals(variable)) {
                final String type = variableType.replace("%", "").toUpperCase(Locale.ENGLISH);
                return ExperienceAPI.getLevel(player, type);
            }
        }

        return UNKOWN_VARIABLE;
    }
}
