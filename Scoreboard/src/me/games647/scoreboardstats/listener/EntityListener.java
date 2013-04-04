package me.games647.scoreboardstats.listener;

public final class EntityListener implements org.bukkit.event.Listener {

    @org.bukkit.event.EventHandler
    public void onMobDeath(final org.bukkit.event.entity.EntityDeathEvent event) {
        final org.bukkit.entity.LivingEntity entity = event.getEntity();
        if (!(entity.getType().equals(org.bukkit.entity.EntityType.PLAYER))) {
            final org.bukkit.entity.Player killer = entity.getKiller();
            if (killer != null) {
                me.games647.scoreboardstats.api.Score.update(
                    ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) killer).getHandle().playerConnection
                    , me.games647.scoreboardstats.ScoreboardStats.getSettings().getMob()
                    , me.games647.scoreboardstats.api.Database.increaseMobKills(killer.getName()));
            }
        }
    }
}
