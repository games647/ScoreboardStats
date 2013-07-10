package com.github.games647.scoreboardstats.scoreboard;

import com.github.games647.scoreboardstats.ScoreboardStats;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class ReflectionUtil  {

    private static final ScoreboardStats PLUGIN = ScoreboardStats.getInstance();

    private static boolean skip;
    private static boolean disabled;

    private static Class<?> classCraft;
    private static Method   getHandle;
    private static Field    ping;

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
            final Object localPing = ping.get(getHandle.invoke(classCraft.cast(player)));

            if (localPing instanceof Integer) {
                return (Integer) localPing;
            }

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

    public static void init() {
        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        final String bukkitVersion  = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            classCraft  = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".entity.CraftPlayer");

            getHandle   = classCraft.getDeclaredMethod("getHandle");

            ping        = Class.forName("net.minecraft.server." + bukkitVersion + ".EntityPlayer").getDeclaredField("ping");

            return;
        } catch (ClassNotFoundException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error", ex);
        } catch (NoSuchMethodException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error", ex);
        } catch (NoSuchFieldException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error", ex);
        } catch (SecurityException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "Your vm doesn't allow this plugin to access to the craftbukkit code. This shouldn't happend normaly", ex);
        }

        disabled = true;
    }
}
