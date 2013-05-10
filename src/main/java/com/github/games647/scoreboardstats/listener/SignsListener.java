package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.pvpstats.Cache;
import com.github.games647.scoreboardstats.pvpstats.Database;
import de.blablubbabc.insigns.Changer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SignsListener {

    private SignsListener() {
    }

    public static void registerSigns(final de.blablubbabc.insigns.InSigns instance) {

        final String permission = "scoreboardstats.sign";

        instance.addChanger(new Changer("[Kill]", permission) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final Cache playercache = Database.getCache(player.getName());

                return playercache == null ? "" : String.valueOf(playercache.getKills());
            }
        });

        instance.addChanger(new Changer("[Death]", permission) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final Cache playercache = Database.getCache(player.getName());

                return playercache == null ? "" : String.valueOf(playercache.getDeaths());
            }
        });

        instance.addChanger(new Changer("[Mob]", permission) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final Cache playercache = Database.getCache(player.getName());

                return playercache == null ? "" : String.valueOf(playercache.getMob());
            }
        });

        instance.addChanger(new Changer("[KDR]", permission) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                return Database.getCache(player.getName()) == null ? "" : String.valueOf(Database.getKdr(player.getName()));
            }
        });

        instance.addChanger(new Changer("[Killstreak]", permission) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final Cache playercache = Database.getCache(player.getName());

                return playercache == null ? "" : String.valueOf(playercache.getStreak());
            }
        });
    }
}
