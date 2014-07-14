package com.github.games647.scoreboardstats.variables;

import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.TicksPerSecondTask;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

/**
 * Replace all bukkit and general variables
 */
public class GeneralVariables implements ReplaceManager.Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%tps%".equals(variable)) {
            return NumberConversions.round(TicksPerSecondTask.getLastTicks());
        }

        if ("%health%".equals(variable)) {
            return NumberConversions.round(player.getHealth());
        }

        if ("%online%".equals(variable)) {
            return getOnlinePlayers(player);
        }

        if ("%free_ram%".equals(variable)) {
            return (int)( Runtime.getRuntime().freeMemory() / ( 1024 * 1024 ) );
        }

        if ("%max_ram%".equals(variable)) {
            return (int)( Runtime.getRuntime().maxMemory() / ( 1024 * 1024 ) );
        }

        if ("%used_ram%".equals(variable)) {
            return (int)( ( Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory() ) / ( 1024 * 1024 ) );
        }

        if ("%used%ram%".equals(variable)) {
            return ((int)(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) * 100 / (int)Runtime.getRuntime().maxMemory());
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
            return calculateDurabilityRatio(player.getInventory().getHelmet());
        }

        if ("%boots%".equals(variable)) {
            return calculateDurabilityRatio(player.getInventory().getBoots());
        }

        if ("%leggings%".equals(variable)) {
            return calculateDurabilityRatio(player.getInventory().getLeggings());
        }

        if ("%chestplate%".equals(variable)) {
            return calculateDurabilityRatio(player.getInventory().getChestplate());
        }

        if ("%time%".equals(variable)) {
            return (int) player.getWorld().getTime();
        }

        return UNKOWN_VARIABLE;
    }

    private int calculateDurabilityRatio(ItemStack item) {
        //Check if the user have an item on the slot and if the item isn't a stone block or something
        if  (item == null || item.getType().getMaxDurability() == 0) {
            return -2;
        } else {
            return item.getDurability() * 100 / item.getType().getMaxDurability();
        }
    }

    private int getOnlinePlayers(Player player) {
        //If one player is vanish count all visible player
        if (Settings.isHideVanished()) {
            int online = 0;
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (player.canSee(other)) {
                    online++;
                }
            }

            return online;
        } else {
            final Object object = Bukkit.getOnlinePlayers();
            if (object instanceof Object[]) {
                return ((Object[]) object).length;
            } else {
                return ((Collection<?>) object).size();
            }
        }
    }
}
