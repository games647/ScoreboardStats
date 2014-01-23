package com.github.games647.scoreboardstats;

import com.google.common.collect.Maps;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class Settings {

    private static boolean             pvpStats;
    private static boolean             tempScoreboard;
    private static boolean             hideVanished;
    private static boolean             updateEnabled;

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

    public static boolean isUpdateEnabled() {
        return updateEnabled;
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

    private final ResourceBundle UTF_CHARACTERS = ResourceBundle.getBundle("characters");

    private final ScoreboardStats pluginInstance;

    public Settings(ScoreboardStats instance) {
        this.pluginInstance = instance;
    }

    public void loadConfig() {
        //Creates a default config and/or load it
        pluginInstance.saveDefaultConfig();
        pluginInstance.reloadConfig();

        final FileConfiguration config = pluginInstance.getConfig();

        loaditems(config.getConfigurationSection("Scoreboard.Items"));

        hideVanished = config.getBoolean("hide-vanished");
        pvpStats = config.getBoolean("enable-pvpstats");
        updateEnabled = config.getBoolean("pluginUpdate");

        disabledWorlds = config.getStringList("disabled-worlds");
        intervall = config.getInt("Scoreboard.Update-delay");
        saveIntervall = config.getInt("PvPStats-SaveIntervall");
        title = ChatColor.translateAlternateColorCodes('&',
                checkLength(replaceUtf8Characters(config.getString("Scoreboard.Title")), 32));

        tempScoreboard = config.getBoolean("Temp-Scoreboard-enabled") && pvpStats;

        topitems = checkItems(config.getInt("Temp-Scoreboard.Items"));

        tempShow = config.getInt("Temp-Scoreboard.Intervall-show");
        tempDisapper = config.getInt("Temp-Scoreboard.Intervall-disappear");

        topType = config.getString("Temp-Scoreboard.Type");

        tempColor = ChatColor.translateAlternateColorCodes('&', config.getString("Temp-Scoreboard.Color"));
        tempTitle = ChatColor.translateAlternateColorCodes('&',
                checkLength(replaceUtf8Characters(config.getString("Temp-Scoreboard.Title")), 32));

    }

    private String checkLength(String check, int limit) {
        if (check.length() > limit) {
            //If the string check is longer cut it down
            final String cut = check.substring(0, limit + 1);
            //We are couting from 0 so plus 1
            pluginInstance.getLogger().warning(Language.get("tooLongName", cut, limit));

            return cut;
        }

        return check;
    }

    private int checkItems(int input) {
        if (input >= 16) {
            pluginInstance.getLogger().warning(Language.get("tooManyItems"));
            return 16 - 1;
        } else if (input <= 0) {
            pluginInstance.getLogger().warning(Language.get("notEnoughItems"));
            return 5;
        }

        return input;
    }

    private void loaditems(ConfigurationSection config) {
        final Set<String> keys = config.getKeys(false);
        if (!ITEMS.isEmpty()) {
            //clear all existing items
            ITEMS.clear();
        }

        for (String key : keys) {
            if (ITEMS.size() == 16 - 1) {
                pluginInstance.getLogger().warning(Language.get("tooManyItems"));
                break;
            }

            final String name = ChatColor.translateAlternateColorCodes('&', checkLength(replaceUtf8Characters(key), 16));
            //Prevent case-sensitive mistakes
            final String variable = config.getString(key).toLowerCase(Locale.ENGLISH);
            ITEMS.put(name, variable);
        }
    }

    private String replaceUtf8Characters(String input) {
        //Replace all utf-8 characters
        String replacedInput = input;
        final Enumeration<String> characters = UTF_CHARACTERS.getKeys();
        for (final Enumeration<String> e = characters; e.hasMoreElements();) {
            final String character = e.nextElement();
            final String value = UTF_CHARACTERS.getString(character);
            replacedInput = replacedInput.replace(character, value);
        }

        return replacedInput;
    }
}
