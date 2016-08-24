package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.event.MyPetLevelUpEvent;
import de.Keyle.MyPet.api.player.MyPetPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class MyPetVariables extends DefaultReplaceAdapter<Plugin> implements Listener {

    private final ReplaceManager replaceManager;

    public MyPetVariables(ReplaceManager replaceManager) {
        super(Bukkit.getPluginManager().getPlugin("MyPet"), "pet_level");

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        MyPetPlayer myPetPlayer = MyPetApi.getPlayerManager().getMyPetPlayer(player);
        int level = myPetPlayer.getMyPet().getExperience().getLevel();
        replaceEvent.setScoreOrText(level);
        replaceEvent.setConstant(true);
    }

    @EventHandler
    public void onLevelUp(MyPetLevelUpEvent levelUpEvent) {
        replaceManager.updateScore(levelUpEvent.getOwner().getPlayer(), "pet_level", levelUpEvent.getLevel());
    }
}
