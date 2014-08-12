package com.github.games647.scoreboardstats;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

/**
 * Seperates the code and the messages
 */
public class Lang {

    private static final Lang INSTANCE = new Lang();

    /**
     * Get a localized string.
     *
     * @param key the localization key
     * @return the localized string
     */
    public static String get(String key) {
        //Static method wrapper
        return INSTANCE.getFormatted(key);
    }

    /**
     * Get a localized string.
     *
     * @param key the localization key
     * @param arguments arguments for formatting strings
     * @return localized
     */
    public static String get(String key, Object... arguments) {
        //Static method wrapper
        return INSTANCE.getFormatted(key, arguments);
    }

    /**
     * Get with some utf-8 characters replaced string.
     *
     * @param input where are the utf-8 variables
     * @return the replaced string
     */
    public static String getReplaced(String input) {
        return INSTANCE.getReplacedString(input);
    }


    private final ResourceBundle defaultMessages;
    private final ResourceBundle utfCharacters;

    private boolean logedWarning;

    /**
     *
     */
    public Lang() {
        //Warning this can be null. - ToDo check it
        final ScoreboardStats plugin = (ScoreboardStats) Bukkit.getPluginManager().getPlugin("ScoreboardStats");
        final ClassLoader classLoader = plugin.getClassLoaderBypass();

        defaultMessages = ResourceBundle.getBundle("messages", Locale.getDefault(), classLoader);
        utfCharacters = ResourceBundle.getBundle("characters", Locale.getDefault(), classLoader);
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
        for (String character : utfCharacters.keySet()) {
            final String value = utfCharacters.getString(character);
            replacedInput = replacedInput.replace(character, value);
            if (!logedWarning) {
                Logger.getLogger("ScoreboardStats").warning("You can now put the special characters direct in the configuration.\n\t"
                        + "Additionally this plugins now supports umlauts and any other UTF-8 charcters automatically for all systems.\n\t"
                        + "The variables for the special characters are so no longer needed and are scheduled for deletion for the following version");
                logedWarning = true;
            }
        }

        return replacedInput;
    }
}
