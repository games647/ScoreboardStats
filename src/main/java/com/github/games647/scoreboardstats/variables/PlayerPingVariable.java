package com.github.games647.scoreboardstats.variables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class PlayerPingVariable implements ReplaceManager.Replaceable {

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%ping%".equals(variable)) {
            return getReflectionPing(player);
        }

        return UNKOWN_VARIABLE;
    }

    private int getReflectionPing(Player player) {
        try {
            final Method getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
            final Object entityPlayer = getHandleMethod.invoke(player);
            final Field pingField = entityPlayer.getClass().getDeclaredField("ping");

            return pingField.getInt(entityPlayer);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to get the ping for a player.", ex);
            //Rethrow to handle it in ReplaceManager
        }
    }
}
