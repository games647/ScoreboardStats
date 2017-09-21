package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GriefPreventionVariables extends DefaultReplaceAdapter<GriefPrevention> {

    public GriefPreventionVariables() {
        super(JavaPlugin.getPlugin(GriefPrevention.class)
                , "accrued_claim_block", "bonus_claim_blocks", "group_bonus_blocks"
                , "remaining_blocks", "total_blocks");
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        PlayerData playerData = getPlugin().dataStore.getPlayerData(player.getUniqueId());
        if ("accrued_claim_block".equals(variable)) {
            replaceEvent.setScore(playerData.getAccruedClaimBlocks());
        } else if ("bonus_claim_blocks".equals(variable)) {
            replaceEvent.setScore(playerData.getBonusClaimBlocks());
        } else if ("remaining_blocks".equals(variable)) {
            replaceEvent.setScore(playerData.getRemainingClaimBlocks());
        } else if ("total_blocks".equals(variable)) {
            int groupBonusBlocks = getPlugin().dataStore.getGroupBonusBlocks(player.getUniqueId());
            int totalBlocks = playerData.getAccruedClaimBlocks() + playerData.getBonusClaimBlocks() + groupBonusBlocks;
            replaceEvent.setScore(totalBlocks);
        } else if ("group_bonus_blocks".equals(variable)) {
            replaceEvent.setScore(getPlugin().dataStore.getGroupBonusBlocks(player.getUniqueId()));
        }
    }
}
