package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;

import de.blablubbabc.insigns.SignSendEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class SignsListener implements Listener {

    private static final String PERMISSION = "scoreboardstats.sign";

    @EventHandler
    public void onSignSendEvent(SignSendEvent signSendEvent) {
        final Player player = signSendEvent.getPlayer();
        if (player.hasPermission(PERMISSION)) {
            for (int lineNumber = 0; lineNumber < 4; lineNumber++) {
                replaceVariable(signSendEvent, player, lineNumber);
            }
        }
    }

    private void replaceVariable(SignSendEvent signSendEvent, Player player, int lineNumber) {
        final PlayerCache playerCache = Database.getCacheIfAbsent(player);
        if (playerCache == null) {
            //The stats aren't loaded yet
            return;
        }

        final String line = signSendEvent.getLine(lineNumber);
        String replacedString = null;
        if (line.contains("[Kill]")) {
            //Convert it to a string
            final String kills = String.valueOf(playerCache.getKills());
            replacedString = line.replace("[Kill]", kills);
        } else if (line.contains("[Death]")) {
            final String deaths = String.valueOf(playerCache.getDeaths());
            replacedString = line.replace("[Death]", deaths);
        } else if (line.contains("[KDR]")) {
            final String kdr = String.valueOf(playerCache.getKdr());
            replacedString = line.replace("[KDR]", kdr);
        } else if (line.contains("[Streak]")) {
            final String streak = String.valueOf(playerCache.getHighestStreak());
            replacedString = line.replace("[Streak]", streak);
        }

        if (replacedString != null) {
            //If the variable was found the replace it
            signSendEvent.setLine(lineNumber, replacedString);
        }
    }
}
