package com.github.games647.scoreboardstats.listener;

import com.github.games647.scoreboardstats.Settings;


public final class EntityListener implements org.bukkit.event.Listener {

    @org.bukkit.event.EventHandler
    public static void onMobDeath(final org.bukkit.event.entity.EntityDeathEvent event) {
        final org.bukkit.entity.LivingEntity entity = event.getEntity();
        final org.bukkit.entity.Player killer = entity.getKiller();

        if (!Settings.isPvpStats()
                || Settings.isDisabledWorld(entity.getWorld().getName())
                || entity.getType() == org.bukkit.entity.EntityType.PLAYER
                || killer == null
                || !killer.isOnline()) {
            return;
        }

        final com.github.games647.scoreboardstats.pvpstats.PlayerCache killercache = com.github.games647.scoreboardstats.pvpstats.Database.getCache(killer.getName());

        if (killercache != null) {
            killercache.increaseMob();
        }
    }
}
