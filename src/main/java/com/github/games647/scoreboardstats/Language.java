package com.github.games647.scoreboardstats;

import java.io.File;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
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

    public static String getReplaced(String input) {
        return INSTANCE.getReplacedString(input);
    }

    private final ResourceBundle defaultMessages = ResourceBundle.getBundle("messages", Locale.getDefault(), new ReloadFixLoader());
    private final ResourceBundle utf_characters = ResourceBundle.getBundle("characters", Locale.getDefault(), new ReloadFixLoader());

    public void load(File file) {
        throw new UnsupportedOperationException();
    }

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
        final Enumeration<String> characters = utf_characters.getKeys();
        for (final Enumeration<String> e = characters; e.hasMoreElements();) {
            final String character = e.nextElement();
            final String value = utf_characters.getString(character);
            replacedInput = replacedInput.replace(character, value);
        }

        return replacedInput;
    }
}
