package com.github.games647.scoreboardstats.scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.v1_5_R3.Packet;
import net.minecraft.server.v1_5_R3.Packet206SetScoreboardObjective;
import net.minecraft.server.v1_5_R3.Packet207SetScoreboardScore;
import net.minecraft.server.v1_5_R3.Packet208SetScoreboardDisplayObjective;
import net.minecraft.server.v1_5_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class PacketManager {

    private static final Set<String> REGISTERED = new HashSet<String>(10);
    private static final Map<String, SObjective> TEST = new HashMap<String, SObjective>(10);

    private void sendPacket(final Player player, final Packet packet) {
        final PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;

        if (con != null && !con.disconnected) {
            con.sendPacket(packet);
        }
    }

    public void registerObjective(final Player player, final String objectivename, final String displayname, final boolean disabled) {
        if (REGISTERED.contains(player.getName())) {
            return;
        }

        REGISTERED.add(player.getName());

        final Packet206SetScoreboardObjective objective = new Packet206SetScoreboardObjective();

        objective.a = objectivename; //Name
        objective.b = displayname; //Display Name

        if (disabled) {
            objective.c = 1; //Disabled
        }

        sendPacket(player, objective);
    }

    public void sendScore(final Player player, final String objectivename, final String name, final int value, final boolean disabled) {
        if (!REGISTERED.contains(player.getName())) {
            return;
        }

        final Packet207SetScoreboardScore setscore = new Packet207SetScoreboardScore();

        setscore.a = name; //Score name
        setscore.b = objectivename; //The Name for the Objective
        setscore.c = value; //Score

        if (disabled) {
            setscore.d = 1; //Disabled
        }

        sendPacket(player, setscore);
    }

    public void setDisplay(final Player player, final String objectivename, final int displayslot) {
        if (!REGISTERED.contains(player.getName())) {
            return;
        }

        final Packet208SetScoreboardDisplayObjective display = new Packet208SetScoreboardDisplayObjective();

        display.a = displayslot;
        display.b = objectivename;

        sendPacket(player, display);
    }
}
