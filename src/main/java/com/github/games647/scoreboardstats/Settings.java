package com.github.games647.scoreboardstats;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Managing all general configurations of this plugin.
 */
public final class Settings {

    private static boolean updateEnabled;
    private static boolean compatibilityMode;
    private static boolean pvpStats;
    private static boolean tempScoreboard;
    private static boolean hideVanished;

    private static String title;
    private static String tempTitle;
    private static String tempColor;
    private static String topType;

    private static int intervall;
    private static int topitems;
    private static int tempShow;
    private static int tempDisapper;

    //Sidebar objective can't have more than 15 items
    private static final Map<String, String> ITEMS = Maps.newHashMapWithExpectedSize(15);
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
        return updateEnabled;
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
        return topitems;
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

    private final ScoreboardStats plugin;

    Settings(ScoreboardStats instance) {
        this.plugin = instance;

        plugin.saveDefaultConfig();
    }

    /**
     * Load the configuration file in memory and convert it into simple variables
     */
    public void loadConfig() {
        final FileConfiguration config = getConfigFromDisk();

        //check if compatibilityMode can be activated
        compatibilityMode = isCompatibilityMode(config);

        updateEnabled = config.getBoolean("pluginUpdate");
        hideVanished = config.getBoolean("hide-vanished");
        pvpStats = config.getBoolean("enable-pvpstats");

        //This set only changes after another call to loadConfig so this set can be immutable
        disabledWorlds = ImmutableSet.copyOf(config.getStringList("disabled-worlds"));
        intervall = config.getInt("Scoreboard.Update-delay");
        title = ChatColor.translateAlternateColorCodes('&', trimLength(config.getString("Scoreboard.Title"), 32));

        //Load all normal scoreboard variables
        loaditems(config.getConfigurationSection("Scoreboard.Items"));

        //temp-scoreboard
        tempScoreboard = config.getBoolean("Temp-Scoreboard-enabled") && pvpStats;

        topitems = checkItems(config.getInt("Temp-Scoreboard.Items"));

        tempShow = config.getInt("Temp-Scoreboard.Intervall-show");
        tempDisapper = config.getInt("Temp-Scoreboard.Intervall-disappear");

        topType = config.getString("Temp-Scoreboard.Type");

        tempColor = ChatColor.translateAlternateColorCodes('&', config.getString("Temp-Scoreboard.Color"));
        tempTitle = ChatColor.translateAlternateColorCodes('&',
                trimLength(config.getString("Temp-Scoreboard.Title"), 32));
    }

    /**
     * Gets the YAML file configuration from the disk while loading it
     * explicit with UTF-8. This allows umlauts and other UTF-8 characters
     * for all Bukkit versions.
     *
     * Bukkit add also this feature since
     * https://github.com/Bukkit/Bukkit/commit/24883a61704f78a952e948c429f63c4a2ab39912
     * To be allow the same feature for all Bukkit version, this method was
     * created.
     *
     * @return the loaded file configuration
     */
    public FileConfiguration getConfigFromDisk() {
        final File file = new File(plugin.getDataFolder(), "config.yml");

        final YamlConfiguration newConf = new YamlConfiguration();
        newConf.setDefaults(getDefaults());

        BufferedReader reader = null;
        try {
            //UTF-8 should be available on all java running systems
            reader = Files.newReader(file, Charsets.UTF_8);

            final StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                //indicates a new line
                builder.append('\n');
                line = reader.readLine();
            }

            newConf.loadFromString(builder.toString());
            return newConf;
        } catch (InvalidConfigurationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Invalid Configuration", ex);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't load the configuration", ex);
        } finally {
            Closeables.closeQuietly(reader);
        }

        return newConf;
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
            final String variable = config.getString(key).toLowerCase(Locale.ENGLISH);
            if (variable.charAt(0) == '%' && variable.endsWith("%")) {
                //indicates a variable
                ITEMS.put(name, variable);
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

    private boolean isCompatibilityMode(ConfigurationSection config) {
        final boolean active = config.getBoolean("compatibilityMode");
        if (active) {
            //Check only if it was activated by the administrator
            if (plugin.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
                return true;
            } else {
                //we cannot active compatibilityMode without ProtocolLib
                plugin.getLogger().info(Lang.get("missingProtocolLib"));
            }
        }

        return false;
    }

    /**
     * Get the default configuration located in the plugin jar
     *
     * @return the default configuration
     */
    private FileConfiguration getDefaults() {
        final InputStream defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream != null) {
            //stream will be closed in this method
            return YamlConfiguration.loadConfiguration(defConfigStream);
        }

        return new YamlConfiguration();
    }
}
