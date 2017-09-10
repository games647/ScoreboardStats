package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.util.player.UserManager;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Replace all variables that are associated with the mcMMO plugin
 *
 * https://dev.bukkit.org/bukkit-plugins/mcmmo/
 */
public class McmmoVariables extends DefaultReplaceAdapter<Plugin> implements Listener {

    private static String[] getSkillVariables() {
        Set<String> skills = Stream.of(SkillType.values()).map(SkillType::name)
                .map(String::toLowerCase).collect(Collectors.toSet());

        skills.add("powlvl");
        return skills.toArray(new String[0]);
    }

    private final ReplaceManager replaceManager;

    /**
     * Creates a new mcMMO replacer. This also validates if all variables are available
     * and can be used in the runtime.
     *
     * @param replaceManager to update the variables by event
     */
    public McmmoVariables(ReplaceManager replaceManager) {
        super(Bukkit.getPluginManager().getPlugin("mcMMO"), getSkillVariables());

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replaceEvent.setConstant(true);
        if (!UserManager.hasPlayerDataKey(player)) {
            //check if player is loaded
            return;
        }

        if ("powlvl".equals(variable)) {
            replaceEvent.setScore(ExperienceAPI.getPowerLevel(player));
        } else {
            String type = variable.toUpperCase(Locale.ENGLISH);
            replaceEvent.setScore(ExperienceAPI.getLevel(player, type));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(McMMOPlayerLevelUpEvent levelUpEvent) {
        Player player = levelUpEvent.getPlayer();
        SkillType skill = levelUpEvent.getSkill();
        int newSkillLevel = levelUpEvent.getSkillLevel();

        onLevelChange(player, skill, newSkillLevel);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelDown(McMMOPlayerLevelDownEvent levelDownEvent) {
        Player player = levelDownEvent.getPlayer();
        SkillType skill = levelDownEvent.getSkill();
        int newSkillLevel = levelDownEvent.getSkillLevel();

        onLevelChange(player, skill, newSkillLevel);
    }

    private void onLevelChange(Player player, SkillType skill, int newSkillLevel) {
        replaceManager.updateScore(player, skill.getName(), newSkillLevel);
        replaceManager.updateScore(player, "powlvl", ExperienceAPI.getPowerLevel(player));
    }
}
