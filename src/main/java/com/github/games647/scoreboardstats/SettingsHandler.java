package com.github.games647.scoreboardstats;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class SettingsHandler {

    private final ScoreboardStats plugin;
    private boolean pvpstats, tempscoreboard, hidevanished;
    private String title, temptitle, tempcolor, toptype;
    private int intervall, topitems, tempshow, tempdisapper;
    private final java.util.Map<String, String> items = new java.util.HashMap<String, String>();
    private java.util.List<String> disabledworlds;

    public SettingsHandler(final ScoreboardStats instance) {
        this.plugin = instance;
        instance.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = org.bukkit.configuration.file.
                YamlConfiguration.loadConfiguration(new java.io.File(plugin.getDataFolder(), "config.yml")); //Will not save a other version in the Bukkit Server
        this.pvpstats = config.getBoolean("enable-pvpstats");
        this.title = translateAlternateColorCodes('&', checkLength(config.getString("Scoreboard.Title")));
        this.disabledworlds = config.getStringList("disabled-worlds");
        this.intervall = config.getInt("Scoreboard.Update-delay");
        this.tempscoreboard = config.getBoolean("Temp-Scoreboard-enabled") && pvpstats;
        this.temptitle = translateAlternateColorCodes('&', checkLength(config.getString("Temp-Scoreboard.Title")));
        this.topitems = config.getInt("Temp-Scoreboard.Items");
        this.tempshow = config.getInt("Temp-Scoreboard.Intervall-show");
        this.tempdisapper = config.getInt("Temp-Scoreboard.Intervall-disappear");
        this.tempcolor = translateAlternateColorCodes('&', config.getString("Temp-Scoreboard.Color"));
        this.toptype = config.getString("Temp-Scoreboard.Type");
        this.hidevanished = config.getBoolean("hide-vanished");
        loaditems(config.getConfigurationSection("Scoreboard.Items"));
    }

    public String getTitle() {
        return title;
    }

    public boolean isPvpstats() {
        return pvpstats;
    }

    public boolean checkWorld(final String world) {
        return disabledworlds.contains(world);
    }

    public boolean isTempscoreboard() {
        return tempscoreboard;
    }

    public String getTemptitle() {
        return temptitle;
    }

    public int getIntervall() {
        return intervall;
    }

    public int getTopitems() {
        return topitems;
    }

    public int getTempshow() {
        return tempshow;
    }

    public int getTempdisapper() {
        return tempdisapper;
    }

    public String getTempcolor() {
        return tempcolor;
    }

    public String getToptype() {
        return toptype;
    }

    public boolean isHidevanished() {
        return hidevanished;
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
            items.put(translateAlternateColorCodes('&', checkLength(key)), config.getString(key));
        }
    }
}
