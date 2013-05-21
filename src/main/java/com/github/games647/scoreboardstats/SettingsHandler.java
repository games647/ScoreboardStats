package com.github.games647.scoreboardstats;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class SettingsHandler {

    private final java.io.File datafolder;
    private boolean pvpStats
            , tempScoreboard
            , hideVanished;
    private String title
            , tempTitle
            , tempColor
            , topType;
    private int intervall
            , topitems
            , tempShow
            , tempDisapper;
    private final java.util.Map<String, String> items = new java.util.HashMap<String, String>(10);
    private java.util.List<String> disabledWorlds;

    public SettingsHandler(final ScoreboardStats instance) {
        this.datafolder = instance.getDataFolder();
        instance.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(new java.io.File(datafolder, "config.yml")); //Will not save a other version in the Bukkit Server
        this.pvpStats = config.getBoolean("enable-pvpstats");
        this.title = translateAlternateColorCodes('&', checkLength(replaceSpecialCharacters(config.getString("Scoreboard.Title"))));
        this.disabledWorlds = config.getStringList("disabled-worlds");
        this.intervall = config.getInt("Scoreboard.Update-delay");
        this.tempScoreboard = config.getBoolean("Temp-Scoreboard-enabled") && pvpStats;
        this.tempTitle = translateAlternateColorCodes('&', checkLength(replaceSpecialCharacters(config.getString("Temp-Scoreboard.Title"))));
        this.topitems = config.getInt("Temp-Scoreboard.Items");
        this.tempShow = config.getInt("Temp-Scoreboard.Intervall-show");
        this.tempDisapper = config.getInt("Temp-Scoreboard.Intervall-disappear");
        this.tempColor = translateAlternateColorCodes('&', config.getString("Temp-Scoreboard.Color"));
        this.topType = config.getString("Temp-Scoreboard.Type");
        this.hideVanished = config.getBoolean("hide-vanished");
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
        for (String localtitle : items.keySet()) {
            com.github.games647.scoreboardstats.scoreboard.ScoreboardManager.sendScore(
                    player, localtitle, com.github.games647.scoreboardstats.scoreboard.VariableReplacer.getReplacedInt(items.get(localtitle), player), complete);
        }
    }

    private static String checkLength(final String check) {

        return check.length() > 16 ? check.substring(0, 16) : check;
    }

    private void loaditems(final org.bukkit.configuration.ConfigurationSection config) {
        final java.util.Set<String> keys = config.getKeys(false);

        if (!items.isEmpty()) {
            items.clear();
        }

        for (String key : keys) {
            items.put(translateAlternateColorCodes('&', checkLength(replaceSpecialCharacters(key))), config.getString(key));
        }
    }

    private String replaceSpecialCharacters(final String input) {
        return input.replace("[<3]", "❤").replace("[check]", "✔").replace("[<]", "◄").replace("[>]", "►")
                    .replace("[star]", "★").replace("[grid]", "▓").replace("[round_star]", "✪")
                    .replace("[stars]", "⁂").replace("[crown]", "♛").replace("[chess]", "♜").replace("[top]", "▀")
                    .replace("[button]", "▄").replace("[side]", "▌").replace("[1]", "▂").replace("[2]", "▃")
                    .replace("[3]", "▄").replace("[4]", "▅").replace("[5]", "▆").replace("[6]", "▇").replace("[7]", "█")
                    .replace("[8]", "▓").replace("[9]", "▒").replace("[10]", "░");
    }
}
