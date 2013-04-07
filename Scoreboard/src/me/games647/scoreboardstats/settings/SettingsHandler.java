package me.games647.scoreboardstats.settings;

import com.bergerkiller.bukkit.nolagg.monitor.PerformanceMonitor;
import com.gmail.nossr50.api.ExperienceAPI;
import java.util.Date;
import java.util.List;
import me.games647.scoreboardstats.ScoreboardStats;
import me.games647.scoreboardstats.api.Database;
import static me.games647.scoreboardstats.api.Score.sendScore;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
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
            sendScore(((CraftPlayer) player).getHandle().playerConnection, localtitle, getValue(
                    (String) items.get(localtitle), player));
        }
    }

    private int getValue(final String key, final Player player) { // force to use if/else because of using java 6
        if ("%online%".equals(key)) {
            return Bukkit.getOnlinePlayers().length;
        }
        if ("%free_ram%".equals(key)) {
            return (int) (Runtime.getRuntime().freeMemory() / 1000);
        }
        if ("%max_ram%".equals(key)) {
            return (int) Runtime.getRuntime().maxMemory() / 1000;
        }
        if ("%used_ram".equals(key)) {
            return (int) ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / 1000);
        }
        if ("%date%".equals(key)) {
            return new Date(System.currentTimeMillis()).getDate();
        }
        if ("%time%".equals(key)) {
            return (int) (player.getWorld().getTime() / 1000);
        }
        if ("%lifetime%".equals(key)) {
            return player.getTicksLived();
        }
        if ("%exp%".equals(key)) {
            return player.getTotalExperience();
        }
        if ("%no_damage_ticks%".equals(key)) {
            return player.getNoDamageTicks();
        }
        if ("%xp_to_level%".equals(key)) {
            return player.getExpToLevel();
        }
        if ("%last_damage%".equals(key)) {
            return player.getLastDamage();
        }
        if ("%max_player%".equals(key)) {
            return Bukkit.getMaxPlayers();
        }
        if ("%ping%".equals(key)) {
            return ((CraftPlayer) player).getHandle().ping;
        }
        if ("%first_day%".equals(key)) {
            return new Date(player.getFirstPlayed()).getDay();
        }
        if ("%first_month%".equals(key)) {
            return new Date(player.getFirstPlayed()).getMonth();
        }
        if ((ScoreboardStats.getEcon() != null) && ("%econ%".equals(key))) {
            return (int) ScoreboardStats.getEcon().getBalance(player.getName());
        }
        if (pvpstats) { //own pvp stats
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
        if (ScoreboardStats.isMcmmo()) { //mcMMO part
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
        if (ScoreboardStats.isMobarena()) {

        }
        if ((ScoreboardStats.isNolagg()) && ("%ticks%".equals(key))) {
            return (int) PerformanceMonitor.tps;
        }
        if (ScoreboardStats.isPaintball()) {

        }
        if (ScoreboardStats.isSimpleclans()) {
            if ("%kills%".equals(key)) {

            }
            if ("%deaths%".equals(key)) {

            }
            if ("%mob%".equals(key)) {

            }
            if ("%kdr%".equals(key)) {

            }
        }
        if (ScoreboardStats.isSurvival()) {

        }

        return -1;
    }
}
