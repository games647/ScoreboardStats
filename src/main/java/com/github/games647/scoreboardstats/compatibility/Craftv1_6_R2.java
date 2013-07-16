package com.github.games647.scoreboardstats.compatibility;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class Craftv1_6_R2 implements ICraftPlayerPingable {

    @Override
    public int getPlayerPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }
}
