package com.github.games647.scoreboardstats.variables;

import com.gmail.nossr50.api.ExperienceAPI;

import org.bukkit.entity.Player;

public class McmmoVariables implements ReplaceManager.Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%powlvl%".equals(variable)) {
            return ExperienceAPI.getPowerLevel(player);
        }

        if ("%woodcutting%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "WOODCUTTING");
        }

        if ("%acrobatics%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "ACROBATICS");
        }

        if ("%archery%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "ARCHERY");
        }

        if ("%axes%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "AXES");
        }

        if ("%excavation%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "EXCAVATION");
        }

        if ("%fishing%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "FISHING");
        }

        if ("%herbalism%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "HERBALISM");
        }

        if ("%mining%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "MINING");
        }

        if ("%repair%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "REPAIR");
        }

        if ("%smelting%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "SMELTING");
        }

        if ("%swords%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "SWORDS");
        }

        if ("%taming%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "TAMING");
        }

        if ("%unarmed%".equals(variable)) {
            return ExperienceAPI.getLevel(player, "UNARMED");
        }

        return UNKOWN_VARIABLE;
    }
}
