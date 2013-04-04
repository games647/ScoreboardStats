package me.games647.scoreboardstats.listener;

import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import me.games647.scoreboardstats.api.Database;
import me.games647.scoreboardstats.api.Score;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class PlayerListener implements org.bukkit.event.Listener {

    @EventHandler
    public void onDeath(final org.bukkit.event.entity.PlayerDeathEvent death) {

        final Player killed = death.getEntity();
        final Player killer = killed.getKiller();

        Score.update(
                ((CraftPlayer) killed).getHandle().playerConnection, getSettings().getDeaths(), Database.increaseDeaths(killed.getName()));

        if (killer != null) {

            Score.update(
                    ((CraftPlayer) killer).getHandle().playerConnection, getSettings().getKills(), Database.increaseKills(killer.getName()));

        }
    }

    @EventHandler
    public void onJoin(final org.bukkit.event.player.PlayerJoinEvent join) {
        if (!getSettings().checkWorld(join.getPlayer().getWorld().getName())) {
            return;
        }
        final me.games647.scoreboardstats.api.PlayerStats stats = Database.checkAccount(join.getPlayer().getName());
        Score.createScoreboard(
                ((CraftPlayer) join.getPlayer()).getHandle().playerConnection, stats.getKills(), stats.getDeaths(), stats.getMobkills());

    }

    @EventHandler(ignoreCancelled = true)
    public void onChange(final org.bukkit.event.player.PlayerChangedWorldEvent teleport) {
        if (getSettings().checkWorld(teleport.getPlayer().getWorld().getName())) {
            if (!getSettings().checkWorld(teleport.getFrom().getName())) {
               Score.disableScoreboard(((CraftPlayer) teleport.getPlayer()).getHandle().playerConnection);
            }
        } else if (!getSettings().checkWorld(teleport.getFrom().getName())) { // Check if the Scoreboard was activated
            final me.games647.scoreboardstats.api.PlayerStats stats = Database.checkAccount(teleport.getPlayer().getName());
            Score.createScoreboard(
                    ((CraftPlayer) teleport.getPlayer()).getHandle().playerConnection, stats.getKills(), stats.getDeaths(), stats.getMobkills());
        }
    }
}
