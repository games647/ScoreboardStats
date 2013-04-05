package me.games647.scoreboardstats.settings;

import java.util.List;
import me.games647.scoreboardstats.ScoreboardStats;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class SettingsHandler {

    private final ScoreboardStats plugin;
    private boolean mobkills;
    private boolean playerkills;
    private boolean death;
    private String title;
    private String kills;
    private String deaths;
    private String mob;
    private List<String> disabledworlds;

    public SettingsHandler(final ScoreboardStats instance) {
        this.plugin = instance;
        this.plugin.saveDefaultConfig();
        loadConfig();
    }

    public boolean Bukkit.getServer().getOnlinePlayers().length() {
        return mobkills;
    }

    public boolean isPlayerkills() {
        return playerkills;
    }

    public boolean isDeath() {
        return death;
    }

    public String getTitle() {
        return title;
    }

    public String getKills() {
        return kills;
    }

    public String getDeaths() {
        return deaths;
    }

    public String getMob() {
        return mob;
    }

    private void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = this.plugin.getConfig();
        this.mobkills = config.getBoolean("mob-kills-enabled");
        this.playerkills = config.getBoolean("player-kills-enabled");
        this.death = config.getBoolean("deaths-enabled");
        this.title = translateAlternateColorCodes('&', config.getString("Title"));
        this.kills = translateAlternateColorCodes('&', config.getString("Kills"));
        this.deaths = translateAlternateColorCodes('&', config.getString("Deaths"));
        this.mob = translateAlternateColorCodes('&', config.getString("Mob-kills"));
        this.disabledworlds = config.getStringList("disabled-worlds");
    }

    public boolean checkWorld(final String world) {
        if (this.disabledworlds.contains(world)) {
            return true;
        } else {
            return false;
        }
    }
}
