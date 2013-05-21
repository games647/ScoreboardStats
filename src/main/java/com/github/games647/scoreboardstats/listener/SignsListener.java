package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;
import de.blablubbabc.insigns.Changer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SignsListener {

    public static void registerSigns(final de.blablubbabc.insigns.InSigns instance) {

        final String PERMISSION = "scoreboardstats.sign";

        instance.addChanger(new Changer("[Kill]", PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final PlayerCache playercache = Database.getCache(player.getName());

                return playercache == null ? "" : String.valueOf(playercache.getKills());
            }
        });

        instance.addChanger(new Changer("[Death]", PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final PlayerCache playercache = Database.getCache(player.getName());

                return playercache == null ? "" : String.valueOf(playercache.getDeaths());
            }
        });

        instance.addChanger(new Changer("[Mob]", PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final PlayerCache playercache = Database.getCache(player.getName());

                return playercache == null ? "" : String.valueOf(playercache.getMob());
            }
        });

        instance.addChanger(new Changer("[KDR]", PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                return Database.getCache(player.getName()) == null ? "" : String.valueOf(Database.getKdr(player.getName()));
            }
        });

        instance.addChanger(new Changer("[Killstreak]", PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final PlayerCache playercache = Database.getCache(player.getName());

                return playercache == null ? "" : String.valueOf(playercache.getStreak());
            }
        });
    }
}
