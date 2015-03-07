package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableSet;

import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Replace all variables that are associated with the mcMMO plugin
 */
public class McmmoVariables extends VariableReplaceAdapter<mcMMO> implements Listener {

    private final ReplaceManager replaceManager;
    private final Set<String> skillTypes;

    /**
     * Creates a new mcMMO replacer. This also validates if all variables are available
     * and can be used in the runtime.
     *
     * @param replaceManager to update the variables by event
     */
    public McmmoVariables(ReplaceManager replaceManager) {
        super((mcMMO) Bukkit.getPluginManager().getPlugin("mcMMO"));

        this.replaceManager = replaceManager;

        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        //goes through all available skill types
        for (SkillType type : SkillType.values()) {
            final String skillName = type.name().toLowerCase(Locale.ENGLISH);
            builder.add(skillName);
        }

        skillTypes = builder.build();
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("powlvl".equals(variable)) {
            //check if player is loaded
            if (UserManager.hasPlayerDataKey(player)) {
                replaceEvent.setScore(ExperienceAPI.getPowerLevel(player));
                replaceEvent.setConstant(true);
            } else {
                replaceEvent.touch();
            }
        }

        if (skillTypes.contains(variable)) {
            if (UserManager.hasPlayerDataKey(player)) {
                final String type = variable.toUpperCase(Locale.ENGLISH);
                replaceEvent.setScore(ExperienceAPI.getLevel(player, type));
                replaceEvent.setConstant(true);
            } else {
                replaceEvent.touch();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(McMMOPlayerLevelChangeEvent levelChangeEvent) {
        final Player player = levelChangeEvent.getPlayer();
        final SkillType skill = levelChangeEvent.getSkill();
        final int newSkillLevel = levelChangeEvent.getSkillLevel();

        replaceManager.updateScore(player, skill.getName(), newSkillLevel);
        replaceManager.updateScore(player, "powlvl", ExperienceAPI.getPowerLevel(player));
    }
}