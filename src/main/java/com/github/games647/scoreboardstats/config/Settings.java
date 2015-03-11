package com.github.games647.scoreboardstats.config;

import com.github.games647.scoreboardstats.Lang;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Managing all general configurations of this plugin.
 */
public class Settings extends CommentedYaml<ScoreboardStats> {

    private static boolean pluginUpdate;
    private static boolean compatibilityMode;

    @ConfigNode(path = "enable-pvpstats")
    private static boolean pvpStats;

    @ConfigNode(path = "Temp-Scoreboard-enabled")
    private static boolean tempScoreboard;

    @ConfigNode(path = "hide-vanished")
    private static boolean hideVanished;

    @ConfigNode(path = "Scoreboard.Title")
    private static String title;

    @ConfigNode(path = "Temp-Scoreboard.Title")
    private static String tempTitle;

    @ConfigNode(path = "Temp-Scoreboard.Color")
    private static String tempColor;

    @ConfigNode(path = "Temp-Scoreboard.Type")
    private static String topType;

    @ConfigNode(path = "Scoreboard.Update-delay")
    private static int intervall;

    @ConfigNode(path = "Temp-Scoreboard.Items")
    private static int topItems;

    @ConfigNode(path = "Temp-Scoreboard.Intervall-show")
    private static int tempShow;

    @ConfigNode(path = "Temp-Scoreboard.Intervall-disappear")
    private static int tempDisapper;

    //properly a memory leak
    //Sidebar objective can't have more than 15 items
    private static final Map<String, String> ITEMS = Maps.newHashMapWithExpectedSize(15);
    private static final Map<String, String> ITEM_NAMES = Maps.newHashMapWithExpectedSize(15);
    private static Set<String> disabledWorlds;

    /**
     * Get an iterator of all items in the main scoreboard
     *
     * @return an iterator of the configurated items in the main scoreboard
     */
    public static Iterator<Map.Entry<String, String>> getItems() {
        return ITEMS.entrySet().iterator();
    }

    /**
     * Get the display name for the score item
     *
     * @param variable the variable
     * @return the display name
     */
    public static String getItemName(String variable) {
        return ITEM_NAMES.get(variable);
    }

    /**
     * Check if a world is from ScoreboardStats ignored
     *
     * @param worldName the checked world
     * @return if the world is disabled
     */
    public static boolean isActiveWorld(String worldName) {
        return !disabledWorlds.contains(worldName);
    }

    /**
     * Check whether tracking of players stats is enabled
     *
     * @return whether tracking of players stats is enabled
     */
    public static boolean isPvpStats() {
        return pvpStats;
    }

    /**
     * Check whether compatibility mode that ScoreboardStats should operate
     * over raw packets instead of using the Bukkit API.
     *
     * @return whether compatibility mode that ScoreboardStats should operate over raw packets
     */
    public static boolean isCompatibilityMode() {
        return compatibilityMode;
    }

    /**
     * Check if the temp-scoreboard is enabled
     *
     * @return if the temp-scoreboard is enabled
     */
    public static boolean isTempScoreboard() {
        return tempScoreboard;
    }

    /**
     * Check if the plugin should ignore vanished player for online counting
     *
     * @return if the plugin should ignore vanished player for online counting
     */
    public static boolean isHideVanished() {
        return hideVanished;
    }

    /**
     * Check if update checking is enabled
     *
     * @return if update checking is enabled
     */
    public static boolean isUpdateEnabled() {
        return pluginUpdate;
    }

    /**
     * Get the title of the main scoreboard
     *
     * @return the title of the main scoreboard
     */
    public static String getTitle() {
        return title;
    }

    /**
     * Get the title of the temp-scoreboard
     *
     * @return the title of the temp-scoreboard
     */
    public static String getTempTitle() {
        return tempTitle;
    }

    /**
     * Get the color for items in the temp-scoreboard
     *
     * @return the color for items in the temp-scoreboard
     */
    public static String getTempColor() {
        return tempColor;
    }

    /**
     * Get the type what the temp-scoreboard should display
     *
     * @return what the temp-scoreboard should display
     */
    public static String getTopType() {
        return topType;
    }

    /**
     * Get the interval in which the items being refreshed.
     *
     * @return the interval in which the items being refreshed.
     */
    public static int getIntervall() {
        return intervall;
    }

    /**
     * Get how many items the temp-scoreboard should have
     *
     * @return how many items the temp-scoreboard should have
     */
    public static int getTopitems() {
        return topItems;
    }

    /**
     * Get the seconds after the temp-scoreboard should appear.
     *
     * @return the seconds after the temp-scoreboard should appear
     */
    public static int getTempAppear() {
        return tempShow;
    }

    /**
     * Get the seconds after the temp-scoreboard should disappear.
     *
     * @return the seconds after the temp-scoreboard should disappear
     */
    public static int getTempDisappear() {
        return tempDisapper;
    }

    public Settings(ScoreboardStats instance) {
        super(instance);

        plugin.saveDefaultConfig();
    }

    /**
     * Load the configuration file in memory and convert it into simple variables
     */
    @Override
    public void loadConfig() {
        super.loadConfig();

        //check if compatibilityMode can be activated
        compatibilityMode = isCompatibilityMode(compatibilityMode);

        //This set only changes after another call to loadConfig so this set can be immutable
        disabledWorlds = ImmutableSet.copyOf(config.getStringList("disabled-worlds"));

        title = trimLength(title, 32);
        tempTitle = trimLength(tempTitle, 32);

        //Load all normal scoreboard variables
        loaditems(config.getConfigurationSection("Scoreboard.Items"));

        //temp-scoreboard
        tempScoreboard = tempScoreboard && pvpStats;
        topItems = checkItems(topItems);
    }

    private String trimLength(String check, int limit) {
        //Check if the string is longer, so we don't end up with a indexoutofboundex
        if (check.length() > limit) {
            //If the string check is longer cut it down
            final String cut = check.substring(0, limit);
            plugin.getLogger().warning(Lang.get("tooLongName", cut, limit));

            return cut;
        }

        return check;
    }

    private int checkItems(int input) {
        if (input >= 16) {
            //Only 15 items per sidebar objective are allowed
            plugin.getLogger().warning(Lang.get("tooManyItems"));
            return 16 - 1;
        }

        if (input <= 0) {
            plugin.getLogger().warning(Lang.get("notEnoughItems", "tempscoreboard"));
            return 5;
        }

        return input;
    }

    private void loaditems(ConfigurationSection config) {
        //clear all existing items
        ITEMS.clear();

        //not implemented yet in compatibility mode
        final int maxLength = compatibilityMode ? 16 : 48;
        for (String key : config.getKeys(false)) {
            if (ITEMS.size() == 16 - 1) {
                //Only 15 items per sidebar objective are allowed
                plugin.getLogger().warning(Lang.get("tooManyItems"));
                break;
            }

            final String name = ChatColor.translateAlternateColorCodes('&', trimLength(key, maxLength));
            //Prevent case-sensitive mistakes
            final String variable = config.getString(key).toLowerCase();
            if (variable.charAt(0) == '%' && variable.charAt(variable.length() - 1) == '%') {
                //indicates a variable
                ITEMS.put(name, variable.replace("%", ""));
                ITEM_NAMES.put(variable, name);
            } else {
                //Prevent user mistakes
                plugin.getLogger().info(Lang.get("missingVariableSymbol", name));
            }
        }

        if (ITEMS.isEmpty()) {
            //It won't show up if there isn't at least one item
            plugin.getLogger().info(Lang.get("notEnoughItems", "scoreboard"));
        }
    }

    //Inform the user that he should use compatibility modus to be compatible with some plugins
    private boolean isCompatibilityMode(boolean active) {
        if (active) {
            if (!plugin.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
                //we cannot active compatibilityMode without ProtocolLib
                plugin.getLogger().info(Lang.get("missingProtocolLib"));
                return false;
            }
        } else {
            //Thise plugins won't work without compatibilityMode, but do with it, so suggest it
            final String[] plugins = {"HealthBar", "ColoredTags", "McCombatLevel", "Ghost_Player"};
            for (String name : plugins) {
                if (plugin.getServer().getPluginManager().getPlugin(name) == null) {
                    //just check if the plugin is available not if it's active
                    continue;
                }

                //Found minimum one plugin. Inform the adminstrator
                plugin.getLogger().info("You are using plugins that requires to activate compatibilityMode");
                plugin.getLogger().info("Otherwise the plugins won't work");
                plugin.getLogger().log(Level.INFO, "Enable it in the {0} config compatibilityMode", plugin.getName());
                plugin.getLogger().info("Then this plugin will send raw packets and be compatible with other plugins");
                break;
            }
        }

        return active;
    }
}
