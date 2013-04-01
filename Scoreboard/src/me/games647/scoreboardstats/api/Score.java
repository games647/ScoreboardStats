package me.games647.scoreboardstats.api;

import net.minecraft.server.v1_5_R2.Packet206SetScoreboardObjective;
import net.minecraft.server.v1_5_R2.Packet207SetScoreboardScore;
import net.minecraft.server.v1_5_R2.Packet208SetScoreboardDisplayObjective;
import net.minecraft.server.v1_5_R2.PlayerConnection;

public final class Score {

    private static final String TITLE = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&a&lStats&f");
    private static final String KILLS_TITLE = "§9Kills     ";
    private static final String DEAHS_TITLE = "§9Deaths     ";
    private static final Packet206SetScoreboardObjective OBJECTIVE =  new Packet206SetScoreboardObjective();
    private static final Packet208SetScoreboardDisplayObjective DISPLAY = new Packet208SetScoreboardDisplayObjective();

    static {
        OBJECTIVE.a = TITLE;
        OBJECTIVE.b = TITLE;

        DISPLAY.b = TITLE;
        DISPLAY.a = 1;
    }

    public static void createScoreboard(final PlayerConnection con, final int value_kills, final int value_deaths) {
//        objective.c = 0; Integers have automatically zero as value

        final Packet207SetScoreboardScore kills = new Packet207SetScoreboardScore();
        kills.a = KILLS_TITLE;
        kills.b = TITLE;
        kills.c = value_kills;

        final Packet207SetScoreboardScore deaths = new Packet207SetScoreboardScore();
        deaths.a = DEAHS_TITLE;
        deaths.b = TITLE;
        deaths.c = value_deaths;

        con.sendPacket(OBJECTIVE);
        con.sendPacket(DISPLAY);
        con.sendPacket(kills);
        con.sendPacket(deaths);
    }

    public static void update(final PlayerConnection con, final boolean type, final int value) {
        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

        packet.c = value;

        if (type) {
            packet.a = KILLS_TITLE;
        } else {
            packet.a = DEAHS_TITLE;
        }

        packet.b = TITLE;

        con.sendPacket(packet);
    }
}
