package com.github.games647.scoreboardstats;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import org.fusesource.jansi.Ansi;

public class Settings {

    private Settings() { //Singleton

    }

    private static final ScoreboardStats PLUGIN = ScoreboardStats.getInstance();

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

    private static String checkLength(String check, int limit) {
        if (check.length() > limit) {
            final String cut = check.substring(0, limit);
            Bukkit.getLogger().log(Level.WARNING
                    , Ansi.ansi().fg(Ansi.Color.RED) + "[ScoreboardStats]" + "{0}" + Ansi.ansi().fg(Ansi.Color.DEFAULT)
                    , String.format("%s was longer than the limit of %s characters. This Plugin will cut automatically to the right size.", cut, limit));
            return cut;
        }

        return check;
    }

    private static int checkItems(int input) {
        if (input >= 16) {
            Bukkit.getLogger().log(Level.WARNING, "{0}" + "[ScoreboardStats]" + "One Scoreboard can't have more than 15 items"+ Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
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
            if (ITEMS.size() == 16 - 1) {
                Bukkit.getLogger().log(Level.WARNING, "{0}" + "[ScoreboardStats]" + "One Scoreboard can't have more than 15 items" + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
                break;
            }

            ITEMS.put(ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, checkLength(replaceUtf8Characters(key), 16)), config.getString(key));
        }
    }

    private static String replaceUtf8Characters(String input) {
        final String[][] utf8 = new String[][] {
            {"[<3]"     , "❤"},
            {"[check]"  , "✔"},

            {"[<]" , "◄"},
            {"[>]" , "►"},

            {"[star]"       , "★"},
            {"[round_star]" , "✪"},
            {"[stars]"      , "⁂"},
            {"[T_STAR]"     , "✰"},

            {"[crown]", "♛"},
            {"[chess]", "♜"},

            {"[top]"    , "▀"},
            {"[button]" , "▄"},
            {"[side]"   , "▌"},
            {"[mid]"    , "▬"},

            {"[1]"  , "▂"},
            {"[2]"  , "▃"},
            {"[3]"  , "▄"},
            {"[4]"  , "▅"},
            {"[5]"  , "▆"},
            {"[6]"  , "▇"},
            {"[7]"  , "█"},
            {"[8]"  , "▓"},
            {"[9]"  , "▒"},
            {"[10]" , "░"},

            {"[right_up]"   , "⋰"},
            {"[left_up]"    , "⋱"},

            {"[PHONE]"  , "✆"},
            {"[MAIL]"   , "✉"},
            {"[PLANE]"  , "✈"},
            {"[PENCIL]" , "✎"},
            {"[X]"      , "✖"},
            {"[FLOWER]" , "✿"},

            {"[ARROW]"  , "➽"},
            {"[ARROW1]" , "➨"},
            {"[ARROW2]" , "➤"},
            {"[ARROW3]" , "➜"},
            {"[ARROW4]" , "➨"},

            {"[ONE]"    , "❶"},
            {"[TWO]"    , "❷"},
            {"[THREE]"  , "❸"},
            {"[FOUR]"   , "❹"},
            {"[FIVE]"   , "❺"},
            {"[SIX]"    , "❻"},
            {"[SEVEN]"  , "❼"},
            {"[EIGHT]"  , "❽"},
            {"[NINE]"   , "❾"},
            {"[TEN]"    , "❿"},
        };

        for (String[] temp : utf8) {
            input = input.replace(temp[0], temp[1]);
        }

        return input;
    }

    public static int getItemsLenght() {
        return ITEMS.size();
    }

    public static Iterator<Map.Entry<String, String>> getItems() {
        return ITEMS.entrySet().iterator();
    }

    public static boolean isDisabledWorld(String name) {
        return disabledWorlds.contains(name);
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
}
