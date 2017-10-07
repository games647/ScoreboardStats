package com.github.games647.scoreboardstats.defaults;

import com.github.games647.scoreboardstats.variables.DefaultReplacer;
import com.github.games647.scoreboardstats.variables.DefaultReplacers;
import com.github.games647.scoreboardstats.variables.ReplacerAPI;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Replace the ping variable.
 */
@DefaultReplacer
public class PlayerPingVariable extends DefaultReplacers<Plugin> {

    private Method getHandleMethod;
    private Field pingField;

    public PlayerPingVariable(ReplacerAPI replaceManager, Plugin plugin) {
        super(replaceManager, plugin);
    }

    @Override
    public void register() {
        register("ping").scoreSupply(this::getReflectionPing);
    }

    private int getReflectionPing(Player player) {
        try {
            if (getHandleMethod == null) {
                getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                //disable java security check. This will speed it a little
                getHandleMethod.setAccessible(true);
            }

            Object entityPlayer = getHandleMethod.invoke(player);

            if (pingField == null) {
                pingField = entityPlayer.getClass().getDeclaredField("ping");
                //disable java security check. This will speed it a little
                pingField.setAccessible(true);
            }

            //returns the found int value
            return pingField.getInt(entityPlayer);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException ex) {
            //Forward the exception to replaceManager
            throw new RuntimeException(ex);
        }
    }
}
