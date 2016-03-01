package com.github.games647.scoreboardstats;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BackwardsCompatibleUtil {

    private static Method onlinePlayersMethod;
    private static boolean oldPlayerMethod;

    static {
        try {
            onlinePlayersMethod = Bukkit.class.getMethod("getOnlinePlayers");
            oldPlayerMethod = !onlinePlayersMethod.getReturnType().isAssignableFrom(Collection.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            //try to fail silently
            Logger.getLogger(BackwardsCompatibleUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Collection<? extends Player> getOnlinePlayers() {
        if (oldPlayerMethod) {
            Object onlinePlayersResult;
            try {
                onlinePlayersResult = onlinePlayersMethod.invoke(Bukkit.getServer());
                return Arrays.asList((Player[]) onlinePlayersResult);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        return Bukkit.getOnlinePlayers();
    }
}
