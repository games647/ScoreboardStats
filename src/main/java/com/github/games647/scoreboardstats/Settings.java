package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.scoreboard.SbManager;
import com.github.games647.scoreboardstats.scoreboard.VariableReplacer;
import com.github.games647.variables.ConfigurationPaths;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import com.github.games647.variables.Permissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import org.fusesource.jansi.Ansi;

public final class Settings {

    private static final ScoreboardStats PLUGIN = ScoreboardStats.getInstance();

    private static boolean             pvpStats;
    private static boolean             tempScoreboard;
    private static boolean             hideVanished;
    private static boolean             sound;
    private static boolean             updateInfo;
    private static boolean             packetsystem;

    private static String              title;
    private static String              tempTitle;
    private static String              tempColor;
    private static String              topType;

    private static int                 intervall;
    private static int                 topitems;
    private static int                 tempShow;
    private static int                 tempDisapper;

    private static final Map<String, String> ITEMS = new HashMap<String, String>(14);
    private static List<String> disabledWorlds;

    public static void loadConfig() {
        PLUGIN.saveDefaultConfig();
        PLUGIN.reloadConfig();

        final FileConfiguration config = PLUGIN.getConfig();

        loaditems(config.getConfigurationSection(ConfigurationPaths.ITEMS));

        hideVanished    = config.getBoolean(ConfigurationPaths.HIDE_VANISHED);
        sound           = config.getBoolean(ConfigurationPaths.SOUNDS);
        pvpStats        = config.getBoolean(ConfigurationPaths.PVPSTATS);
        updateInfo      = config.getBoolean(ConfigurationPaths.UPDATE_INFO);
        packetsystem    = config.getBoolean(ConfigurationPaths.PACKET_SYSTEM);

        disabledWorlds  = config.getStringList(ConfigurationPaths.DISABLED_WORLDS);
        intervall       = config.getInt(ConfigurationPaths.UPDATE_DELAY);
        title           = ChatColor.translateAlternateColorCodes(Other.CHATCOLOR_CHAR,
                checkLength(replaceUtf8Characters(config.getString(ConfigurationPaths.TITLE)), Other.OBJECTIVE_LIMIT));

        tempScoreboard  = config.getBoolean(ConfigurationPaths.TEMP) && pvpStats;

        topitems        = checkItems(config.getInt(ConfigurationPaths.TEMP_ITEMS));

        tempShow        = config.getInt(ConfigurationPaths.TEMP_SHOW);
        tempDisapper    = config.getInt(ConfigurationPaths.TEMP_DISAPPER);

        topType         = config.getString(ConfigurationPaths.TEMP_TYPE);

        tempColor       = ChatColor.translateAlternateColorCodes(Other.CHATCOLOR_CHAR, config.getString(ConfigurationPaths.TEMP_COLOR));
        tempTitle       = ChatColor.translateAlternateColorCodes(Other.CHATCOLOR_CHAR,
                    checkLength(replaceUtf8Characters(config.getString(ConfigurationPaths.TEMP_TITLE)), Other.OBJECTIVE_LIMIT));

    }

    public static void sendUpdate(Player player, boolean complete) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

        if (!player.hasPermission(Permissions.USE_PERMISSION)
                || objective == null
                || !objective.getName().equals(Other.PLUGIN_NAME)
                || PLUGIN.getHidelist().contains(player.getName())) {
            return;
        }

        for (final Map.Entry<String, String> entry : ITEMS.entrySet()) {
            SbManager.sendScore(
                    objective, entry.getKey(), VariableReplacer.getReplacedInt(entry.getValue(), player), complete);
        }
    }

    private static String checkLength(String check, int limit) {
        if (check.length() > limit) {
            final String cut = check.substring(0, limit);
            Bukkit.getLogger().log(Level.WARNING, Ansi.ansi().fg(Ansi.Color.RED) + Message.LOG_NAME + "{0}" + Ansi.ansi().fg(Ansi.Color.DEFAULT), String.format(Message.LONGER_THAN_LIMIT, cut, limit));
            return cut;
        }

        return check;
    }

    private static int checkItems(int input) {
        if (input >= Other.MINECRAFT_LIMIT) {
            Bukkit.getLogger().log(Level.WARNING, "{0}" + Message.LOG_NAME + Message.TOO_LONG_LIST + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
            return Other.MINECRAFT_LIMIT - 1;
        }

        return input;
    }

    private static void loaditems(ConfigurationSection config) {
        final Set<String> keys = config.getKeys(false);

        if (!ITEMS.isEmpty()) {
            ITEMS.clear();
        }

        for (final String key : keys) {
            if (ITEMS.size() == Other.MINECRAFT_LIMIT - 1) {
                Bukkit.getLogger().log(Level.WARNING, "{0}" + Message.LOG_NAME + Message.TOO_LONG_LIST + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
                break;
            }

            ITEMS.put(ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, checkLength(replaceUtf8Characters(key), Other.MINECRAFT_LIMIT)), config.getString(key));
        }
    }

    private static String replaceUtf8Characters(String input) {
        final String[][] utf8 = {
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
            {"[X]", "✖"},
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

    public static boolean isPvpStats() {
        return pvpStats;
    }

    public static boolean isTempScoreboard() {
        return tempScoreboard;
    }

    public static boolean isHideVanished() {
        return hideVanished;
    }

    public static boolean isSound() {
        return sound;
    }

    public static boolean isUpdateInfo() {
        return updateInfo;
    }

    public static boolean isPacketsystem() {
        return packetsystem;
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

    public static int getTopitems() {
        return topitems;
    }

    public static int getTempShow() {
        return tempShow;
    }

    public static int getTempDisapper() {
        return tempDisapper;
    }

    public static int getItemsLenght() {
        return ITEMS.size();
    }

    public static boolean isDisabledWorld(String name) {
        return disabledWorlds.contains(name);
    }

    @Override
    public String toString() {
        return "SettingsHandler{pvpStats="  + pvpStats

                + ", tempScoreboard="       + tempScoreboard
                + ", hideVanished="         + hideVanished
                + ", sound="                + sound
                + ", title="                + title
                + ", tempTitle="            + tempTitle
                + ", tempColor="            + tempColor
                + ", topType="              + topType
                + ", intervall="            + intervall
                + ", topitems="             + topitems
                + ", tempShow="             + tempShow
                + ", tempDisapper="         + tempDisapper
                + ", items="                + ITEMS
                + ", disabledWorlds="       + disabledWorlds

                + '}';
    }
}
