package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
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
 *
 * http://dev.bukkit.org/bukkit-plugins/heroes/
 */
public class HeroesVariables extends DefaultReplaceAdapter<Heroes> implements Listener {

    private final ReplaceManager replaceManager;
    private final CharacterManager characterManager;

    /**
     * Creates a new heroes replacer
     *
     * @param replaceManager the replace manager from ScoreboardStats
     */
    public HeroesVariables(ReplaceManager replaceManager) {
        super((Heroes) Bukkit.getPluginManager().getPlugin("Heroes"), "mana", "level", "max_mana");

        this.replaceManager = replaceManager;
        characterManager = getPlugin().getCharacterManager();
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        Hero hero = characterManager.getHero(player);
        replaceEvent.setConstant(true);
        if ("mana".equals(variable)) {
            replaceEvent.setScore(hero.getMana());
        } else if ("level".equals(variable)) {
            replaceEvent.setScore(hero.getLevel());
        } else if ("max_mana".equals(variable)) {
            replaceEvent.setScore(hero.getMaxMana());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelChange(HeroChangeLevelEvent levelChangeEvent) {
        Player player = levelChangeEvent.getHero().getPlayer();
        HeroClass heroClass = levelChangeEvent.getHeroClass();
        int newLevel = levelChangeEvent.getTo();

        replaceManager.updateScore(player, "level", newLevel);
        replaceManager.updateScore(player, "max_mana", (int) heroClass.getMaxManaPerLevel() * newLevel);
    }

    @EventHandler(ignoreCancelled = true)
    public void onManaRegain(HeroRegainManaEvent regainManaEvent) {
        Hero hero = regainManaEvent.getHero();
        replaceManager.updateScore(hero.getPlayer(), "mana", hero.getMana());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSkillUse(SkillUseEvent skillUseEvent) {
        Hero hero = skillUseEvent.getHero();
        replaceManager.updateScore(hero.getPlayer(), "mana", hero.getMana() - skillUseEvent.getManaCost());
    }

    @EventHandler(ignoreCancelled = true)
    public void onClassChange(ClassChangeEvent classChangeEvent) {
        Hero hero = classChangeEvent.getHero();
        int maxMana = (int) classChangeEvent.getTo().getMaxManaPerLevel();
        replaceManager.updateScore(hero.getPlayer(), "max_mana", maxMana);
    }
}
