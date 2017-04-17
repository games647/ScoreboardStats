package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.ChallengeCompleteEvent;
import com.wasteofplastic.askyblock.events.IslandPostLevelEvent;

import java.util.Map;
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
        super(Bukkit.getPluginManager().getPlugin("ASkyBlock")
                , "island-level", "challenge_done", "challenge_incomplete", "challenge_total", "challenge_unique");

        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        replaceEvent.setConstant(true);

        if ("island-level".equals(variable)) {
            replaceEvent.setScore(NumberConversions.round(skyBlockAPI.getIslandLevel(player.getUniqueId())));
        } else if ("challenge_done".equals(variable)) {
            Map<String, Boolean> challengeStatus = skyBlockAPI.getChallengeStatus(player.getUniqueId());
            int completeChallenge = NumberConversions.toInt(challengeStatus.values().stream()
                    .filter(complete -> complete)
                    .count());
            replaceEvent.setScore(completeChallenge);
        } else if ("challenge_incomplete".equals(variable)) {
            Map<String, Boolean> challengeStatus = skyBlockAPI.getChallengeStatus(player.getUniqueId());
            int incomplete = NumberConversions.toInt(challengeStatus.values().stream()
                    .filter(complete -> !complete)
                    .count());
            replaceEvent.setScore(incomplete);
        } else if ("challenge_unique".equals(variable)) {
            Map<String, Integer> challengeTimes = skyBlockAPI.getChallengeTimes(player.getUniqueId());
            int unique = NumberConversions.toInt(challengeTimes.values().stream()
                    .filter(times -> times > 0)
                    .count());
            replaceEvent.setScore(unique);
        } else {
            Map<String, Integer> challengeTimes = skyBlockAPI.getChallengeTimes(player.getUniqueId());
            int allChallenges = NumberConversions.toInt(challengeTimes.values().stream()
                    .mapToInt(times -> times)
                    .sum());
            replaceEvent.setScore(allChallenges);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(IslandPostLevelEvent levelEvent) {
        UUID player = levelEvent.getPlayer();
        Player receiver = Bukkit.getPlayer(player);
        if (receiver == null) {
            return;
        }

        replaceManager.updateScore(receiver, "island-level", levelEvent.getLevel());
    }

    @EventHandler(ignoreCancelled = true)
    public void onChallengeComplete(ChallengeCompleteEvent completeEvent) {
        Player player = completeEvent.getPlayer();

        Map<String, Boolean> challengeStatus = skyBlockAPI.getChallengeStatus(player.getUniqueId());
        int incomplete = NumberConversions.toInt(challengeStatus.values().stream()
                .filter(complete -> !complete)
                .count());
        int completeChallenge = NumberConversions.toInt(challengeStatus.values().stream()
                .filter(complete -> complete)
                .count());

        replaceManager.updateScore(player, "challenge_incomplete", incomplete);
        replaceManager.updateScore(player, "challenge_done", completeChallenge);
    }
}
