package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;

import de.blablubbabc.insigns.SignSendEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Replace some variables on signs with the player individual stats.
 * The variables will be replaced dynamically
 *
 * @see SignSendEvent
 * @see Database
 */
public class SignListener implements Listener {

    private static final String PERMISSION = "scoreboardstats.sign";

    /**
     * Listen to a sign change event. This will check the permission of the
     * sign changer for creating a sign with a dynamic variable.
     *
     * @param signChangeEvent the change event fired by Bukkit
     */
    //ignore cancelled events
    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent signChangeEvent) {
        final Player player = signChangeEvent.getPlayer();
        if (!player.hasPermission(PERMISSION)) {
            for (String line : signChangeEvent.getLines()) {
                if (line.contains("[Kill]") || line.contains("[Death]")
                        || line.contains("[KDR]") || line.contains("[Streak]")
                        || line.contains("[Mob]")) {
                    signChangeEvent.setCancelled(true);
                    player.sendMessage(Lang.get("noPermissionSign"));
                    break;
                }
            }
        }
    }

    /**
     * Replace the variables before the sign packet is send.
     *
     * @param signSendEvent the sign event
     */
    //ignore cancelled events
    @EventHandler(ignoreCancelled = true)
    public void onSignSendEvent(SignSendEvent signSendEvent) {
        for (int lineNumber = 0; lineNumber < 4; lineNumber++) {
            replaceVariable(signSendEvent, signSendEvent.getPlayer(), lineNumber);
        }
    }

    private void replaceVariable(SignSendEvent signSendEvent, Player player, int lineNumber) {
        final PlayerStats playerCache = Database.getCachedStats(player);
        if (playerCache == null) {
            //The stats aren't loaded yet
            return;
        }

        final String line = signSendEvent.getLine(lineNumber);
        String replacedString = null;
        if (line.contains("[Kill]")) {
            //Convert it to a string
            final String kills = Integer.toString(playerCache.getKills());
            replacedString = line.replace("[Kill]", kills);
        } else if (line.contains("[Death]")) {
            final String deaths = Integer.toString(playerCache.getDeaths());
            replacedString = line.replace("[Death]", deaths);
        } else if (line.contains("[KDR]")) {
            final String kdr = Integer.toString(playerCache.getKdr());
            replacedString = line.replace("[KDR]", kdr);
        } else if (line.contains("[Streak]")) {
            final String streak = Integer.toString(playerCache.getKillstreak());
            replacedString = line.replace("[Streak]", streak);
        } else if (line.contains("[Mob]")) {
            final String mobKills = Integer.toString(playerCache.getMobkills());
            replacedString = line.replace("[Mob]", mobKills);
        }

        //Don't trigger an update if nothing was changed
        if (replacedString != null) {
            //If the variable was found the replace it
            signSendEvent.setLine(lineNumber, replacedString);
        }
    }
}
