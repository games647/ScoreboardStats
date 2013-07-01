package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;
import com.github.games647.variables.Permissions;
import com.github.games647.variables.VariableList;

import de.blablubbabc.insigns.Changer;
import de.blablubbabc.insigns.InSigns;

import org.bukkit.Location;
import org.bukkit.entity.Player;

final class SignsListener {

    public static void registerSigns(final InSigns instance) {

        instance.addChanger(new Changer(VariableList.SIGN_KILL, Permissions.SIGN_PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final PlayerCache playercache = Database.getCache(player.getName());

                return playercache == null
                        ? "" : String.valueOf(playercache.getKills());
            }
        });

        instance.addChanger(new Changer(VariableList.SIGN_DEATH, Permissions.SIGN_PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final PlayerCache playercache = Database.getCache(player.getName());

                return playercache == null
                        ? "" : String.valueOf(playercache.getDeaths());
            }
        });

        instance.addChanger(new Changer(VariableList.SIGN_MOB, Permissions.SIGN_PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final PlayerCache playercache = Database.getCache(player.getName());

                return playercache == null
                        ? "" : String.valueOf(playercache.getMob());
            }
        });

        instance.addChanger(new Changer(VariableList.SIGN_KDR, Permissions.SIGN_PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                return Database.getCache(player.getName()) == null
                        ? "" : String.valueOf(Database.getKdr(player.getName()));
            }
        });

        instance.addChanger(new Changer(VariableList.SIGN_STREAK, Permissions.SIGN_PERMISSION) {
            @Override
            public String getValue(final Player player, final Location lctn) {
                final PlayerCache playercache = Database.getCache(player.getName());

                return playercache == null
                        ? "" : String.valueOf(playercache.getStreak());
            }
        });
    }
}
