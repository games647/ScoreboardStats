package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.VariableReplaceAdapter;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

/**
 * Replace all Bukkit variables
 */
public class BukkitVariables extends VariableReplaceAdapter<Plugin> implements Listener {

    private static final int MINUTE_TO_SECOND = 60;

    public BukkitVariables() {
        super(null, "health", "lifetime", "exp", "no_domage_ticks", "xp_to_Level", "last_damage"
                , "helmet", "boots", "leggings", "chestplate", "time"
                , "meta_*");
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("health".equals(variable)) {
            replaceEvent.setScore(NumberConversions.round(player.getHealth()));
            return;
        }

        if ("lifetime".equals(variable)) {
            // --> Minutes
            replaceEvent.setScore(player.getTicksLived() / (20 * MINUTE_TO_SECOND));
            return;
        }

        if ("exp".equals(variable)) {
            replaceEvent.setScore(player.getTotalExperience());
            return;
        }

        if ("no_damage_ticks".equals(variable)) {
            // --> Minutes
            replaceEvent.setScore(player.getNoDamageTicks() / (20 * MINUTE_TO_SECOND));
            return;
        }

        if ("xp_to_level".equals(variable)) {
            replaceEvent.setScore(player.getExpToLevel());
            return;
        }

        if ("last_damage".equals(variable)) {
            replaceEvent.setScore((int) (player.getLastDamage()));
            return;
        }

        if ("helmet".equals(variable)) {
            replaceEvent.setScore(calculateDurability(player.getInventory().getHelmet()));
            return;
        }

        if ("boots".equals(variable)) {
            replaceEvent.setScore(calculateDurability(player.getInventory().getBoots()));
            return;
        }

        if ("leggings".equals(variable)) {
            replaceEvent.setScore(calculateDurability(player.getInventory().getLeggings()));
            return;
        }

        if ("chestplate".equals(variable)) {
            replaceEvent.setScore(calculateDurability(player.getInventory().getChestplate()));
            return;
        }

        if ("time".equals(variable)) {
            replaceEvent.setScore((int) player.getWorld().getTime());
        }

        if (variable.startsWith("meta_")) {
            final String key = variable.replace("meta_", "");
            //assumimg this key is unique
            if (player.hasMetadata(key)) {
                replaceEvent.setScore(player.getMetadata(key).get(0).asInt());
            } else {
                replaceEvent.setScore(-1);
            }
        }
    }

    private int calculateDurability(ItemStack item) {
        //Check if the user have an item on the slot and if the item isn't a stone block or something
        if (item == null || item.getType().getMaxDurability() == 0) {
            return 0;
        } else {
            //calculate in percent
            return item.getDurability() * 100 / item.getType().getMaxDurability();
        }
    }
}
