package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.gmail.mrphpfan.mccombatlevel.McCombatLevel;
import com.gmail.mrphpfan.mccombatlevel.PlayerCombatLevelChangeEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McCombatLevelVariables extends DefaultReplaceAdapter<McCombatLevel> implements Listener {

    private final ReplaceManager replaceManager;

    public McCombatLevelVariables(ReplaceManager replaceManager) {
        super((McCombatLevel) Bukkit.getPluginManager().getPlugin("McCombatLevel")
                , "mc_combat_level", "bonus_claim_blocks", "group_bonus_blocks");

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replaceEvent.setScore(getPlugin().getCombatLevel(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(PlayerCombatLevelChangeEvent levelChangeEvent) {
        replaceManager.updateScore(levelChangeEvent.getPlayer(), "mc_combat_level", levelChangeEvent.getNewLevel());
    }
}
