package me.games647.scoreboardstats.api;

import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import net.minecraft.server.v1_5_R2.Packet206SetScoreboardObjective;
import net.minecraft.server.v1_5_R2.Packet207SetScoreboardScore;
import net.minecraft.server.v1_5_R2.Packet208SetScoreboardDisplayObjective;
import net.minecraft.server.v1_5_R2.PlayerConnection;

public final class Score {

    private static final String TITLE = getSettings().getTitle();
    private static final Packet206SetScoreboardObjective OBJECTIVE = new Packet206SetScoreboardObjective();
    private static final Packet208SetScoreboardDisplayObjective DISPLAY = new Packet208SetScoreboardDisplayObjective();

    static {
        OBJECTIVE.a = TITLE;
        OBJECTIVE.b = TITLE;

        DISPLAY.b = TITLE;
        DISPLAY.a = 1;
    }

    public static void createScoreboard(final org.bukkit.entity.Player player) {
        final PlayerConnection con = ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection;
        con.sendPacket(OBJECTIVE);
        con.sendPacket(DISPLAY);
        
    }

    public static void sendScore(final PlayerConnection con, final String title, final int value) {
        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

        packet.a = title;
        packet.b = TITLE;
        packet.c = value;

        con.sendPacket(packet);
    }

    public static void disableScoreboard(final PlayerConnection con) {

        final Packet206SetScoreboardObjective removepacket = new Packet206SetScoreboardObjective();

        removepacket.a = TITLE;
        removepacket.b = TITLE;
        removepacket.c = 1;

        con.sendPacket(removepacket);
    }
}
