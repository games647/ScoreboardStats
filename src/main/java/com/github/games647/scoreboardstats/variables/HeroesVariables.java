package com.github.games647.scoreboardstats.variables;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;

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
        if ("%mana%".equals(variable)) {
            return characterManager.getHero(player).getMana();
        }

        if ("%level%".equals(variable)) {
            return characterManager.getHero(player).getLevel();
        }

        if ("%max_mana%".equals(variable)) {
            return characterManager.getHero(player).getMaxMana();
        }

        if ("%mana_regen%".equals(variable)) {
            return characterManager.getHero(player).getManaRegen();
        }

        return UNKOWN_VARIABLE;
    }

    private void initialize() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        final Heroes heroesPlugin = (Heroes) pluginManager.getPlugin("Heroes");
        characterManager = heroesPlugin.getCharacterManager();
    }
}
