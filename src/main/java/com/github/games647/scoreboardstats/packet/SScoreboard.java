package com.github.games647.scoreboardstats.packet;

import net.minecraft.server.v1_5_R3.Packet;
import net.minecraft.server.v1_5_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class SScoreboard {

    private static void sendPacket(final Player player, final Packet packet) {
        final PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;

        if (con != null && !con.disconnected) {
            con.sendPacket(packet);
        }
    }
}
