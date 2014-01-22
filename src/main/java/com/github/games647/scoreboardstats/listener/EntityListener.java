package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();

        if (!Settings.isPvpStats()
                || Settings.isDisabledWorld(entity.getWorld())
                //Check if it's not player because we are already handling it
                || entity.getType() == EntityType.PLAYER) {
            return;
        }

        final PlayerCache killercache = Database.getCacheIfAbsent(killer);
        if (killercache != null) {
            killercache.onMobKill();
        }
    }
}
