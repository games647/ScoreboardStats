package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.IslandLevelEvent;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

public class ASkyBlockVariables extends DefaultReplaceAdapter<Plugin> implements Listener {

    private final ASkyBlockAPI skyBlockAPI = ASkyBlockAPI.getInstance();
    private final ReplaceManager replaceManager;

    public ASkyBlockVariables(ReplaceManager replaceManager) {
        super(Bukkit.getPluginManager().getPlugin("ASkyBlock"), "island_level");

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replaceEvent.setScore(NumberConversions.round(skyBlockAPI.getIslandLevel(player.getUniqueId())));
        replaceEvent.setConstant(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(IslandLevelEvent levelEvent) {
        UUID player = levelEvent.getPlayer();
        Player receiver = Bukkit.getPlayer(player);
        if (receiver == null) {
            return;
        }

        replaceManager.updateScore(receiver, "island_level", levelEvent.getLevel());
    }
}
