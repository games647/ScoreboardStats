package com.github.games647.scoreboardstats.variables;

import com.gmail.nossr50.api.ExperienceAPI;
import com.google.common.collect.ImmutableSet;

import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

/**
 * Replace all variables that are associated with the mcmmo pluin
 */
public class McmmoVariables implements ReplaceManager.Replaceable {

    private final Set<String> skillTypes;

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

    /**
     * Creates a new mcmmo replacer. This also validates if all variables are available
     * and can be used in the runtime.
     */
    public McmmoVariables() {
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (String skillVariable : skillType) {
            final String skill = skillVariable.replace("%", "").toUpperCase(Locale.ENGLISH);
            if (ExperienceAPI.isValidSkillType(skill)) {
                builder.add(skillVariable);
            } else {
                Logger.getLogger("ScoreboardStats").log(Level.INFO, "Found invalid skill. Maybe the '{0}' was removed.", skillVariable);
            }
        }

        skillTypes = builder.build();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%powlvl%".equals(variable)) {
            return ExperienceAPI.getPowerLevel(player);
        }

        if (skillTypes.contains(variable)) {
            final String type = variable.replace("%", "").toUpperCase(Locale.ENGLISH);
            return ExperienceAPI.getLevel(player, type);
        }

        return UNKOWN_VARIABLE;
    }
}
