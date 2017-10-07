package com.github.games647.scoreboardstats.config;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

/**
 * Managing all general configurations of this plugin.
 */
public class Settings extends CommentedYaml {

    private static final String MIN_ITEMS = "A scoreboard have to display min. 1 item ({})";
    private static final String TOO_MANY_ITEMS = "Scoreboard can't have more than 15 items";

    @ConfigNode(path = "enable-pvpstats")
    private static boolean pvpStats;

    @ConfigNode(path = "Temp-Scoreboard-enabled")
    private static boolean tempScoreboard;

    private static SidebarConfig mainScoreboard;

    @ConfigNode(path = "Temp-Scoreboard.Title")
    private static String tempTitle;

    @ConfigNode(path = "Temp-Scoreboard.Color")
    private static String tempColor;

    @ConfigNode(path = "Temp-Scoreboard.Type")
    private static String topType;

    @ConfigNode(path = "Scoreboard.Update-delay")
    private static int interval;

    @ConfigNode(path = "Temp-Scoreboard.Items")
    private static int topItems;

    @ConfigNode(path = "Temp-Scoreboard.Interval-show")
    private static int tempShow;

    @ConfigNode(path = "Temp-Scoreboard.Interval-Disappear")
    private static int tempDisappear;

    private static Set<String> disabledWorlds;

    public Settings(Plugin plugin, Logger logger) {
        super(logger, plugin.getDataFolder().toPath());

        plugin.saveDefaultConfig();
    }

    /**
     * Check if a world is from ScoreboardStats active
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
     * Check if the temp-scoreboard is enabled
     *
     * @return if the temp-scoreboard is enabled
     */
    public static boolean isTempScoreboard() {
        return tempScoreboard;
    }

    public static SidebarConfig getMainScoreboard() {
        return mainScoreboard;
    }

    public static String getTempTitle() {
        return tempTitle;
    }

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
    public static int getInterval() {
        return interval;
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
        return tempDisappear;
    }

    /**
     * Load the configuration file in memory and convert it into simple variables
     */
    @Override
    public void loadConfig() {
        super.loadConfig();

        //This set only changes after another call to loadConfig so this set can be immutable
        disabledWorlds = ImmutableSet.copyOf(config.getStringList("disabled-disabledWorlds"));

        tempTitle = trimLength(tempTitle, 32);

        String title = config.getString("Scoreboard.Title");
        mainScoreboard = new SidebarConfig(trimLength(title, 32));
        //Load all normal scoreboard variables
        loadItems(config.getConfigurationSection("Scoreboard.Items"));

        //temp-scoreboard
        tempScoreboard = tempScoreboard && pvpStats;
        topItems = checkItems(topItems);
        topType = topType.replace("%", "");
    }

    private String trimLength(String check, int limit) {
        //Check if the string is longer, so we don't end up with a IndexOutOfBoundEx
        if (check.length() > limit) {
            //If the string check is longer cut it down
            String cut = check.substring(0, limit);
            logger.warn("{} was longer than {} characters. We remove the overlapping characters", cut, limit);

            return cut;
        }

        return check;
    }

    private int checkItems(int input) {
        if (input >= 16) {
            //Only 15 items per sidebar objective are allowed
            logger.warn(TOO_MANY_ITEMS);
            return 16 - 1;
        }

        if (input <= 0) {
            logger.warn(MIN_ITEMS, "tempscoreboard");
            return 5;
        }

        return input;
    }

    private void loadItems(ConfigurationSection config) {
        //clear all existing items
        mainScoreboard.clear();

        //not implemented yet in compatibility mode
        int maxLength = 40;
        for (String key : config.getKeys(false)) {
            if (mainScoreboard.size() == 15) {
                //Only 15 items per sidebar objective are allowed
                logger.warn(TOO_MANY_ITEMS);
                break;
            }

            String displayName = trimLength(key, maxLength);
            String value = config.getString(key);
            if (displayName.contains("%")) {
                String variable = "";
//                mainScoreboard.addVariableItem(true, variable, displayName, value);
            } else if (value.charAt(0) == '%' && value.charAt(value.length() - 1) == '%') {
                //Prevent case-sensitive mistakes
                String variable = value.replace("%", "").toLowerCase();
                mainScoreboard.addVariableItem(false, variable, displayName, 0);
            } else {
                try {
                    int score = Integer.parseInt(value);
                    mainScoreboard.addItem(displayName, score);
                } catch (NumberFormatException numberFormatException) {
                    //Prevent user mistakes
                    logger.info("Replacer {} has to contain % at the beginning and at the end", displayName);
                }
            }
        }

        if (mainScoreboard.size() == 0) {
            //It won't show up if there isn't at least one item
            logger.info(MIN_ITEMS, "scoreboard");
        }
    }
}
