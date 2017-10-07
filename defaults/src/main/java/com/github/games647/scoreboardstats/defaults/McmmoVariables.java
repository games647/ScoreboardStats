package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

import java.util.stream.Stream;

import org.bukkit.plugin.Plugin;

/**
 * Replace all variables that are associated with the mcMMO plugin
 * <p>
 * https://dev.bukkit.org/bukkit-plugins/mcmmo/
 */
@DefaultReplacer(plugin = "mcMMO")
public class McmmoVariables extends DefaultReplacers<Plugin> {

    public McmmoVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("powlvl").scoreSupply(ExperienceAPI::getPowerLevel)
                .eventScore(McMMOPlayerLevelUpEvent.class, this::getPowerLevel)
                .eventScore(McMMOPlayerLevelDownEvent.class, this::getPowerLevel);

        Stream.of(SkillType.values())
                .map(SkillType::name)
                .map(String::toUpperCase)
                .forEach(skill -> register(skill)
                        .scoreSupply(player -> ExperienceAPI.getLevel(player, skill))
                        .eventScore(McMMOPlayerLevelUpEvent.class, event -> ExperienceAPI
                                .getLevel(event.getPlayer(), skill))
                        .eventScore(McMMOPlayerLevelDownEvent.class, event -> ExperienceAPI
                                .getLevel(event.getPlayer(), skill)));
    }

    private int getPowerLevel(McMMOPlayerLevelChangeEvent changeEvent) {
        return ExperienceAPI.getPowerLevel(changeEvent.getPlayer());
    }
}
