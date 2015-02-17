package com.github.games647.scoreboardstats.variables;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.McMMOPlayerNotFoundException;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent;
import com.google.common.collect.ImmutableSet;

import java.util.Locale;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Replace all variables that are associated with the mcMMO plugin
 */
public class McmmoVariables implements Replaceable, Listener {

    private final ReplaceManager replaceManager;
    private final Set<String> skillTypes;

    /**
     * Creates a new mcMMO replacer. This also validates if all variables are available
     * and can be used in the runtime.
     *
     * @param replaceManager to update the variables by event
     */
    public McmmoVariables(ReplaceManager replaceManager) {
        this.replaceManager = replaceManager;
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        //goes through all available skill types
        for (SkillType type : SkillType.values()) {
            final String skillName = type.name().toLowerCase(Locale.ENGLISH);
            builder.add('%' + skillName + '%');
        }

        skillTypes = builder.build();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        try {
            if ("%powlvl%".equals(variable)) {
                replaceManager.updateScore(player, variable, ExperienceAPI.getPowerLevel(player));
                return ON_EVENT;
            }

            if (skillTypes.contains(variable)) {
                final String type = variable.replace("%", "").toUpperCase(Locale.ENGLISH);
                replaceManager.updateScore(player, variable, ExperienceAPI.getLevel(player, type));
                return ON_EVENT;
            }
        } catch (McMMOPlayerNotFoundException playerNotFoundEx) {
            //player not loaded yet - fail silently
        }

        return UNKOWN_VARIABLE;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(McMMOPlayerLevelChangeEvent levelChangeEvent) {
        final Player player = levelChangeEvent.getPlayer();
        final SkillType skill = levelChangeEvent.getSkill();
        final int newSkillLevel = levelChangeEvent.getSkillLevel();

        replaceManager.updateScore(player, '%' + skill.getName() + '%', newSkillLevel);
        replaceManager.updateScore(player, "%powlvl%", ExperienceAPI.getPowerLevel(player));
    }
}