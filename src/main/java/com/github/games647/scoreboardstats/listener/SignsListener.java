package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;

import de.blablubbabc.insigns.Changer;
import de.blablubbabc.insigns.InSigns;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SignsListener {

    private static final String PERMISSION = "scoreboardstats.sign";

    private SignsListener() {}

    public static void registerSigns(InSigns instance) {

        instance.addChanger(new Changer("[Kill]", PERMISSION) {
            @Override
            public String getValue(Player player, Location lctn) {
                final PlayerCache playercache = Database.getCacheIfAbsent(player.getName());
                return playercache == null
                        ? "" : String.valueOf(playercache.getKills());
            }
        });

        instance.addChanger(new Changer("[Death]", PERMISSION) {
            @Override
            public String getValue(Player player, Location lctn) {
                final PlayerCache playercache = Database.getCacheIfAbsent(player.getName());
                return playercache == null
                        ? "" : String.valueOf(playercache.getDeaths());
            }
        });

        instance.addChanger(new Changer("[Mob]", PERMISSION) {
            @Override
            public String getValue(Player player, Location lctn) {
                final PlayerCache playercache = Database.getCacheIfAbsent(player.getName());
                return playercache == null
                        ? "" : String.valueOf(playercache.getMob());
            }
        });

        instance.addChanger(new Changer("[KDR]", PERMISSION) {
            @Override
            public String getValue(Player player, Location lctn) {
                return Database.getCacheIfAbsent(player.getName()) == null
                        ? "" : String.valueOf(Database.getKdr(player.getName()));
            }
        });

        instance.addChanger(new Changer("[Streak]", PERMISSION) {
            @Override
            public String getValue(Player player, Location lctn) {
                final PlayerCache playercache = Database.getCacheIfAbsent(player.getName());
                return playercache == null
                        ? "" : String.valueOf(playercache.getStreak());
            }
        });
    }
}
