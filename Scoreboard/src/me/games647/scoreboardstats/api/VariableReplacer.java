package me.games647.scoreboardstats.api;

import com.gmail.nossr50.api.ExperienceAPI;
import java.util.Date;
import me.games647.scoreboardstats.ScoreboardStats;
import me.games647.scoreboardstats.listener.PluginListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VariableReplacer {

    // I will try to increase the performance for that before i will released it
    @SuppressWarnings("deprecation")
    public static int getValue(final String key, final Player player) {
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
            return ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().ping;
        }
        if ("%first_day%".equals(key)) {
            return new Date(player.getFirstPlayed()).getDay();
        }
        if ("%first_month%".equals(key)) {
            return new Date(player.getFirstPlayed()).getMonth();
        }
        if ((PluginListener.getEcon() != null) && ("%econ%".equals(key))) {
            return (int) PluginListener.getEcon().getBalance(player.getName());
        }
        if (ScoreboardStats.getSettings().isPvpstats()) {
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
                return -1;
            }
        }
        if (PluginListener.isMcmmo()) {
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
        if (PluginListener.isMobarena()) {
            return -1;
        }
        if ((PluginListener.isNolagg()) && ("%ticks%".equals(key))) {
            return (int) com.bergerkiller.bukkit.nolagg.monitor.PerformanceMonitor.tps;
        }
        if (PluginListener.isPaintball()) {
            return -1;
        }
        if (PluginListener.getSimpleclans() != null) {
            if ("%kills_civilian%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getCivilianKills();
            }
            if ("%kills_neutral%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getNeutralKills();
            }
            if ("%kills_rival%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getRivalKills();
            }
            if ("%deaths%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getDeaths();
            }
            if ("%kdr%".equals(key)) {
                return (int) PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getKDR();
            }
            if ("%mebers%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getMembers().size();
            }
            if ("%clan_kdr%".equals(key)) {
                return (int) PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalKDR();
            }
            if ("%clan_money%".equals(key)) {
                return (int) PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getBalance();
            }
            if ("%members_online%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getOnlineMembers().size();
            }
            if ("%rivals%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getRivals().size();
            }
            if ("%allies%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getAllies().size();
            }
            if ("allies_total%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getAllAllyMembers().size();
            }
            if ("claims%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getAllowedClaims();
            }
            if ("clan_rival%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalRival();
            }
            if ("clan_neutral%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalNeutral();
            }
            if ("clan_deaths%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalDeaths();
            }
            if ("clan_civilian%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getTotalCivilian();
            }
            if ("power%".equals(key)) {
                return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getClan().getPower();
            }
        }
        if (PluginListener.isSurvival()) {
            if ("surival_kills_current".equals(key)) {
                return -1;
            }
            if ("surival_kills".equals(key)) {
                return -1;
            }
            if ("surival_deaths".equals(key)) {
                return -1;
            }
            if ("survival_players".equals(key)) {
                return -1;
            }
        }
        return -1;
    }
}
