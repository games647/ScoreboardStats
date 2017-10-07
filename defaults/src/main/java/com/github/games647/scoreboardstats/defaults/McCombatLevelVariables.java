package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.gmail.mrphpfan.mccombatlevel.McCombatLevel;
import com.gmail.mrphpfan.mccombatlevel.PlayerCombatLevelChangeEvent;

@DefaultReplacer(plugin = "mcCombatLevel")
public class McCombatLevelVariables extends DefaultReplacers<McCombatLevel> {

    public McCombatLevelVariables(ReplacerAPI replaceManager, McCombatLevel plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("mc_combat_level")
                .scoreSupply(plugin::getCombatLevel)
                .eventScore(PlayerCombatLevelChangeEvent.class, PlayerCombatLevelChangeEvent::getNewLevel);
    }
}
