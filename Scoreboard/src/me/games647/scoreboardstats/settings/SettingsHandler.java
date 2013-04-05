package me.games647.scoreboardstats.settings;

import java.util.List;
import me.games647.scoreboardstats.ScoreboardStats;

public final class SettingsHandler {

    private final ScoreboardStats plugin;
    private boolean pvpstats;
    private String title;
    private int delay;
    private List<String> disabledworlds;

    public SettingsHandler(final ScoreboardStats instance) {
        this.plugin = instance;
        this.plugin.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = this.plugin.getConfig();
        this.pvpstats = config.getBoolean("enable-pvpstats");
        this.title = config.getString("Scoreboard.Title");
        this.delay = config.getInt("Scoreboard.Update-delay");
        this.disabledworlds = config.getStringList("disabled-worlds");
    }

    public String getTitle() {
        return title;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isPvpstats() {
        return pvpstats;
    }

    public boolean checkWorld(final String world) {
        return this.disabledworlds.contains(world);
    }
}
