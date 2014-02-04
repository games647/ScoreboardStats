package com.github.games647.scoreboardstats;

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

    public static ClassLoader newInstance() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
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

            closeQuietly(jarFile);
        } catch (IOException ex) {
            closeQuietly(jarFile);
            Logger.getLogger("ScoreboardStats")
                    .log(Level.WARNING, "Couln't load the resourceBundle", ex);
        }

        return null;
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
