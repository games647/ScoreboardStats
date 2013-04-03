package me.games647.scoreboardstats.listener;

import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import me.games647.scoreboardstats.api.Database;
import me.games647.scoreboardstats.api.Score;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class DeathListener implements org.bukkit.event.Listener {

    @EventHandler
    public void onDeath(final org.bukkit.event.entity.PlayerDeathEvent death) {

        final Player killed = death.getEntity();
        final Player killer = killed.getKiller();

        Score.update(
                ((CraftPlayer) killed).getHandle().playerConnection
                , getSettings().getDeaths()
                , Database.increaseDeaths(killed.getName()));

        if (killer != null) {

            Score.update(
                    ((CraftPlayer) killer).getHandle().playerConnection
                    , getSettings().getKills()
                    , Database.increaseKills(killer.getName()));

        }
    }

    @EventHandler
    public void onMobDeath(final org.bukkit.event.entity.EntityDeathEvent event) {
        final org.bukkit.entity.LivingEntity entity = event.getEntity();
        if (!(entity.getType().equals(org.bukkit.entity.EntityType.PLAYER))) {
            final Player killer = entity.getKiller();
            if (killer != null) {
                Score.update(
                    ((CraftPlayer) killer).getHandle().playerConnection
                    , getSettings().getMob()
                    , Database.increaseMobKills(killer.getName()));
            }
        }
    }

    @EventHandler
    public void onJoin(final org.bukkit.event.player.PlayerJoinEvent join) {

        final me.games647.scoreboardstats.api.PlayerStats stats = Database.checkAccount(join.getPlayer().getName());

        Score.createScoreboard(
                ((CraftPlayer) join.getPlayer()).getHandle().playerConnection
                , stats.getKills()
                , stats.getDeaths()
                , stats.getMobkills());
    }
}
