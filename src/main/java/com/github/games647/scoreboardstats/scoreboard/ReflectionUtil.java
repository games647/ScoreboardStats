package com.github.games647.scoreboardstats.scoreboard;

import com.github.games647.scoreboardstats.ScoreboardStats;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

final class ReflectionUtil  {

    private ReflectionUtil() {}

    private static final ScoreboardStats PLUGIN = ScoreboardStats.getInstance();
    private static String bukkitVersion;
    private static boolean skip;
    private static boolean disabled;

    static {
       final String packageName    = Bukkit.getServer().getClass().getPackage().getName();
       bukkitVersion  = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    protected static int getPlayerPing(Player player) {
        if (disabled) {
            return -1;
        }

        if (skip) {
            return getReflectionPing(player);
        }

        try {
            final Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer");

            return ((CraftPlayer) player).getHandle().ping;
        } catch (ClassNotFoundException ex) {
            PLUGIN.getLogger().log(Level.FINE, "The class couldn't be found", ex);
            skip = true;
        }

        return getReflectionPing(player);
    }

    private static int getReflectionPing(Player player) {
        try {
            PLUGIN.getLogger().log(Level.FINE, bukkitVersion);

            final Class<?> classCraft   = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".entity.CraftPlayer");
            final Object craftPlayer    = classCraft.cast(player);
            final Method method         = craftPlayer.getClass().getDeclaredMethod("getHandle");

            final Object result         = method.invoke(craftPlayer);
            PLUGIN.getLogger().log(Level.FINE, "The result is:    {0}", result);

            final Object ping = result.getClass().getDeclaredField("ping").get(result);
            PLUGIN.getLogger().log(Level.FINE, "The Ping is:      {0}", ping);

            if (ping instanceof Integer) {
                return (Integer) ping;
            }

        } catch (ClassNotFoundException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error", ex);
        } catch (NoSuchFieldException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error", ex);
        } catch (NoSuchMethodException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error", ex);
        } catch (IllegalArgumentException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error", ex);
        } catch (SecurityException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your vm doesn't allow this plugin to access to the craftbukkit code. This shouldn't happend normaly", ex);
        } catch (IllegalAccessException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your vm doesn't allow this plugin to access to the craftbukkit code. This shouldn't happend normaly", ex);
        } catch (InvocationTargetException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Please contact the developer with the following error", ex);
        }

        disabled = true;
        return -1;
    }
}
