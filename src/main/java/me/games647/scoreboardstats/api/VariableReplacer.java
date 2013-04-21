package me.games647.scoreboardstats.api;

import com.gmail.nossr50.api.ExperienceAPI;
import java.util.Date;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import me.games647.scoreboardstats.api.pvpstats.Database;
import me.games647.scoreboardstats.listener.PluginListener;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VariableReplacer {

    @SuppressWarnings("deprecation")
    public static int getReplacedInt(final String key, final Player player) {
        if (!player.isOnline()) {
            return -1;
        }

        if (getSettings().isPvpstats()) {
            final int value = getPvpValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        if ((PluginListener.getEcon() != null) && ("%econ%".equals(key))) {
            return (int) PluginListener.getEcon().getBalance(player.getName());
        }

        if (PluginListener.isMcmmo()) {
            final int value = getMcmmoValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        if ((PluginListener.getEssentials() != null) && ("%ticks%".equals(key))) {
            return (int) PluginListener.getEssentials().getAverageTPS();
        }

        if (PluginListener.getSimpleclans() != null) {
            final int value = getSimpleClansValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        final int value = getBukkitValues(key, player);
        if (value != -1) {
            return value;
        }

        return -1;
    }

    public static String getReplacedString(final String variable) {
        return "";
    }

    private static int getPvpValue(final String key, final Player player) {
        if ("%kills%".equals(key)) {
            return Database.getCache(player.getName()).getKills();
        }
        if ("%deaths%".equals(key)) {
            return Database.getCache(player.getName()).getDeaths();
        }
        if ("%mob%".equals(key)) {
            return Database.getCache(player.getName()).getMob();
        }
        if ("%kdr%".equals(key)) {
            return Database.getKdr(player.getName());
        }
        if ("%killstreak%".equals(key)) {
            return Database.getCache(player.getName()).getStreak();
        }
        if ("%current_streak%".equals(key)) {
            return Database.getCache(player.getName()).getLastStreak();
        }
        return -1;
    }

    private static int getMcmmoValue(final String key, final Player player) {
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
        return -1;
    }

    private static int getSimpleClansValue(final String key, final Player player) {
        if ("%kills_civilian%".equals(key)) {
            return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getCivilianKills();
        }
        if ("%kills_neutral%".equals(key)) {
            return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getNeutralKills();
        }
        if ("%kills_rival%".equals(key)) {
            return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getRivalKills();
        }
        if ("%kills_total%".equals(key)) {
            final ClanPlayer clanPlayer = PluginListener.getSimpleclans().getClanManager().getClanPlayer(player);
            return clanPlayer.getCivilianKills() + clanPlayer.getNeutralKills() + clanPlayer.getRivalKills();
        }
        if ("%deaths%".equals(key)) {
            return PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getDeaths();
        }
        if ("%kdr%".equals(key)) {
            return (int) PluginListener.getSimpleclans().getClanManager().getClanPlayer(player).getKDR();
        }
        if ("%members%".equals(key)) {
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
        return -1;
    }

    @SuppressWarnings("deprecation")
    private static int getBukkitValues(final String key, final Player player) {
        if ("%online%".equals(key)) {
            return Bukkit.getOnlinePlayers().length;
        }
        if ("%free_ram%".equals(key)) {
            return (int) (Runtime.getRuntime().freeMemory() / 1048576); // / 1024 / 1024
        }
        if ("%max_ram%".equals(key)) {
            return (int) Runtime.getRuntime().maxMemory() / 1048576;
        }
        if ("%used_ram%".equals(key)) {
            return (int) Runtime.getRuntime().totalMemory() / 1048576;
        }
        if ("%date%".equals(key)) {
            return new Date().getDate();
        }
        if ("%time%".equals(key)) {
            return (int) (player.getWorld().getTime() / 1000);
        }
        if ("%lifetime%".equals(key)) {
            return player.getTicksLived() / 1200;
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
        return -1;
    }
}
