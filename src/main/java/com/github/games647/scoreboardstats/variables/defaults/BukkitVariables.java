package com.github.games647.scoreboardstats.variables.defaults;

import com.github.games647.scoreboardstats.TicksPerSecondTask;
import com.github.games647.scoreboardstats.variables.ReplaceEvent;
import com.github.games647.scoreboardstats.variables.ReplaceManager;
import com.github.games647.scoreboardstats.variables.VariableReplacer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

/**
 * Replace all Bukkit and general variables
 */
public class BukkitVariables implements VariableReplacer, Listener {

    private final ReplaceManager replaceManager;

    public BukkitVariables(ReplaceManager replaceManager) {
        this.replaceManager = replaceManager;
    }

    @Override
    public void onReplace(Player player, String variable, ReplaceEvent replaceEvent) {
        if ("tps".equals(variable)) {
            replaceEvent.setScore(NumberConversions.round(TicksPerSecondTask.getLastTicks()));
        }

        if ("health".equals(variable)) {
            replaceEvent.setScore(NumberConversions.round(player.getHealth()));
        }

        if ("online".equals(variable)) {
            replaceEvent.setScore(Bukkit.getOnlinePlayers().length);
            replaceEvent.setConstant(true);
        }

        if ("lifetime".equals(variable)) {
            // --> Minutes
            replaceEvent.setScore(player.getTicksLived() / (20 * 60));
        }

        if ("exp".equals(variable)) {
            replaceEvent.setScore(player.getTotalExperience());
        }

        if ("no_damage_ticks".equals(variable)) {
            // --> Minutes
            replaceEvent.setScore(player.getNoDamageTicks() / (20 * 60));
        }

        if ("xp_to_level".equals(variable)) {
            replaceEvent.setScore(player.getExpToLevel());
        }

        if ("last_damage".equals(variable)) {
            //casting should be made after division
            // --> Minutes
            replaceEvent.setScore((int) (player.getLastDamage() / (20 * 60)));
        }

        if ("max_player".equals(variable)) {
            replaceEvent.setScore(Bukkit.getMaxPlayers());
            replaceEvent.setConstant(true);
        }

        if ("helmet".equals(variable)) {
            replaceEvent.setScore(calculateDurability(player.getInventory().getHelmet()));
        }

        if ("boots".equals(variable)) {
            replaceEvent.setScore(calculateDurability(player.getInventory().getBoots()));
        }

        if ("leggings".equals(variable)) {
            replaceEvent.setScore(calculateDurability(player.getInventory().getLeggings()));
        }

        if ("chestplate".equals(variable)) {
            replaceEvent.setScore(calculateDurability(player.getInventory().getChestplate()));
        }

        if ("time".equals(variable)) {
            replaceEvent.setScore((int) player.getWorld().getTime());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent joinEvent) {
        replaceManager.updateScore("online", Bukkit.getOnlinePlayers().length + 1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        replaceManager.updateScore("online", Bukkit.getOnlinePlayers().length - 1);
    }

    private int calculateDurability(ItemStack item) {
        //Check if the user have an item on the slot and if the item isn't a stone block or something
        if (item == null || item.getType().getMaxDurability() == 0) {
            return -2;
        } else {
            return item.getDurability() * 100 / item.getType().getMaxDurability();
        }
    }
}
