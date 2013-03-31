package me.games647.gscoreboard.api;

import net.minecraft.server.v1_5_R2.Packet206SetScoreboardObjective;
import net.minecraft.server.v1_5_R2.Packet207SetScoreboardScore;
import net.minecraft.server.v1_5_R2.Packet208SetScoreboardDisplayObjective;
import net.minecraft.server.v1_5_R2.PlayerConnection;

public final class Score {

    private static final String TITLE = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&a&lStats&f");

    public static void createScoreboard(final PlayerConnection con, final int value_kills, final int value_deaths) {

        final Packet206SetScoreboardObjective objective = new Packet206SetScoreboardObjective();
        objective.a = TITLE;
        objective.b = TITLE;
//        objective.c = 0; Integers have automatically zero as value

        final Packet208SetScoreboardDisplayObjective display = new Packet208SetScoreboardDisplayObjective();
        display.b = TITLE;
        display.a = 1;

        final Packet207SetScoreboardScore kills = new Packet207SetScoreboardScore();
        kills.a = "§9Kills     ";
        kills.b = TITLE;
        kills.c = value_kills;

        final Packet207SetScoreboardScore deaths = new Packet207SetScoreboardScore();
        deaths.a = "§9Deaths     ";
        deaths.b = TITLE;
        deaths.c = value_deaths;

        con.sendPacket(objective);
        con.sendPacket(display);
        con.sendPacket(kills);
        con.sendPacket(deaths);
    }

    public static void update(final PlayerConnection con, final String key, final int value) {
        final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

        packet.c = value;
        packet.a = key;
        packet.b = TITLE;

        con.sendPacket(packet);
    }
}
