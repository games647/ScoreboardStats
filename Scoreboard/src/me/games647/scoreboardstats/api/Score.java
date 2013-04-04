package me.games647.scoreboardstats.api;

import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import net.minecraft.server.v1_5_R2.Packet206SetScoreboardObjective;
import net.minecraft.server.v1_5_R2.Packet207SetScoreboardScore;
import net.minecraft.server.v1_5_R2.Packet208SetScoreboardDisplayObjective;
import net.minecraft.server.v1_5_R2.PlayerConnection;

public final class Score {

    private static final String TITLE = getSettings().getTitle();
    private static final String KILLS_TITLE = getSettings().getKills();
    private static final String DEAHS_TITLE = getSettings().getDeaths();
    private static final String MOB_TITLE = getSettings().getMob();
    private static final Packet206SetScoreboardObjective OBJECTIVE = new Packet206SetScoreboardObjective();
    private static final Packet208SetScoreboardDisplayObjective DISPLAY = new Packet208SetScoreboardDisplayObjective();

    static {
        OBJECTIVE.a = TITLE;
        OBJECTIVE.b = TITLE;

        DISPLAY.b = TITLE;
        DISPLAY.a = 1;
    }

    public static void createScoreboard(final PlayerConnection con, final int killsvalue, final int deathsvalue, final int mobvalue) {

        con.sendPacket(OBJECTIVE);
        con.sendPacket(DISPLAY);

        if (getSettings().isPlayerkills()) {
            final Packet207SetScoreboardScore kills = new Packet207SetScoreboardScore();

            kills.a = KILLS_TITLE;
            kills.b = TITLE;
            kills.c = killsvalue;

            con.sendPacket(kills);
        }

        if (getSettings().isDeath()) {
            final Packet207SetScoreboardScore deaths = new Packet207SetScoreboardScore();

            deaths.a = DEAHS_TITLE;
            deaths.b = TITLE;
            deaths.c = deathsvalue;

            con.sendPacket(deaths);
        }


        if (getSettings().isMobkills()) {
            final Packet207SetScoreboardScore mobkills = new Packet207SetScoreboardScore();

            mobkills.a = MOB_TITLE;
            mobkills.b = TITLE;
            mobkills.c = mobvalue;

            con.sendPacket(mobkills);
        }
    }

    public static void update(final PlayerConnection con, final String title, final int value) {
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
