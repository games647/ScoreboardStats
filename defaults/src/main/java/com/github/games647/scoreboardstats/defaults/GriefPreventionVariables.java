package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

import org.bukkit.entity.Player;

@DefaultReplacer(plugin = "GriefPrevention")
public class GriefPreventionVariables extends DefaultReplacers<GriefPrevention> {

    public GriefPreventionVariables(ReplacerAPI replaceManager, GriefPrevention plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("accrued_claim_block")
                .scoreSupply(player -> getData(player).getAccruedClaimBlocks());

        register("bonus_claim_blocks")
                .scoreSupply(player -> getData(player).getBonusClaimBlocks());

        register("remaining_blocks")
                .scoreSupply(player -> getData(player).getRemainingClaimBlocks());

        register("total_blocks")
                .scoreSupply(player -> {
                    PlayerData playerData = getData(player);
                    return plugin.dataStore.getGroupBonusBlocks(player.getUniqueId())
                            + playerData.getAccruedClaimBlocks()
                            + playerData.getRemainingClaimBlocks()
                            + playerData.getBonusClaimBlocks();
                });

        register("group_bonus_blocks")
                .scoreSupply(player -> plugin.dataStore.getGroupBonusBlocks(player.getUniqueId()));
    }

    private PlayerData getData(Player player) {
        return plugin.dataStore.getPlayerData(player.getUniqueId());
    }
}
