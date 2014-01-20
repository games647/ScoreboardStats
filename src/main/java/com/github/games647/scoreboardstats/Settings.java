package com.github.games647.scoreboardstats;

import com.google.common.collect.Maps;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    private static final ScoreboardStats PLUGIN = ScoreboardStats.getInstance();
    private static final ResourceBundle SPECIAL_CHARACTERS = ResourceBundle.getBundle("characters");

    private static boolean             pvpStats;
    private static boolean             tempScoreboard;
    private static boolean             hideVanished;

    private static String              title;
    private static String              tempTitle;
    private static String              tempColor;
    private static String              topType;

    private static int                 intervall;
    private static int                 saveIntervall;
    private static int                 topitems;
    private static int                 tempShow;
    private static int                 tempDisapper;

    private static final Map<String, String> ITEMS = Maps.newHashMap();
    private static List<String> disabledWorlds;

    public static void loadConfig() {
        PLUGIN.saveDefaultConfig();
        PLUGIN.reloadConfig();

        final FileConfiguration config = PLUGIN.getConfig();

        loaditems(config.getConfigurationSection("Scoreboard.Items"));

        hideVanished    = config.getBoolean("hide-vanished");
        pvpStats        = config.getBoolean("enable-pvpstats");

        disabledWorlds  = config.getStringList("disabled-worlds");
        intervall       = config.getInt("Scoreboard.Update-delay");
        saveIntervall   = config.getInt("PvPStats-SaveIntervall");
        title           = ChatColor.translateAlternateColorCodes('&',
                checkLength(replaceUtf8Characters(config.getString("Scoreboard.Title")), 32));

        tempScoreboard  = config.getBoolean("Temp-Scoreboard-enabled") && pvpStats;

        topitems        = checkItems(config.getInt("Temp-Scoreboard.Items"));

        tempShow        = config.getInt("Temp-Scoreboard.Intervall-show");
        tempDisapper    = config.getInt("Temp-Scoreboard.Intervall-disappear");

        topType         = config.getString("Temp-Scoreboard.Type");

        tempColor       = ChatColor.translateAlternateColorCodes('&', config.getString("Temp-Scoreboard.Color"));
        tempTitle       = ChatColor.translateAlternateColorCodes('&',
                    checkLength(replaceUtf8Characters(config.getString("Temp-Scoreboard.Title")), 32));

    }

    public static int getItemsLenght() {
        return ITEMS.size();
    }

    public static Iterator<Map.Entry<String, String>> getItems() {
        return ITEMS.entrySet().iterator();
    }

    public static boolean isDisabledWorld(World world) {
        return disabledWorlds.contains(world.getName());
    }

    public static boolean isPvpStats() {
        return pvpStats;
    }

    public static boolean isTempScoreboard() {
        return tempScoreboard;
    }

    public static boolean isHideVanished() {
        return hideVanished;
    }

    public static String getTitle() {
        return title;
    }

    public static String getTempTitle() {
        return tempTitle;
    }

    public static String getTempColor() {
        return tempColor;
    }

    public static String getTopType() {
        return topType;
    }

    public static int getIntervall() {
        return intervall;
    }

    public static int getSaveIntervall() {
        return saveIntervall;
    }

    public static int getTopitems() {
        return topitems;
    }

    public static int getTempShow() {
        return tempShow;
    }

    public static int getTempDisapper() {
        return tempDisapper;
    }

    private Settings() {
        //Singleton
    }

    private static String checkLength(String check, int limit) {
        if (check.length() > limit) {
            final String cut = check.substring(0, limit);
            Bukkit.getLogger().warning(Language.get("toLongName", cut, limit));

            return cut;
        }

        return check;
    }

    private static int checkItems(int input) {
        if (input >= 16) {
            Bukkit.getLogger().warning(Language.get("toManyItems"));
            return 16 - 1;
        }

        return input;
    }

    private static void loaditems(ConfigurationSection config) {
        final Set<String> keys = config.getKeys(false);
        if (!ITEMS.isEmpty()) {
            ITEMS.clear();
        }

        for (final String key : keys) {
            if (ITEMS.size() == (16 - 1)) {
                Bukkit.getLogger().warning(Language.get("toManyItems"));
                break;
            }

            ITEMS.put(ChatColor.translateAlternateColorCodes('&', checkLength(replaceUtf8Characters(key), 16)), config.getString(key));
        }
    }

    private static String replaceUtf8Characters(String input) {
        String replacedInput = input;
        final Enumeration<String> characters = SPECIAL_CHARACTERS.getKeys();
        for (Enumeration<String> e = characters; e.hasMoreElements();) {
            final String character = e.nextElement();
            final String value = SPECIAL_CHARACTERS.getString(character);
            replacedInput = replacedInput.replace(character, value);
        }

        return replacedInput;
    }
}
