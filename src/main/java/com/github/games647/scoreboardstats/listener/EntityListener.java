package com.github.games647.scoreboardstats.listener;

import static com.github.games647.scoreboardstats.ScoreboardStats.getSettings;
import com.github.games647.scoreboardstats.pvpstats.Cache;
import com.github.games647.scoreboardstats.pvpstats.Database;

public final class EntityListener implements org.bukkit.event.Listener {

    @org.bukkit.event.EventHandler
    public void onMobDeath(final org.bukkit.event.entity.EntityDeathEvent event) {
        final org.bukkit.entity.LivingEntity entity = event.getEntity();

        if (getSettings().checkWorld(entity.getWorld().getName()) || entity.getType().equals(org.bukkit.entity.EntityType.PLAYER)) {
            return;
        }

        final org.bukkit.entity.Player killer = entity.getKiller();

        if (killer == null || !killer.isOnline()) {
            return;
        }

        final Cache killercache = Database.getCache(killer.getName());

        if (killercache != null) {
            killercache.increaseMob();
        }
    }
}
