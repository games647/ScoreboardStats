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

        if ((PluginListener.getEcon() != null) && (VariableList.ECONOMY.equals(key))) {
            return (int) PluginListener.getEcon().getBalance(player.getName());
        }

        if (PluginListener.isMcmmo()) {
            final int value = getMcmmoValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        if ((PluginListener.getEssentials() != null) && (VariableList.TICKS.equals(key))) {
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
    
    private static int getPvpValue(final String key, final Player player) {
        if (VariableList.KILLS.equals(key)) {
            return Database.getCache(player.getName()).getKills();
        }
        if (VariableList.DEATHS.equals(key)) {
            return Database.getCache(player.getName()).getDeaths();
        }
        if (VariableList.MOB.equals(key)) {
            return Database.getCache(player.getName()).getMob();
        }
        if (VariableList.KDR.equals(key)) {
            return Database.getKdr(player.getName());
        }
        if (VariableList.KILLSTREAK.equals(key)) {
            return Database.getCache(player.getName()).getStreak();
        }
        if (VariableList.CURRENTSTREAK.equals(key)) {
            return Database.getCache(player.getName()).getLastStreak();
        }
        return -1;
    }

    private static int getMcmmoValue(final String key, final Player player) {
        if (VariableList.POWLVL.equals(key)) {
            return ExperienceAPI.getPowerLevel(player);
        }
        if (VariableList.WOODCUTTING.equals(key)) {
            return ExperienceAPI.getLevel(player, "WOODCUTTING");
        }
        if (VariableList.ACROBATICS.equals(key)) {
            return ExperienceAPI.getLevel(player, "ACROBATICS");
        }
        if (VariableList.ARCHERY.equals(key)) {
            return ExperienceAPI.getLevel(player, "ARCHERY");
        }
        if (VariableList.AXES.equals(key)) {
            return ExperienceAPI.getLevel(player, "AXES");
        }
        if (VariableList.EXCAVATION.equals(key)) {
            return ExperienceAPI.getLevel(player, "EXCAVATION");
        }
        if (VariableList.FISHING.equals(key)) {
            return ExperienceAPI.getLevel(player, "FISHING");
        }
        if (VariableList.HERBALISM.equals(key)) {
            return ExperienceAPI.getLevel(player, "HERBALISM");
        }
        if (VariableList.MINING.equals(key)) {
            return ExperienceAPI.getLevel(player, "MINING");
        }
        if (VariableList.REPAIR.equals(key)) {
            return ExperienceAPI.getLevel(player, "REPAIR");
        }
        if (VariableList.SMELTING.equals(key)) {
            return ExperienceAPI.getLevel(player, "SMELTING");
        }
        if (VariableList.SWORDS.equals(key)) {
            return ExperienceAPI.getLevel(player, "SWORDS");
        }
        if (VariableList.TAMING.equals(key)) {
            return ExperienceAPI.getLevel(player, "TAMING");
        }
        if (VariableList.UNARMED.equals(key)) {
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
        if (VariableList.ONLINE.equals(key)) {
            return Bukkit.getOnlinePlayers().length;
        }
        if (VariableList.FREE_RAM.equals(key)) {
            return (int) (Runtime.getRuntime().freeMemory() / 1048576); // / 1024 / 1024
        }
        if (VariableList.MAX_RAM.equals(key)) {
            return (int) Runtime.getRuntime().maxMemory() / 1048576;
        }
        if (VariableList.USED_RAM.equals(key)) {
            return (int) Runtime.getRuntime().totalMemory() / 1048576;
        }
        if (VariableList.DATE.equals(key)) {
            return new Date().getDate();
        }
        if (VariableList.TIME.equals(key)) {
            return (int) (player.getWorld().getTime() / 1000);
        }
        if (VariableList.LIFETIME.equals(key)) {
            return player.getTicksLived() / 1200;
        }
        if (VariableList.EXP.equals(key)) {
            return player.getTotalExperience();
        }
        if (VariableList.NODAMAGE.equals(key)) {
            return player.getNoDamageTicks();
        }
        if (VariableList.XPTOLEVEL.equals(key)) {
            return player.getExpToLevel();
        }
        if (VariableList.LASTDAMAGE.equals(key)) {
            return player.getLastDamage();
        }
        if (VariableList.MAXPLAYER.equals(key)) {
            return Bukkit.getMaxPlayers();
        }
        if (VariableList.PING.equals(key)) {
            return ((org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer) player).getHandle().ping;
        }
        return -1;
    }
}
