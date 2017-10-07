package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

/**
 * Replace all Bukkit variables
 */
@DefaultReplacer
public class BukkitVariables extends DefaultReplacers<Plugin> {

    private static final int MINUTE_TO_SECOND = 60;

    public BukkitVariables(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("health").scoreSupply(player -> NumberConversions.round(player.getHealth()));
        register("lifetime").scoreSupply(player -> player.getTicksLived() / (20 * MINUTE_TO_SECOND));
        register("no_damage_ticks").scoreSupply(player -> player.getNoDamageTicks() / (20 * MINUTE_TO_SECOND));
        register("last_damage").scoreSupply(player -> (int) (player.getLastDamage()));

        register("exp").scoreSupply(Player::getTotalExperience);
        register("xp_to_level").scoreSupply(Player::getExpToLevel);


        register("helmet").scoreSupply(player -> calculateDurability(player.getInventory().getHelmet()));
        register("boots").scoreSupply(player -> calculateDurability(player.getInventory().getBoots()));
        register("leggings").scoreSupply(player -> calculateDurability(player.getInventory().getLeggings()));
        register("chestplate").scoreSupply(player -> calculateDurability(player.getInventory().getChestplate()));

        register("chestplate").scoreSupply(player -> (int) player.getWorld().getTime());
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
