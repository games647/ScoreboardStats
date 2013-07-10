package com.github.games647.scoreboardstats.compatibility;

import org.bukkit.craftbukkit.v1_5_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class Craftv1_5_R1 implements ICraftPlayerPing {

    @Override
    public int getPlayerPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }
}
