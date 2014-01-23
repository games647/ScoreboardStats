package com.github.games647.scoreboardstats;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Language {

    private static final Language INSTANCE = new Language();

    //Static method wrapper
    public static String get(String key, Object... arguments) {
        return INSTANCE.getFormatted(key, arguments);
    }

     //Static method wrapper
    public static String get(String key) {
        return INSTANCE.getFormatted(key);
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
}
