package me.games647.gscoreboard.listener;


import me.games647.gscoreboard.api.Database;
import me.games647.gscoreboard.api.Score;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class DeathListener implements org.bukkit.event.Listener {

    @EventHandler
    public void onDeath(final org.bukkit.event.entity.PlayerDeathEvent death) {

        final Player killed = death.getEntity();
        final Player killer = killed.getKiller();

        if (killer != null) {

            Score.update(
                    ((CraftPlayer) killer).getHandle().playerConnection
                    , "§9Kills     "
                    , Database.increase(killer.getName(), true));

            Score.update(
                    ((CraftPlayer) killed).getHandle().playerConnection
                    , "§9Deaths     "
                    , Database.increase(killed.getName(), false));
        }
    }

    @EventHandler
    public void onJoin(final org.bukkit.event.player.PlayerJoinEvent join) {

        final me.games647.gscoreboard.api.PlayerStats stats = Database.checkAccount(join.getPlayer().getName());

        Score.createScoreboard(
                ((CraftPlayer) join.getPlayer()).getHandle().playerConnection
                , stats.getKills()
                , stats.getDeaths());
    }
}
