package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;

/**
 * Represents a replacer for the Plugin PlayerPoints
 *
 * https://dev.bukkit.org/bukkit-plugins/playerpoints/
 */
@DefaultReplacer(plugin = "PlayerPoints")
public class PlayerPointsVariables extends DefaultReplacers<PlayerPoints> {

    public PlayerPointsVariables(ReplacerAPI replaceManager, PlayerPoints plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("points")
                .scoreSupply(player -> plugin.getAPI().look(player.getUniqueId()))
                .eventScore(PlayerPointsResetEvent.class, () -> 0)
                .eventScore(PlayerPointsChangeEvent.class, event ->  {
                    int lastBal = plugin.getAPI().look(event.getPlayerId());
                    return lastBal + event.getChange();
                });
    }
}
