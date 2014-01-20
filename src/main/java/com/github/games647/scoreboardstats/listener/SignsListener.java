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
                final String line = signSendEvent.getLine(lineNumber);
                getValue(signSendEvent, player, line, lineNumber);
            }
        }
    }

    private void getValue(SignSendEvent signSendEvent, Player player, String line, int lineNumber) {
        final PlayerCache playerCache = Database.getCacheIfAbsent(player.getName());
        if (playerCache == null) {
            return;
        }

        String replacedString = null;
        if (line.contains("[Kill]")) {
            final String kills = String.valueOf(playerCache.getKills());
            replacedString = line.replace("[Kill]", kills);
        } else if (line.contains("[Death]")) {
            final String deaths = String.valueOf(playerCache.getDeaths());
            replacedString = line.replace("[Death]", deaths);
        } else if (line.contains("[KDR]")) {
            final String kdr = String.valueOf(playerCache.getKdr());
            replacedString = line.replace("[KDR]", kdr);
        } else if (line.contains("[Streak]")) {
            final String streak = String.valueOf(playerCache.getStreak());
            replacedString = line.replace("[Streak]", streak);
        }

        if (replacedString != null) {
            signSendEvent.setLine(lineNumber, replacedString);
        }
    }
}
