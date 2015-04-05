package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.config.Lang;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

/**
 * This class resolves the issue that the plugin couldn't find a specific
 * resource after changing the plugin file. The cause is that java still have
 * after the reload a reference to the old file in the cache.
 *
 * @see com.github.games647.scoreboardstats.pvpstats.Database
 */
public class ReloadFixLoader extends ClassLoader {

    /**
     * Disable or enable the use of class caching. Workaround for linking to an
     * old jar version while the plugin jar was changed during a reload.
     *
     * @param status should the cache be activated
     * @return if the process succeed.
     */
    public static boolean setClassCache(boolean status) {
        try {
            final Field cacheField = URLConnection.class.getDeclaredField("defaultUseCaches");
            cacheField.setAccessible(true);
            cacheField.setBoolean(null, status);
            return true;
        } catch (Exception ex) {
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, Lang.get("changeEx"), ex);
            return false;
        }
    }

    private final Plugin plugin;

    /**
     *
     * @param plugin the plugin instance
     * @param parent Bukkits plugin class loader
     */
    public ReloadFixLoader(Plugin plugin, ClassLoader parent) {
        super(parent);

        this.plugin = plugin;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        //This will get the resource and not linking to the old file.
        InputStream resourceStream = plugin.getResource(name);
        if (resourceStream == null) {
            resourceStream = super.getResourceAsStream(name);
        }

        return resourceStream;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        //temporialy fix
        if (name.startsWith("org.joda.time.")) {
            //Bukkits Classloader --> default Classloader
            //Bukkits ClassLoader doens't load it
            return getParent().getParent().loadClass(name);
        }

        return super.loadClass(name);
    }
}
