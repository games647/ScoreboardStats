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

    private final Set<String> skillTypes;

    /**
     * Creates a new mcMMO replacer. This also validates if all variables are available
     * and can be used in the runtime.
     */
    public McmmoVariables() {
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
                return ExperienceAPI.getPowerLevel(player);
            }

            if (skillTypes.contains(variable)) {
                final String type = variable.replace("%", "").toUpperCase(Locale.ENGLISH);
                return ExperienceAPI.getLevel(player, type);
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

        String variable = '%' + skill.getName() + '%';

    }
}