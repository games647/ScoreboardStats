package com.github.games647.scoreboardstats;

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
        InputStream inputStream = null;
        
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(pluginFile);
            for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
                final JarEntry entry = e.nextElement();
                if (entry.getName().equals(name)) {
                    inputStream = jarFile.getInputStream(entry);
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException ex) {
                    Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return inputStream;
    }
}
