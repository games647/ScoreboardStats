package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GriefPreventionVariables extends DefaultReplaceAdapter<GriefPrevention> {

    public GriefPreventionVariables() {
        super((GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention")
                , "accrued_claim_block", "bonus_claim_blocks", "group_bonus_blocks", "remaining_blocks", "total_blocks");
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
            int totalBlocks = playerData.getAccruedClaimBlocks() + playerData.getBonusClaimBlocks() + getPlugin().dataStore.getGroupBonusBlocks(player.getUniqueId());
            replaceEvent.setScore(totalBlocks);
        } else if ("group_bonus_blocks".equals(variable)) {
            replaceEvent.setScore(getPlugin().dataStore.getGroupBonusBlocks(player.getUniqueId()));
        }
    }
}
