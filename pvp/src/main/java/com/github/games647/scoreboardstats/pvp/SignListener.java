package com.github.games647.scoreboardstats.pvp;

import com.google.common.collect.ImmutableMap;

import de.blablubbabc.insigns.SignSendEvent;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Replace some variables on signs with the player individual stats.
 * The variables will be replaced dynamically
 *
 * @see Database
 */
public class SignListener implements Listener {

    private static final char OPENING_TAG = '[';
    private static final char CLOSING_TAG = ']';

    private final Database database;
    private final Map<String, Function<PlayerStats, Integer>> variables = ImmutableMap.
            <String, Function<PlayerStats, Integer>>builder()
            .put(OPENING_TAG + "Kill" + CLOSING_TAG, PlayerStats::getKills)
            .put(OPENING_TAG + "Death" + CLOSING_TAG, PlayerStats::getDeaths)
            .put(OPENING_TAG + "KDR" + CLOSING_TAG, PlayerStats::getKdr)
            .put(OPENING_TAG + "Streak" + CLOSING_TAG, PlayerStats::getKillstreak)
            .build();

    public SignListener(Plugin plugin, Database statsDatabase) {
        this.database = statsDatabase;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignSend(SignSendEvent event) {
        for (int i = 0; i < 4; ++i) {
            String line = event.getLine(i);
            Player player = event.getPlayer();

            Optional<String> replaced = replace(player, line);
            if (replaced.isPresent()) {
                event.setLine(i, replaced.get());
            }
        }
    }

    private Optional<String> replace(Player player, String line) {
        for (Map.Entry<String, Function<PlayerStats, Integer>> entry : variables.entrySet()) {
            if (line.contains(entry.getKey())) {
                PlayerStats playerCache = database.getCachedStats(player);
                if (playerCache == null) {
                    //The stats aren't loaded yet
                    return Optional.of("Not loaded");
                }

                return Optional.of(Integer.toString(entry.getValue().apply(playerCache)));
            }
        }

        return Optional.empty();
    }
}
