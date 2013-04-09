package me.games647.scoreboardstats.api;

import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import net.minecraft.server.v1_5_R2.Packet206SetScoreboardObjective;
import net.minecraft.server.v1_5_R2.Packet207SetScoreboardScore;
import net.minecraft.server.v1_5_R2.Packet208SetScoreboardDisplayObjective;
import net.minecraft.server.v1_5_R2.PlayerConnection;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class Score {

    private static final String TITLE = getSettings().getTitle();
    private static final Packet206SetScoreboardObjective OBJECTIVE = new Packet206SetScoreboardObjective(), CLEARPACKET = new Packet206SetScoreboardObjective();
    private static final Packet208SetScoreboardDisplayObjective DISPLAY = new Packet208SetScoreboardDisplayObjective();

    static {
        OBJECTIVE.a = TITLE;
        OBJECTIVE.b = TITLE;

        CLEARPACKET.a = TITLE;
        CLEARPACKET.b = TITLE;
        CLEARPACKET.c = 1;

        DISPLAY.a = 1;
        DISPLAY.b = TITLE;
    }

    public static void createScoreboard(final org.bukkit.entity.Player player) {
        final PlayerConnection con = ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection;

        con.sendPacket(OBJECTIVE);
        con.sendPacket(DISPLAY);

        getSettings().sendUpdate(player);
    }

    public static void sendScore(final PlayerConnection con, final String title, final int value) {
        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

        packet.a = translateAlternateColorCodes('&', title);
        packet.b = TITLE;
        packet.c = value;

        con.sendPacket(packet);
    }

    public static Packet206SetScoreboardObjective getClearPacket() {
        return CLEARPACKET;
    }

    public static void sendRemoveScore(final PlayerConnection con, final String title) {
        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

        packet.a = translateAlternateColorCodes('&', title);
        packet.b = TITLE;
        packet.d = 1;

        con.sendPacket(packet);
    }
}
