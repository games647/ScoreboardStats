package com.github.games647.scoreboardstats.variables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

/**
 * Replace the ping variable.
 */
public class PlayerPingVariable implements ReplaceManager.Replaceable {

    private Method getHandleMethod;
    private Field pingField;

    @Override
    public int getScoreValue(Player player, String variable) {
        if ("%ping%".equals(variable)) {
            return getReflectionPing(player);
        }

        return UNKOWN_VARIABLE;
    }

    private int getReflectionPing(Player player) {
        try {
            if (getHandleMethod == null) {
                getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
            }

            final Object entityPlayer = getHandleMethod.invoke(player);

            if (pingField == null) {
                pingField = entityPlayer.getClass().getDeclaredField("ping");
            }

            //returns the found int value
            return pingField.getInt(entityPlayer);
        } catch (Exception ex) {
            //Forward the exception to replaceManager
            throw new RuntimeException(ex);
        }
    }
}
