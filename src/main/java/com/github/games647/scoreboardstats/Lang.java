package com.github.games647.scoreboardstats;

//import com.google.common.io.Closeables;
//import com.google.common.io.Resources;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
//import java.util.logging.Level;
//import java.util.logging.Logger;

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

//    public static void copyDefault(boolean replace) {
//        copyDefault("characters.properties", replace);
//        copyDefault("messages.properties", replace);
//    }
//
//    public static void copyDefault(String name, boolean replace) {
//        final File file = new File(ScoreboardStats.getInstance().getDataFolder(), name);
//        final URL url = Lang.class.getResource(name);
//        if (!replace && file.exists()) {
//            return;
//        }
//
//        FileOutputStream out = null;
//        try {
//            file.createNewFile();
//
//            out = new FileOutputStream(file);
//            Resources.copy(url, out);
//        } catch (IOException ex) {
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
//        } finally {
//            Closeables.closeQuietly(out);
//        }
//    }

    private final ResourceBundle defaultMessages = ResourceBundle.getBundle("messages", Locale.getDefault(), new ReloadFixLoader());
    private final ResourceBundle utfCharacters = ResourceBundle.getBundle("characters", Locale.getDefault(), new ReloadFixLoader());

    private boolean logedWarning;

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
