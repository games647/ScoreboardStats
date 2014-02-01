package com.github.games647.scoreboardstats;

import com.google.common.io.Closeables;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class resolves the issue for can't finding a class resource after a server reload
 */
public class ReloadFixLoader extends ClassLoader {

    public static ClassLoader getNewInstance() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return new ReloadFixLoader();
            }
        });
    }

    private final File pluginFile = ScoreboardStats.getInstance().getFileBypass();

    @Override
    public InputStream getResourceAsStream(String name) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(pluginFile);
            for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
                final JarEntry entry = e.nextElement();
                if (entry.getName().equals(name)) {
                    return jarFile.getInputStream(entry);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger("ScoreboardStats").log(Level.WARNING, "Couln't load the resourceBundle", ex);
        } finally {
            //Can't use finally because the jarFile have to be openen when we return the stream
            Closeables.closeQuietly((Closeable) jarFile);
        }

        return null;
    }
}
