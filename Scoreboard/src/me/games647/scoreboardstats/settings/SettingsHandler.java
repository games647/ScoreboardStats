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
            if ("%rank%".equals(key)) {
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
            return -1;
        }
        if ((ScoreboardStats.isNolagg()) && ("%ticks%".equals(key))) {
            return (int) PerformanceMonitor.tps;
        }
        if (ScoreboardStats.isPaintball()) {
            return -1;
        }
        if (ScoreboardStats.getSimpleclans() != null) {
            if ("%kills_civilian%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getCivilianKills();
            }
            if ("%kills_neutral%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getNeutralKills();
            }
            if ("%kills_rival%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getRivalKills();
            }
            if ("%deaths%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getDeaths();
            }
            if ("%kdr%".equals(key)) {
                return (int) ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getKDR();
            }
            if ("%mebers%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getMembers().size();
            }
            if ("%clan_kdr%".equals(key)) {
                return (int) ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalKDR();
            }
            if ("%clan_money%".equals(key)) {
                return (int) ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getBalance();
            }
            if ("%members_online%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getOnlineMembers().size();
            }
            if ("%rivals%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getRivals().size();
            }
            if ("%allies%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getAllies().size();
            }
            if ("allies_total%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getAllAllyMembers().size();
            }
            if ("claims%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getAllowedClaims();
            }
            if ("clan_rival%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalRival();
            }
            if ("clan_neutral%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalNeutral();
            }
            if ("clan_deaths%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalDeaths();
            }
            if ("clan_civilian%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalCivilian();
            }
            if ("power%".equals(key)) {
                return ScoreboardStats.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getPower();
            }
        }
        if (ScoreboardStats.isSurvival()) {
            return -1;
        }

        return -1;
    }
}
