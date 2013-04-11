package me.games647.scoreboardstats.listener;

import java.util.concurrent.ConcurrentLinkedQueue;
import static me.games647.scoreboardstats.ScoreboardStats.getInstance;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import me.games647.scoreboardstats.api.Database;
import static me.games647.scoreboardstats.api.Score.createScoreboard;
import static me.games647.scoreboardstats.api.Score.getCLEARPACKET;
import me.games647.scoreboardstats.api.TempScoreboardThread;
import static org.bukkit.Bukkit.getScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class PlayerListener implements org.bukkit.event.Listener {

    public static ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<String>();

    @EventHandler
    public void onDeath(final org.bukkit.event.entity.PlayerDeathEvent death) {

        if ((!getSettings().isPvpstats()) || (getSettings().checkWorld(death.getEntity().getWorld().getName())) || (list.contains(death.getEntity().getName()))) {
            return;
        }
        final Player killed = death.getEntity();
        Database.increaseDeaths(killed.getName());
        final Player killer = killed.getKiller();

        if (killer != null) {
            Database.increaseKills(killer.getName());
        }
    }

    @EventHandler
    public void onJoin(final org.bukkit.event.player.PlayerJoinEvent join) {

        if (getSettings().checkWorld(join.getPlayer().getWorld().getName())) {
            return;
        }

        createScoreboard(join.getPlayer(), true);
        getScheduler().runTaskLater(getInstance(), new TempScoreboardThread(join.getPlayer()), getSettings().getTempshow() * 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChange(final org.bukkit.event.player.PlayerChangedWorldEvent teleport) {

        if (getSettings().checkWorld(teleport.getPlayer().getWorld().getName())) {

            if (!getSettings().checkWorld(teleport.getFrom().getName())) {
                ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) teleport.getPlayer()).getHandle().playerConnection.sendPacket(getCLEARPACKET());
            }

        } else if (getSettings().checkWorld(teleport.getFrom().getName())) { // Check if the Scoreboard was activated
            createScoreboard(teleport.getPlayer(), true);
        }
    }
}
