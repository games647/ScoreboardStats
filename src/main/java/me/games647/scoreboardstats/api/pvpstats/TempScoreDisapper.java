package me.games647.scoreboardstats.api.pvpstats;

import me.games647.scoreboardstats.api.Score;
import org.bukkit.entity.Player;

public final class TempScoreDisapper implements Runnable {

    private final Player player;

    TempScoreDisapper(final Player paramplayer) {
        this.player = paramplayer;
    }

    @Override
    public void run() {
        me.games647.scoreboardstats.listener.PlayerListener.list.remove(player.getName());
        final net.minecraft.server.v1_5_R2.PlayerConnection con = ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection;
        con.sendPacket(Score.getTEMPCLEARPACKET());
        con.sendPacket(Score.getCLEARPACKET());
        Score.createScoreboard(player, true);
    }
}
