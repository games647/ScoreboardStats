package com.github.games647.scoreboardstats.variables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.bukkit.Bukkit;
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
                if (Bukkit.getVersion().contains("MCPC")) {
                    //MCPC has a remapper, but it doesn't work if we get the class dynamic
                    setMCPCPing(entityPlayer);
                } else {
                    pingField = entityPlayer.getClass().getDeclaredField("ping");
                }
            }

            //returns the found int value
            return pingField.getInt(entityPlayer);
        } catch (Exception ex) {
            //Forward the exception to replaceManager
            throw new RuntimeException(ex);
        }
    }

    private void setMCPCPing(Object entityPlayer) {
        Class<?> lastType = null;
        Field lastIntField = null;
        for (Field field : entityPlayer.getClass().getDeclaredFields()) {
            if (field.getType() == Integer.TYPE
                    && Modifier.isPublic(field.getModifiers())
                    && lastType == Boolean.TYPE) {
                lastIntField = field;
                continue;
            }

            if (field.getType() == Boolean.TYPE && lastIntField != null) {
                pingField = lastIntField;
                break;
            }

            lastIntField = null;
            lastType = field.getType();
        }
    }
}
