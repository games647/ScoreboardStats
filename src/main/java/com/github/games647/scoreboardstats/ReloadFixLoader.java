package com.github.games647.scoreboardstats;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReloadFixLoader extends ClassLoader {

    private final File pluginFile = ScoreboardStats.getInstance().getFile();

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

            closeStreamSafely(jarFile);
        } catch (IOException ex) {
            //Can't use finally because the jarFile have to be openen when we return the stream
            closeStreamSafely(jarFile);
        }

        return null;
    }

    private void closeStreamSafely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
                Logger.getLogger(ReloadFixLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
