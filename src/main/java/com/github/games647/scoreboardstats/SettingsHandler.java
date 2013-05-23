package com.github.games647.scoreboardstats;

import com.github.games647.variables.Other;
import com.github.games647.variables.Permissions;
import java.util.Map;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class SettingsHandler {

    private final java.io.File  datafolder;

    private boolean             pvpStats;
    private boolean             tempScoreboard;
    private boolean             hideVanished;

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

    public SettingsHandler(final ScoreboardStats instance) {
        datafolder = instance.getDataFolder();
        instance.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = org
                .bukkit.configuration.file.YamlConfiguration.loadConfiguration(new java.io.File(datafolder, "config.yml")); //Will not save a other version in the Bukkit Server
        pvpStats = config.getBoolean("enable-pvpstats");
        title = translateAlternateColorCodes('&', checkLength(replaceSpecialCharacters(config.getString("Scoreboard.Title"))));
        disabledWorlds = config.getStringList("disabled-worlds");
        intervall = config.getInt("Scoreboard.Update-delay");
        tempScoreboard = config.getBoolean("Temp-Scoreboard-enabled") && pvpStats;
        tempTitle = translateAlternateColorCodes('&', checkLength(replaceSpecialCharacters(config.getString("Temp-Scoreboard.Title"))));
        topitems = config.getInt("Temp-Scoreboard.Items");
        tempShow = config.getInt("Temp-Scoreboard.Intervall-show");
        tempDisapper = config.getInt("Temp-Scoreboard.Intervall-disappear");
        tempColor = translateAlternateColorCodes('&', config.getString("Temp-Scoreboard.Color"));
        topType = config.getString("Temp-Scoreboard.Type");
        hideVanished = config.getBoolean("hide-vanished");
        loaditems(config.getConfigurationSection("Scoreboard.Items"));
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

    public boolean checkWorld(final String world) {
        return disabledWorlds.contains(world);
    }

    public void sendUpdate(final org.bukkit.entity.Player player, final boolean complete) {
        final org.bukkit.scoreboard.Objective objective = player.getScoreboard().getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        if (!player.hasPermission(Permissions.USE_PERMISSION) || objective == null || !objective.getName().startsWith(Other.PLUGIN_NAME)) {
            return;
        }

        for (final Map.Entry<String, String> entry : items.entrySet()) {
            com.github.games647.scoreboardstats.scoreboard.SbManager.sendScore(
                    objective, entry.getKey(), com.github.games647.scoreboardstats.scoreboard.VariableReplacer.getReplacedInt(entry.getValue(), player), complete);
        }
    }

    private static String checkLength(final String check) {

        return (check.length() > Other.MINECRAFT_LIMIT) ? check.substring(0, Other.MINECRAFT_LIMIT) : check;
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
        return input.replace("[<3]", "❤").replace("[check]", "✔").replace("[<]", "◄").replace("[>]", "►")
                    .replace("[star]", "★").replace("[grid]", "▓").replace("[round_star]", "✪")
                    .replace("[stars]", "⁂").replace("[crown]", "♛").replace("[chess]", "♜").replace("[top]", "▀")
                    .replace("[button]", "▄").replace("[side]", "▌").replace("[mid]", "▬").replace("[1]", "▂").replace("[2]", "▃")
                    .replace("[3]", "▄").replace("[4]", "▅").replace("[5]", "▆").replace("[6]", "▇").replace("[7]", "█")
                    .replace("[8]", "▓").replace("[9]", "▒").replace("[10]", "░");
    }
}
