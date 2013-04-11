package me.games647.scoreboardstats.api.pvpstats;

import me.games647.scoreboardstats.api.pvpstats.Database;
import me.games647.scoreboardstats.ScoreboardStats;
import me.games647.scoreboardstats.api.Score;
import me.games647.scoreboardstats.listener.PlayerListener;
import net.minecraft.server.v1_5_R2.PlayerConnection;
import org.bukkit.entity.Player;

public final class TempScoreboardThread implements Runnable {

    public final Player player;

    public TempScoreboardThread(final Player paramplayer) {
        PlayerListener.list.add(paramplayer.getName());
        this.player = paramplayer;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            return;
        }
        final java.util.HashMap<String, Integer> top = Database.getTop();
        Score.createScoreboard(player, false);
        for (String key : top.keySet()) {
            Score.sendScore(
                    ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection
                    , String.format("\u00a79%s", key)
                    , top.get(key)
                    , false);
        }
        org.bukkit.Bukkit.getScheduler().runTaskLater(ScoreboardStats.getInstance(), new Runnable() {
            @Override
            public void run() {
                PlayerListener.list.remove(player.getName());
                final PlayerConnection con = ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection;
                con.sendPacket(Score.getTEMPCLEARPACKET());
                con.sendPacket(Score.getCLEARPACKET());
                Score.createScoreboard(player, true);
            }
        }, ScoreboardStats.getSettings().getTempdisapper() * 20L);
    }
}
