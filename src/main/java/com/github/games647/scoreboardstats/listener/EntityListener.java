package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerStats;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * If enabled this class counts the mob kills.
 */
public class EntityListener implements Listener {

    /**
     * Tracks the mob kills.
     *
     * @param event the death event
     */
    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();

        if (!Settings.isPvpStats()
                || !Settings.isActiveWorld(entity.getWorld())
                //Check if it's not player because we are already handling it
                || EntityType.PLAYER == entity.getType()) {
            return;
        }

        final PlayerStats killercache = Database.getCacheIfAbsent(killer);
        if (killercache != null) {
            //If the cache entry is loaded and the player isn't null, increase the mob kills
            killercache.incrementMobKills();
        }
    }
}
