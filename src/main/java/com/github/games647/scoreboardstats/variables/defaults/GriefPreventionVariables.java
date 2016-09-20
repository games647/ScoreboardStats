package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GriefPreventionVariables extends DefaultReplaceAdapter<GriefPrevention> {

    /**
     * Creates a new vault replacer
     */
    public GriefPreventionVariables() {
        super((GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention")
                , "accrued_claim_block", "bonus_claim_blocks", "group_bonus_blocks");


    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        PlayerData playerData = getPlugin().dataStore.getPlayerData(player.getUniqueId());
        if ("accrued_claim_block".equals(variable)) {
            replaceEvent.setScore(playerData.getAccruedClaimBlocks());
        } else if ("bonus_claim_blocks".equals(variable)) {
            replaceEvent.setScore(playerData.getBonusClaimBlocks());
        } else if ("group_bonus_blocks".equals(variable)) {
            replaceEvent.setScore(-1);
//            replaceEvent.setScore(getPlugin().dataStore.getGroupBonusBlocks(player.getUniqueId()));
        }
    }
}
