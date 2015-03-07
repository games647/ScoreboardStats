package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.VariableReplacer;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.ClassChangeEvent;
import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;
import com.herocraftonline.heroes.api.events.HeroRegainManaEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Replace all variables that are associated with the heroes plugin
 */
public class HeroesVariables implements VariableReplacer, Listener {

    private final ReplaceManager replaceManager;
    private final CharacterManager characterManager;

    /**
     * Creates a new heroes replacer
     *
     * @param replaceManager
     */
    public HeroesVariables(ReplaceManager replaceManager) {
        this.replaceManager = replaceManager;

        final Heroes heroesPlugin = (Heroes) Bukkit.getPluginManager().getPlugin("Heroes");
        characterManager = heroesPlugin.getCharacterManager();
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        final Hero hero = characterManager.getHero(player);
        if ("mana".equals(variable)) {
            replaceEvent.setScore(hero.getMana());
            replaceEvent.setConstant(true);
        }

        if ("level".equals(variable)) {
            replaceEvent.setScore(hero.getLevel());
            replaceEvent.setConstant(true);
        }

        if ("max_mana".equals(variable)) {
            replaceEvent.setScore(hero.getMaxMana());
            replaceEvent.setConstant(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelChange(HeroChangeLevelEvent levelChangeEvent) {
        final Player player = levelChangeEvent.getHero().getPlayer();
        final HeroClass heroClass = levelChangeEvent.getHeroClass();
        final int newLevel = levelChangeEvent.getTo();

        replaceManager.updateScore(player, "level", newLevel);
        replaceManager.updateScore(player, "max_mana", (int) heroClass.getMaxManaPerLevel() * newLevel);
    }

    @EventHandler(ignoreCancelled = true)
    public void onManaRegain(HeroRegainManaEvent regainManaEvent) {
        final Hero hero = regainManaEvent.getHero();
        replaceManager.updateScore(hero.getPlayer(), "mana", hero.getMana());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSkillUse(SkillUseEvent skillUseEvent) {
        final Hero hero = skillUseEvent.getHero();
        replaceManager.updateScore(hero.getPlayer(), "mana", hero.getMana() - skillUseEvent.getManaCost());
    }

    @EventHandler(ignoreCancelled = true)
    public void onClassChange(ClassChangeEvent classChangeEvent) {
        final Hero hero = classChangeEvent.getHero();
        final int maxMana = (int) classChangeEvent.getTo().getMaxManaPerLevel();
        replaceManager.updateScore(hero.getPlayer(), "max_mana", maxMana);
    }
}
