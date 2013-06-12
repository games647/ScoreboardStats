package com.github.games647.scoreboardstats.packet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.v1_5_R3.Packet207SetScoreboardScore;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public final class SObjective {

    private DisplaySlot displayslot;

    private String      objectivename;
    private String      displayname;

    private int disabled; // 0 to create the scoreboard. 1 to remove the scoreboard. 2 to update the display text. TODO: Check these values
    private Map<String, SScore> scores = new ConcurrentHashMap<String, SScore>(10); // Never should be more than 15

    public SObjective(final DisplaySlot displayslot, final String objectivename, final String displayname) {
        this.displayslot    = displayslot;
        this.objectivename  = objectivename;
        this.displayname    = displayname;
    }

    public SObjective(final String displayname) {
        this.displayname    = displayname;
        this.objectivename  = displayname;
    }

    public DisplaySlot getDisplayslot() {
        return displayslot;
    }

    public void setDisplayslot(final DisplaySlot displayslot) {
        this.displayslot = displayslot;
    }

    public String getObjectivename() {
        return objectivename;
    }

    public void setObjectivename(final String objectivename) {
        this.objectivename = objectivename;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(final String displayname) {
        this.displayname = displayname;
    }

    public void sendDisabledScore(final Player player, final String scorename) {
        final SScore score = scores.get(scorename);

        if (score != null) {
            scores.remove(scorename);

            final Packet207SetScoreboardScore packet = new Packet207SetScoreboardScore();

            packet.a = score.getScorename();
            packet.b = score.getDisplayname();
            packet.c = 1;

            SScoreboard.sendPacket(player, packet);
        }
    }
}
