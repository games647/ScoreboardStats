package com.github.games647.scoreboardstats;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLConnection;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * This class resolves the issue that the plugin couldn't find a specific resource after
 * changing the plugin file. The cause is that java still have after the reload
 * a reference to the old file in the cache.
 */
public class ReloadFixLoader extends ClassLoader {

    /**
     * Disable or enable the use of class caching. Workaround for linking to an old jar version
     * while the plugin jar was changed during a reload.
     *
     * @param status should the cache be activated
     * @return if the process succeed.
     */
    public static boolean changeClassCache(boolean status) {
        try {
            final Field cacheField = URLConnection.class.getDeclaredField("defaultUseCaches");
            cacheField.setAccessible(true);
            cacheField.setBoolean(null, status);
            return true;
        } catch (Exception ex) {
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, Lang.get("changeChageEx"), ex);
            return false;
        }
    }

    private final File pluginFile = ScoreboardStats.getInstance().getFileBypass();

    @Override
    public InputStream getResourceAsStream(String name) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(pluginFile);
            final ZipEntry entry = jarFile.getEntry(name);
            if (entry != null) {
                return jarFile.getInputStream(entry);
            }

            closeQuietly(jarFile);
        } catch (IOException ex) {
            //We cannot use finally because we need the stream open if return the stream
            closeQuietly(jarFile);
            Logger.getLogger("ScoreboardStats").log(Level.WARNING, "Couln't load the resourceBundle", ex);
        }

        return super.getResourceAsStream(name);
    }

    private void closeQuietly(JarFile file) {
        if (file != null) {
            try {
                file.close();
            } catch (IOException ex) {
                Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
            }
        }
    }
}
