package com.github.games647.scoreboardstats;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Language {

    private static final Language instance = new Language();

    public static String get(String key, Object... arguments) {
       if (instance != null) {
           return instance.getFormatted(key, arguments);
       }

       return "";
    }

    public static String get(String key) {
        return get(key, new Object[0]);
    }

    private final ResourceBundle messageBundle = ResourceBundle.getBundle("messages");

    private String getFormatted(String key, Object... arguments) {
        if (messageBundle.containsKey(key)) {
            String result = messageBundle.getString(key);
            if (arguments.length > 0) {
                result = MessageFormat.format(result, arguments);
            }

            return result;
        }

        return "";
    }
}
