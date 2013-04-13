package me.games647.scoreboardstats.api.pvpstats;

import me.games647.scoreboardstats.ScoreboardStats;
import me.games647.scoreboardstats.api.Score;
import me.games647.scoreboardstats.listener.PlayerListener;
import org.bukkit.entity.Player;

public final class TempScoreboardThread implements Runnable {

    public final Player player;

    public TempScoreboardThread(final Player paramplayer) {
        this.player = paramplayer;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            return;
        }
        Database.saveAccount(player.getName(), false);
        PlayerListener.list.add(player.getName());
        final java.util.Map<String, Integer> top = Database.getTop();
        Score.createScoreboard(player, false);

        for (String key : top.keySet()) {
            Score.sendScore(
                    ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection
                    , String.format("\u00a79%s", key.length() > 16 ? key.substring(0, 16) : key)
                    , top.get(key)
                    , false);
        }

        org.bukkit.Bukkit.getScheduler().runTaskLater(ScoreboardStats.getInstance(), new Runnable() {
            @Override
            public void run() {
                PlayerListener.list.remove(player.getName());
                final net.minecraft.server.v1_5_R2.PlayerConnection con = ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection;
                con.sendPacket(Score.getTEMPCLEARPACKET());
                con.sendPacket(Score.getCLEARPACKET());
                Score.createScoreboard(player, true);
            }
        }, ScoreboardStats.getSettings().getTempdisapper() * 20L);
    }
}
