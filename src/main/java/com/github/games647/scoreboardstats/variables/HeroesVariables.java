package com.github.games647.scoreboardstats.variables;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class HeroesVariables implements ReplaceManager.Replaceable {

    private CharacterManager characterManager;

    public HeroesVariables() {
        initialize();
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

    private void initialize() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        final Heroes heroesPlugin = (Heroes) pluginManager.getPlugin("Heroes");
        characterManager = heroesPlugin.getCharacterManager();
    }
}
