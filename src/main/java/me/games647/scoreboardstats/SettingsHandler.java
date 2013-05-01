package me.games647.scoreboardstats;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class SettingsHandler {

    private final ScoreboardStats plugin;
    private boolean pvpstats, tempscoreboard;
    private String title, temptitle, tempcolor, toptype;
    private int intervall, topitems, tempshow, tempdisapper;
    private java.util.Map<String, Object> items = new java.util.HashMap<String, Object>();
    private java.util.List<String> disabledworlds;

    public SettingsHandler(final ScoreboardStats instance) {
        this.plugin = instance;
        instance.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = this.plugin.getConfig();
        this.pvpstats = config.getBoolean("enable-pvpstats");
        this.title = translateAlternateColorCodes('&', checkLength(config.getString("Scoreboard.Title")));
        this.disabledworlds = config.getStringList("disabled-worlds");
        this.intervall = config.getInt("Scoreboard.Update-delay");
        this.tempscoreboard = config.getBoolean("Temp-Scoreboard-enabled");
        this.temptitle = translateAlternateColorCodes('&', checkLength(config.getString("Temp-Scoreboard.Title")));
        this.topitems = config.getInt("Temp-Scoreboard.Items");
        this.tempshow = config.getInt("Temp-Scoreboard.Intervall-show");
        this.tempdisapper = config.getInt("Temp-Scoreboard.Intervall-disappear");
        this.tempcolor = translateAlternateColorCodes('&', config.getString("Temp-Scoreboard.Color"));
        this.toptype = config.getString("Temp-Scoreboard.Type");
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

    public void sendUpdate(final org.bukkit.entity.Player player, final boolean complete) {
        for (String localtitle : items.keySet()) {
            me.games647.scoreboardstats.api.Score.sendScore(
                    player
                    , localtitle
                    , me.games647.scoreboardstats.api.VariableReplacer.getReplacedInt((String) items.get(localtitle), player), complete);
        }
    }

    private static String checkLength(final String check) {
        if (check.length() < 17) {
            return check;
        }

        return check.substring(0, 16);
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
