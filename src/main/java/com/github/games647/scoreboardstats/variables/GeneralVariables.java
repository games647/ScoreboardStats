package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Settings;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GeneralVariables implements ReplaceManager.Replaceable {

    private static int getOnlinePlayers(Player player) {
        //If one player is vanish count all visible player
        if (Settings.isHideVanished()) {
            int online = 0;
            for (Player other: Bukkit.getOnlinePlayers()) {
                if (player.canSee(other)) {
                    online++;
                }
            }

            return online;
        } else {
            return Bukkit.getOnlinePlayers().length;
        }
    }

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%health%".equals(variable)) {
            return (int) Math.round(player.getHealth());
        }

        if ("%online%".equals(variable)) {
            return getOnlinePlayers(player);
        }

        if ("%free_ram%".equals(variable)) {
            return (int) (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        }

        if ("%max_ram%".equals(variable)) {
            return (int) Runtime.getRuntime().maxMemory() / 1024 / 1024;
        }

        if ("%used_ram%".equals(variable)) {
            return (int) (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        }

        if ("%used%ram%".equals(variable)) {
            return (int) (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory() * 100 / Runtime.getRuntime().maxMemory());
        }

        if ("%date%".equals(variable)) {
            //Get the current date
            return new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
        }

        if ("%lifetime%".equals(variable)) {
            // --> Minutes
            return player.getTicksLived() / 20 / 60;
        }

        if ("%exp%".equals(variable)) {
            return player.getTotalExperience();
        }

        if ("%no_damage_ticks%".equals(variable)) {
            // --> Minutes
            return player.getNoDamageTicks() / 20 / 60;
        }

        if ("%xp_to_level%".equals(variable)) {
            return player.getExpToLevel();
        }

        if ("%last_damage%".equals(variable)) {
            // --> Minutes
            return (int) (player.getLastDamage() / 20 / 60);
        }

        if ("%max_player%".equals(variable)) {
            return Bukkit.getMaxPlayers();
        }

        if ("%helmet%".equals(variable)) {
            final ItemStack helmet = player.getInventory().getHelmet();
            if (helmet == null || helmet.getType().getMaxDurability() == 0) {
                return -2;
            } else {
                return (helmet.getDurability() * 100) / helmet.getType().getMaxDurability();
            }
        }

        if ("%boots%".equals(variable)) {
            final ItemStack boots = player.getInventory().getBoots();
            if (boots == null || boots.getType().getMaxDurability() == 0) {
                return -2;
            } else {
                return (boots.getDurability() * 100) / boots.getType().getMaxDurability();
            }
        }

        if ("%leggings%".equals(variable)) {
            final ItemStack leggings = player.getInventory().getLeggings();
            if (leggings == null || leggings.getType().getMaxDurability() == 0) {
                return -2;
            } else {
                return (leggings.getDurability() * 100) / leggings.getType().getMaxDurability();
            }
        }

        if ("%chestplate%".equals(variable)) {
            final ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate == null || chestplate.getType().getMaxDurability() == 0) {
                return -2;
            } else {
                return (chestplate.getDurability() * 100) / chestplate.getType().getMaxDurability();
            }
        }

        return UNKOWN_VARIABLE;
    }
}
