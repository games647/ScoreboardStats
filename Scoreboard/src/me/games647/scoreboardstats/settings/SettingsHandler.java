package me.games647.scoreboardstats.settings;

import com.gmail.nossr50.api.ExperienceAPI;
import java.util.List;
import me.games647.scoreboardstats.ScoreboardStats;
import me.games647.scoreboardstats.api.Database;
import static me.games647.scoreboardstats.api.Score.sendScore;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import org.bukkit.entity.Player;

public final class SettingsHandler {

    private final ScoreboardStats plugin;
    private boolean pvpstats;
    private String title;
    private java.util.Map<String, Object> items;
    private List<String> disabledworlds;

    public SettingsHandler(final ScoreboardStats instance) {
        this.plugin = instance;
        instance.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() {
        final org.bukkit.configuration.file.FileConfiguration config = this.plugin.getConfig();
        this.pvpstats = config.getBoolean("enable-pvpstats");
        this.title = translateAlternateColorCodes('&', config.getString("Scoreboard.Title"));
        items = config.getConfigurationSection("Scoreboard.Items").getValues(false);
        this.disabledworlds = config.getStringList("disabled-worlds");
    }

    public String getTitle() {
        return title;
    }

    public boolean isPvpstats() {
        return pvpstats;
    }

    public boolean checkWorld(final String world) {
        return this.disabledworlds.contains(world);
    }

    public void sendUpdate(final Player player) {
        for (String localtitle : items.keySet()) {
            sendScore(((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().playerConnection, localtitle, getValue(
                    (String) items.get(localtitle), player));
        }
    }

    private int getValue(final String key, final Player player) { // force to use if/else because of using java 6
        if ("%online%".equals(key)) {
            return Bukkit.getOnlinePlayers().length;
        }
        if ((ScoreboardStats.getEcon() != null) && ("%econ%".equals(key))) {
            return (int) ScoreboardStats.getEcon().getBalance(player.getName());
        }
        if (pvpstats) {
            if ("%kills%".equals(key)) {
                return Database.checkAccount(player.getName()).getKills();
            }
            if ("%deaths%".equals(key)) {
                return Database.checkAccount(player.getName()).getDeaths();
            }
            if ("%mob%".equals(key)) {
                return Database.checkAccount(player.getName()).getMobkills();
            }
            if ("%kdr%".equals(key)) {
                return Database.getKdr(player.getName());
            }
        }
        if (ScoreboardStats.isMcmmo()) {
            if ("%powlvl%".equals(key)) {
                return ExperienceAPI.getPowerLevel(player);
            }
            if ("%woodcutting%".equals(key)) {
                return ExperienceAPI.getLevel(player, "WOODCUTTING");
            }
            if ("%acrobatics%".equals(key)) {
                return ExperienceAPI.getLevel(player, "ACROBATICS");
            }
            if ("%archery%".equals(key)) {
                return ExperienceAPI.getLevel(player, "ARCHERY");
            }
            if ("%axes%".equals(key)) {
                return ExperienceAPI.getLevel(player, "AXES");
            }
            if ("%excavation%".equals(key)) {
                return ExperienceAPI.getLevel(player, "EXCAVATION");
            }
            if ("%fishing%".equals(key)) {
                return ExperienceAPI.getLevel(player, "FISHING");
            }
            if ("%herbalism%".equals(key)) {
                return ExperienceAPI.getLevel(player, "HERBALISM");
            }
            if ("%mining%".equals(key)) {
                return ExperienceAPI.getLevel(player, "MINING");
            }
            if ("%repair%".equals(key)) {
                return ExperienceAPI.getLevel(player, "REPAIR");
            }
            if ("%smelting%".equals(key)) {
                return ExperienceAPI.getLevel(player, "SMELTING");
            }
            if ("%swords%".equals(key)) {
                return ExperienceAPI.getLevel(player, "SWORDS");
            }
            if ("%taming%".equals(key)) {
                return ExperienceAPI.getLevel(player, "TAMING");
            }
            if ("%unarmed%".equals(key)) {
                return ExperienceAPI.getLevel(player, "UNARMED");
            }
        }
        return -1;
    }
}
