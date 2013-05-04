package com.github.games647.scoreboardstats.listener;

import static com.github.games647.scoreboardstats.ScoreboardStats.getSettings;
import com.github.games647.scoreboardstats.pvpstats.Database;

public final class EntityListener implements org.bukkit.event.Listener {

    @org.bukkit.event.EventHandler
    public void onMobDeath(final org.bukkit.event.entity.EntityDeathEvent event) {
        if ((!getSettings().isPvpstats()) || (getSettings().checkWorld(event.getEntity().getWorld().getName()))) {
            return;
        }

        final org.bukkit.entity.LivingEntity entity = event.getEntity();

        if (!(entity.getType().equals(org.bukkit.entity.EntityType.PLAYER))) {
            final org.bukkit.entity.Player killer = entity.getKiller();

            if ((killer != null) && (killer.isOnline() && (Database.getCache(killer.getName()) != null))) {
                Database.getCache(killer.getName()).increaseMob();
            }
        }
    }
}
