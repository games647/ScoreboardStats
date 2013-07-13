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

import lombok.Getter;
import lombok.ToString;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import org.fusesource.jansi.Ansi;

@ToString(includeFieldNames = true)
public final class Settings {

    private static final ScoreboardStats PLUGIN = ScoreboardStats.getInstance();

    @Getter private static boolean             pvpStats;
    @Getter private static boolean             tempScoreboard;
    @Getter private static boolean             hideVanished;
    @Getter private static boolean             sound;
    @Getter private static boolean             updateInfo;
    @Getter private static boolean             packetsystem;

    @Getter private static String              title;
    @Getter private static String              tempTitle;
    @Getter private static String              tempColor;
    @Getter private static String              topType;

    @Getter private static int                 intervall;
    @Getter private static int                 topitems;
    @Getter private static int                 tempShow;
    @Getter private static int                 tempDisapper;

    private static final Map<String, String> ITEMS = new HashMap<String, String>(15);
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

    public static boolean isDisabledWorld(String name) {
        return disabledWorlds.contains(name);
    }
}
