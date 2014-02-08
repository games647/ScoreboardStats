package com.github.games647.scoreboardstats;

import com.google.common.io.Closeables;
import com.google.common.io.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Used to seperate messages and code.
 */
public class Lang {

    private static final Lang INSTANCE = new Lang();

    //Static method wrapper
    public static String get(String key, Object... arguments) {
        return INSTANCE.getFormatted(key, arguments);
    }

    //Static method wrapper
    public static String get(String key) {
        return INSTANCE.getFormatted(key);
    }

    public static String getReplaced(String input) {
        return INSTANCE.getReplacedString(input);
    }

    public static void copyDefault(boolean replace) {
        copyDefault("characters.properties", replace);
        copyDefault("messages.properties", replace);
    }

    public static void copyDefault(String name, boolean replace) {
        final File file = new File(ScoreboardStats.getInstance().getDataFolder(), name);
        final URL url = Lang.class.getResource(name);
        if (!replace && file.exists()) {
            return;
        }

        FileOutputStream out = null;
        try {
            file.createNewFile();

            out = new FileOutputStream(file);
            Resources.copy(url, out);
        } catch (IOException ex) {
            Logger.getLogger("ScoreboardStats").log(Level.SEVERE, null, ex);
        } finally {
            Closeables.closeQuietly(out);
        }
    }

    private final ResourceBundle defaultMessages = ResourceBundle.getBundle("messages", Locale.getDefault(), ReloadFixLoader.newInstance());
    private final ResourceBundle utfCharacters = ResourceBundle.getBundle("characters", Locale.getDefault(), ReloadFixLoader.newInstance());

    private String getFormatted(String key, Object... arguments) {
        if (defaultMessages.containsKey(key)) {
            String result = defaultMessages.getString(key);
            if (arguments.length != 0) {
                //If there are arguments use messageformat to replace
                result = MessageFormat.format(result, arguments);
            }

            return result;
        }

        return "";
    }

    private String getReplacedString(String input) {
        //Replace all utf-8 characters
        String replacedInput = input;
        final Enumeration<String> characters = utfCharacters.getKeys();
        for (final Enumeration<String> e = characters; e.hasMoreElements();) {
            final String character = e.nextElement();
            final String value = utfCharacters.getString(character);
            replacedInput = replacedInput.replace(character, value);
        }

        return replacedInput;
    }
}
