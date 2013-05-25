package com.github.games647.scoreboardstats;

import com.github.games647.variables.ConfigurationPaths;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import com.github.games647.variables.Permissions;
import com.github.games647.variables.SpecialCharacter;
import java.util.Map;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class SettingsHandler {

    private boolean             pvpStats;
    private boolean             tempScoreboard;
    private boolean             hideVanished;
    private boolean             sound;

    private String              title;
    private String              tempTitle;
    private String              tempColor;
    private String              topType;

    private int                 intervall;
    private int                 topitems;
    private int                 tempShow;
    private int                 tempDisapper;

    private final java.util.Map<String, String> items = new java.util.HashMap<String, String>(10);
    private java.util.List<String> disabledWorlds;

    public SettingsHandler() {
        loadConfig();
    }

    public void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = ScoreboardStats.getInstance().getConfig();

        hideVanished    = config.getBoolean(ConfigurationPaths.HIDE_VANISHED);
        sound           = config.getBoolean(ConfigurationPaths.SOUNDS);
        disabledWorlds  = config.getStringList(ConfigurationPaths.DISABLED_WORLDS);
        pvpStats        = config.getBoolean(ConfigurationPaths.PVPSTATS);

        loaditems(config.getConfigurationSection(ConfigurationPaths.ITEMS));
        title           = translateAlternateColorCodes('&', checkLength(replaceSpecialCharacters(config.getString(ConfigurationPaths.TITLE))));
        intervall       = config.getInt(ConfigurationPaths.UPDATE_DELAY);

        if (config.getBoolean(ConfigurationPaths.TEMP)
                && pvpStats) {
            tempScoreboard  = true;
            tempTitle       = translateAlternateColorCodes('&', checkLength(replaceSpecialCharacters(config.getString(ConfigurationPaths.TEMP_TITLE))));
            topitems        = config.getInt(ConfigurationPaths.TEMP_ITEMS);
            tempShow        = config.getInt(ConfigurationPaths.TEMP_SHOW);
            tempDisapper    = config.getInt(ConfigurationPaths.TEMP_DISAPPER);
            tempColor       = translateAlternateColorCodes('&', config.getString(ConfigurationPaths.TEMP_COLOR));
            topType         = config.getString(ConfigurationPaths.TEMP_TYPE);
        }
    }

    public boolean isPvpStats() {
        return pvpStats;
    }

    public boolean isTempScoreboard() {
        return tempScoreboard;
    }

    public boolean isHideVanished() {
        return hideVanished;
    }

    public String getTitle() {
        return title;
    }

    public String getTempTitle() {
        return tempTitle;
    }

    public String getTempColor() {
        return tempColor;
    }

    public String getTopType() {
        return topType;
    }

    public int getIntervall() {
        return intervall;
    }

    public int getTopitems() {
        return topitems;
    }

    public int getTempShow() {
        return tempShow;
    }

    public int getTempDisapper() {
        return tempDisapper;
    }

    public boolean isSound() {
        return sound;
    }

    public boolean checkWorld(final String world) {
        return disabledWorlds.contains(world);
    }

    public void sendUpdate(final org.bukkit.entity.Player player, final boolean complete) {
        final org.bukkit.scoreboard.Objective objective = player.getScoreboard().getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        if (!player.hasPermission(Permissions.USE_PERMISSION)
                || objective == null
                || !objective.getName().equals(Other.PLUGIN_NAME)) {
            return;
        }

        for (final Map.Entry<String, String> entry : items.entrySet()) {
            com.github.games647.scoreboardstats.scoreboard.SbManager.sendScore(
                    objective, entry.getKey(), com.github.games647.scoreboardstats.scoreboard.VariableReplacer.getReplacedInt(entry.getValue(), player), complete);
        }
    }

    private static String checkLength(final String check) {
        if (check.length() > Other.MINECRAFT_LIMIT) {
            final String logmessage = Message.LOG_NAME + String.format(Message.LONGER_THAN_LIMIT, check);
            Bukkit.getLogger().warning(logmessage);
            return check.substring(0, Other.MINECRAFT_LIMIT);
        }

        return check;
    }

    private void loaditems(final org.bukkit.configuration.ConfigurationSection config) {
        final java.util.Set<String> keys = config.getKeys(false);

        if (!items.isEmpty()) {
            items.clear();
        }

        for (final String key : keys) {
            items.put(translateAlternateColorCodes('&', checkLength(replaceSpecialCharacters(key))), config.getString(key));
        }
    }

    private static String replaceSpecialCharacters(final String input) {
        return input
                .replace(SpecialCharacter.VAR_HEART, SpecialCharacter.HEART)
                .replace(SpecialCharacter.VAR_CHECK, SpecialCharacter.CHECK)
                .replace(SpecialCharacter.VAR_LESS, SpecialCharacter.LESS)
                .replace(SpecialCharacter.VAR_BIGGER, SpecialCharacter.BIGGER)
                .replace(SpecialCharacter.VAR_STAR, SpecialCharacter.STAR)
                .replace(SpecialCharacter.VAR_ROUND_STAR, SpecialCharacter.ROUND_STAR)
                .replace(SpecialCharacter.VAR_STARS, SpecialCharacter.STARS)
                .replace(SpecialCharacter.VAR_CROWN, SpecialCharacter.CROWN)
                .replace(SpecialCharacter.VAR_CHESS, SpecialCharacter.CHESS)
                .replace(SpecialCharacter.VAR_TOP, SpecialCharacter.TOP)
                .replace(SpecialCharacter.VAR_BUTTON, SpecialCharacter.BUTTON)
                .replace(SpecialCharacter.VAR_SIDE, SpecialCharacter.SIDE)
                .replace(SpecialCharacter.VAR_MID, SpecialCharacter.MID)
                .replace(SpecialCharacter.VAR_ONE, SpecialCharacter.ONE)
                .replace(SpecialCharacter.VAR_TWO, SpecialCharacter.TWO)
                .replace(SpecialCharacter.VAR_THREE, SpecialCharacter.THREE)
                .replace(SpecialCharacter.VAR_FOUR, SpecialCharacter.FOUR)
                .replace(SpecialCharacter.VAR_FIVE, SpecialCharacter.FIVE)
                .replace(SpecialCharacter.VAR_SIX, SpecialCharacter.SIX)
                .replace(SpecialCharacter.VAR_SEVEN, SpecialCharacter.SEVEN)
                .replace(SpecialCharacter.VAR_EIGHT, SpecialCharacter.EIGHT)
                .replace(SpecialCharacter.VAR_NINE, SpecialCharacter.NINE)
                .replace(SpecialCharacter.VAR_TEN, SpecialCharacter.TEN);
    }

    @Override
    public String toString() {
        return "SettingsHandler{" + "pvpStats=" + pvpStats + ", tempScoreboard=" + tempScoreboard + ", hideVanished=" + hideVanished
                + ", sound=" + sound + ", title=" + title + ", tempTitle=" + tempTitle + ", tempColor=" + tempColor
                + ", topType=" + topType + ", intervall=" + intervall + ", topitems=" + topitems + ", tempShow=" + tempShow
                + ", tempDisapper=" + tempDisapper + ", items=" + items + ", disabledWorlds=" + disabledWorlds + '}';
    }
}
