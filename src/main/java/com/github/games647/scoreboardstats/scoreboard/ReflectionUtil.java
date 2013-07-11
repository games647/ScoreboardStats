package com.github.games647.scoreboardstats.scoreboard;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.compatibility.ICraftPlayerPing;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.fusesource.jansi.Ansi;

public final class ReflectionUtil  {

    private static final ScoreboardStats PLUGIN = ScoreboardStats.getInstance();

    private static String  bukkitVersion;
    private static boolean skip;
    private static boolean disabled;

    private static Class<?> classCraft;
    private static Method   getHandle;
    private static Field    ping;

    private static ICraftPlayerPing compabilityClass;

    static {
        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        bukkitVersion = packageName.substring(packageName.lastIndexOf('.') + 1);

        setupClass();
    }

    private static void setupClass() {
        try {
            compabilityClass = (ICraftPlayerPing) Class.forName("com.github.games647.scoreboardstats.compatibility.Craft" + bukkitVersion).newInstance();
            
            return;
        } catch (ClassNotFoundException ex) {
            PLUGIN.getLogger().log(Level.FINE, "The class couldn't be found", ex);
        } catch (InstantiationException ex) {
            PLUGIN.getLogger().log(Level.FINE, "Error while instantiation", ex);
        } catch (IllegalAccessException ex) {
            PLUGIN.getLogger().log(Level.FINE, "Error while accesing", ex);
        }

        skip = true;
        PLUGIN.getLogger().log(Level.INFO, "{0}The Plugin isn''t compatible with your craftbukkit version. It will now try to use Reflections{1}"
                , new Object[] {Ansi.ansi().fg(Ansi.Color.YELLOW)
                        , Ansi.ansi().fg(Ansi.Color.DEFAULT)});
    }

    protected static int getPlayerPing(Player player) {
        if (skip) {
            return getReflectionPing(player);
        } else {
            return compabilityClass.getPlayerPing(player);
        }
    }

    private static int getReflectionPing(Player player) {
        if (disabled) {
            return -1;
        }

        try {
            final Object localPing = ping.get(getHandle.invoke(classCraft.cast(player)));

            if (localPing instanceof Integer) {
                return (Integer) localPing;
            }

        } catch (IllegalArgumentException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error"
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);
        } catch (SecurityException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Your vm doesn't allow this plugin to access to the craftbukkit code. This shouldn't happend normaly"
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);
        } catch (IllegalAccessException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Your vm doesn't allow this plugin to access to the craftbukkit code. This shouldn't happend normaly"
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);
        } catch (InvocationTargetException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Please contact the developer with the following error"
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);
        }

        disabled = true;

        return -1;
    }

    public static void initReflections() {
        try {
            classCraft  = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".entity.CraftPlayer");

            getHandle   = classCraft.getDeclaredMethod("getHandle");

            ping        = Class.forName("net.minecraft.server." + bukkitVersion + ".EntityPlayer").getDeclaredField("ping");

            return;
        } catch (ClassNotFoundException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error"
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);
        } catch (NoSuchMethodException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error"
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);
        } catch (NoSuchFieldException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Your craftbukkit version isn't compatible with the plugin version. Please contact the developer with the following error"
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);
        } catch (SecurityException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, Ansi.ansi().fg(Ansi.Color.RED)
                    + "Your vm doesn't allow this plugin to access to the craftbukkit code. This shouldn't happend normaly"
                    + Ansi.ansi().fg(Ansi.Color.DEFAULT), ex);
        }

        disabled = true;
    }
}
