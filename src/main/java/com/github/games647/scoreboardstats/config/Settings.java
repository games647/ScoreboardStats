package com.github.games647.scoreboardstats.config;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Managing all general configurations of this plugin.
 */
public class Settings extends CommentedYaml<ScoreboardStats> {

    @ConfigNode(path = "pluginUpdate")
    private static boolean pluginUpdate;

    @ConfigNode(path = "compatibilityMode")
    private static boolean compatibilityMode;

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

    @ConfigNode(path = "Temp-Scoreboard.Intervall-show")
    private static int tempShow;

    @ConfigNode(path = "Temp-Scoreboard.Intervall-disappear")
    private static int tempDisapper;

    private static Set<String> worlds;

    @ConfigNode(path = "disabled-worlds-whitelist")
    private static transient boolean isWhitelist;

    /**
     * Check if a world is from ScoreboardStats active
     *
     * @param worldName the checked world
     * @return if the world is disabled
     */
    public static boolean isActiveWorld(String worldName) {
        if (isWhitelist) {
            return worlds.contains(worldName);
        }

        return !worlds.contains(worldName);
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

    public static boolean isUpdateEnabled() {
        return pluginUpdate;
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
        worlds = ImmutableSet.copyOf(config.getStringList("disabled-worlds"));

        tempTitle = trimLength(tempTitle, 32);

        String title = config.getString("Scoreboard.Title");
        mainScoreboard = new SidebarConfig(trimLength(title, 32));
        //Load all normal scoreboard variables
        loaditems(config.getConfigurationSection("Scoreboard.Items"));

        //temp-scoreboard
        tempScoreboard = tempScoreboard && pvpStats;
        topItems = checkItems(topItems);
        topType = topType.replace("%", "");
    }

    private String trimLength(String check, int limit) {
        //Check if the string is longer, so we don't end up with a indexoutofboundex
        if (check.length() > limit) {
            //If the string check is longer cut it down
            String cut = check.substring(0, limit);
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
        mainScoreboard.clear();

        //not implemented yet in compatibility mode
        int maxLength = compatibilityMode ? 16 : 48;
        for (String key : config.getKeys(false)) {
            if (mainScoreboard.size() == 15) {
                //Only 15 items per sidebar objective are allowed
                plugin.getLogger().warning(Lang.get("tooManyItems"));
                break;
            }

            String displayName = trimLength(key, maxLength);
            //Prevent case-sensitive mistakes
            String variable = config.getString(key).toLowerCase();
            if (variable.charAt(0) == '%' && variable.charAt(variable.length() - 1) == '%') {
                //% indicates a variable
                variable = variable.replace("%", "");
                mainScoreboard.addVariableItem(false, variable, displayName, 0);
            } else {
                try {
                    int score = Integer.parseInt(variable);
                    mainScoreboard.addItem(displayName, score);
                } catch (NumberFormatException numberFormatException) {
                    //Prevent user mistakes
                    plugin.getLogger().info(Lang.get("missingVariableSymbol", displayName));
                }
            }
        }

        if (mainScoreboard.size() == 0) {
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
            //These plugins won't work without compatibilityMode, but do with it, so suggest it
            String[] plugins = {"HealthBar", "ColoredTags", "McCombatLevel", "Ghost_Player"};
            for (String name : plugins) {
                if (plugin.getServer().getPluginManager().getPlugin(name) == null) {
                    //just check if the plugin is available not if it's active
                    continue;
                }

                //Found minimum one plugin. Inform the adminstrator
                plugin.getLogger().info("You are using plugins that requires to activate compatibilityMode");
                plugin.getLogger().info("Otherwise the plugins won't work");
                plugin.getLogger().info("Enable it in the config of this plugin: compatibilityMode");
                //one plugin is enough
                break;
            }
        }

        return active;
    }
}
