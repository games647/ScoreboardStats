package me.games647.scoreboardstats.api;

import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import net.minecraft.server.v1_5_R2.Packet206SetScoreboardObjective;
import net.minecraft.server.v1_5_R2.Packet207SetScoreboardScore;
import net.minecraft.server.v1_5_R2.Packet208SetScoreboardDisplayObjective;
import net.minecraft.server.v1_5_R2.PlayerConnection;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class Score {

    private static final Packet206SetScoreboardObjective OBJECTIVE = new Packet206SetScoreboardObjective(), CLEARPACKET = new Packet206SetScoreboardObjective(), TOPOBJECTIVE = new Packet206SetScoreboardObjective(), TEMPCLEARPACKET = new Packet206SetScoreboardObjective();
    private static final Packet208SetScoreboardDisplayObjective DISPLAY = new Packet208SetScoreboardDisplayObjective(), TOPDISPLAY = new Packet208SetScoreboardDisplayObjective();

    static {
        final String stitle = getSettings().getTitle();
        OBJECTIVE.a = stitle;
        OBJECTIVE.b = stitle;

        CLEARPACKET.a = stitle;
        CLEARPACKET.b = stitle;
        CLEARPACKET.c = 1;

        DISPLAY.a = 1;
        DISPLAY.b = stitle;

        final String toptitle = getSettings().getTemptitle();
        TOPOBJECTIVE.a = toptitle;
        TOPOBJECTIVE.b = toptitle;

        TOPDISPLAY.a = 1;
        TOPDISPLAY.b = toptitle;

        TEMPCLEARPACKET.a = toptitle;
        TEMPCLEARPACKET.b = toptitle;
        TEMPCLEARPACKET.c = 1;
    }

    public static void createScoreboard(final org.bukkit.entity.Player player, final boolean type) {
        final PlayerConnection con = ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection;

        if (type) {
            con.sendPacket(OBJECTIVE);
            con.sendPacket(DISPLAY);
            getSettings().sendUpdate(player);
            return;
        }

        con.sendPacket(TOPOBJECTIVE);
        con.sendPacket(TOPDISPLAY);
    }

    public static void sendScore(final PlayerConnection con, final String title, final int value, final boolean type) {
        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

        packet.a = translateAlternateColorCodes('&', title);

        if (type) {
            packet.b = getSettings().getTitle();
        } else {
            packet.b = getSettings().getTemptitle();
        }

        packet.c = value;

        con.sendPacket(packet);
    }

    public static void sendRemoveScore(final PlayerConnection con, final String title) {
        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

        packet.a = translateAlternateColorCodes('&', title);
        packet.b = getSettings().getTitle();
        packet.d = 1;

        con.sendPacket(packet);
    }

    public static Packet206SetScoreboardObjective getCLEARPACKET() {
        return CLEARPACKET;
    }

    public static Packet206SetScoreboardObjective getTEMPCLEARPACKET() {
        return TEMPCLEARPACKET;
    }
}
