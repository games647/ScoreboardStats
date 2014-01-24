package com.github.games647.scoreboardstats;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Language {

    private static Language instance;

    //Static method wrapper
    public static String get(String key, Object... arguments) {
        return getInstance().getFormatted(key, arguments);
    }

     //Static method wrapper
    public static String get(String key) {
        return getInstance().getFormatted(key);
    }

    public static void clearCache() {
        //Forward
        ResourceBundle.clearCache();
    }

    private final ResourceBundle defaultMessages = ResourceBundle.getBundle("messages");

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

    private static Language getInstance() {
        synchronized (instance) {
            if (instance == null) {
                instance = new Language();
            }

            return instance;
        }
    }
}
