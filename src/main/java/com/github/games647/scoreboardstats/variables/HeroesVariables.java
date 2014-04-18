package com.github.games647.scoreboardstats.variables;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Replace all variables that are associated with the heroes plugin
 */
public class HeroesVariables implements ReplaceManager.Replaceable {

    private final CharacterManager characterManager;

    /**
     * Creates a new heroes replacer
     */
    public HeroesVariables() {
        final Heroes heroesPlugin = (Heroes) Bukkit.getPluginManager().getPlugin("Heroes");
        characterManager = heroesPlugin.getCharacterManager();
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        final Hero hero = characterManager.getHero(player);
        if ("%mana%".equals(variable)) {
            return hero.getMana();
        }

        if ("%level%".equals(variable)) {
            return hero.getLevel();
        }

        if ("%max_mana%".equals(variable)) {
            return hero.getMaxMana();
        }

        if ("%mana_regen%".equals(variable)) {
            return hero.getManaRegen();
        }

        return UNKOWN_VARIABLE;
    }
}
