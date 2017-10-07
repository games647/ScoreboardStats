package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.event.MyPetLevelUpEvent;
import de.Keyle.MyPet.api.player.MyPetPlayer;

import org.bukkit.plugin.Plugin;

@DefaultReplacer(plugin = "MyPet")
public class MyPetVariables extends DefaultReplacers<Plugin> {

    public MyPetVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("pet_level").scoreSupply(player -> {
            MyPetPlayer myPetPlayer = MyPetApi.getPlayerManager().getMyPetPlayer(player);
            return myPetPlayer.getMyPet().getExperience().getLevel();
        }).eventScore(MyPetLevelUpEvent.class, MyPetLevelUpEvent::getLevel);
    }
}
