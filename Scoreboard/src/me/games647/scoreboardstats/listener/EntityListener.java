package me.games647.scoreboardstats.listener;

import static me.games647.scoreboardstats.ScoreboardStats.getSettings;

public final class EntityListener implements org.bukkit.event.Listener {

    @org.bukkit.event.EventHandler
    public void onMobDeath(final org.bukkit.event.entity.EntityDeathEvent event) {

        if ((!getSettings().isPvpstats()) || (getSettings().checkWorld(event.getEntity().getWorld().getName()))) {
            return;
        }

        final org.bukkit.entity.LivingEntity entity = event.getEntity();

        if (!(entity.getType().equals(org.bukkit.entity.EntityType.PLAYER))) {
            final org.bukkit.entity.Player killer = entity.getKiller();

            if (killer != null) {
                me.games647.scoreboardstats.api.Database.increaseMobKills(killer.getName());
            }

        }
    }
}
