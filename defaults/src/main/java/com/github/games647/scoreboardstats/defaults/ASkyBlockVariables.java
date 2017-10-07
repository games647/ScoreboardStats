package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.ChallengeCompleteEvent;
import com.wasteofplastic.askyblock.events.IslandPostLevelEvent;

import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@DefaultReplacer(plugin = "ASkyBlock")
public class ASkyBlockVariables extends DefaultReplacers<Plugin> {

    private final ASkyBlockAPI skyBlockAPI = ASkyBlockAPI.getInstance();

    public ASkyBlockVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("island-level")
                .scoreSupply(player -> (int) skyBlockAPI.getLongIslandLevel(player.getUniqueId()))
                .eventScore(IslandPostLevelEvent.class, event -> (int) event.getLongLevel());

        register("challenge_done")
                .scoreSupply(player -> (int) skyBlockAPI.getLongIslandLevel(player.getUniqueId()))
                .eventScore(ChallengeCompleteEvent.class, event -> getDoneChallenge(event.getPlayer()));

        register("challenge_incomplete")
                .scoreSupply(player -> (int) skyBlockAPI.getLongIslandLevel(player.getUniqueId()))
                .eventScore(ChallengeCompleteEvent.class, event -> getIncompleteChallenge(event.getPlayer()));

        register("challenge_total")
                .scoreSupply(player -> (int) skyBlockAPI.getLongIslandLevel(player.getUniqueId()));

        register("challenge_unique")
                .scoreSupply(player -> (int) skyBlockAPI.getLongIslandLevel(player.getUniqueId()));
    }

    private int getDoneChallenge(Player player) {
        return mapChallenges(player, challenges -> (int) challenges
                .filter(complete -> complete)
                .count());
    }

    private int getIncompleteChallenge(Player player) {
        return mapChallenges(player, challenges -> (int) challenges
                .filter(complete -> !complete)
                .count());
    }

    private int getTotalChallenge(Player player) {
        return mapTimes(player, lst -> (int) lst
                .filter(times -> times > 0)
                .count());
    }

    private int getUniqueChallenge(Player player) {
        return mapTimes(player, lst -> lst
                .mapToInt(time -> time)
                .sum());
    }

    private int mapTimes(Player player, ToIntFunction<Stream<Integer>> fct) {
        return fct.applyAsInt(skyBlockAPI.getChallengeTimes(player.getUniqueId()).values().stream());
    }

    private int mapChallenges(Player player, ToIntFunction<Stream<Boolean>> fct) {
        return fct.applyAsInt(skyBlockAPI.getChallengeStatus(player.getUniqueId()).values().stream());
    }
}
