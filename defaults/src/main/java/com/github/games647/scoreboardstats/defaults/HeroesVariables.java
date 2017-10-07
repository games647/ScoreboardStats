package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.ClassChangeEvent;
import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;
import com.herocraftonline.heroes.api.events.HeroRegainManaEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;

import org.bukkit.entity.Player;

/**
 * Replace all variables that are associated with the heroes plugin
 * <p>
 * https://dev.bukkit.org/bukkit-plugins/heroes/
 */
@DefaultReplacer(plugin = "Heroes")
public class HeroesVariables extends DefaultReplacers<Heroes> {

    private final CharacterManager characterManager;

    public HeroesVariables(ReplacerAPI replaceManager, Heroes plugin) {
        super(replaceManager, plugin);

        this.characterManager = plugin.getCharacterManager();
    }

    @Override
    public void register() {
        register("mana").scoreSupply(player -> getHero(player).getMana())
                .eventScore(SkillUseEvent.class, event -> event.getHero().getMana() - event.getManaCost())
                .eventScore(HeroRegainManaEvent.class, event -> event.getHero().getMana());

        register("max_mana").scoreSupply(player -> getHero(player).getMaxMana())
                .eventScore(ClassChangeEvent.class, event -> event.getHero().getMaxMana());

        register("level").scoreSupply(player -> getHero(player).getLevel())
                .eventScore(HeroChangeLevelEvent.class, HeroChangeLevelEvent::getTo);
    }

    private Hero getHero(Player player) {
        return characterManager.getHero(player);
    }
}
