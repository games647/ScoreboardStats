package com.github.games647.scoreboardstats.scoreboard;

import static com.github.games647.scoreboardstats.ScoreboardStats.getSettings;
import com.github.games647.scoreboardstats.listener.PluginListener;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.gmail.nossr50.api.ExperienceAPI;
import java.util.Date;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VariableReplacer {

    public static int getReplacedInt(final String key, final Player player) {
        if (!player.isOnline()) {
            return -1;
        }

        if (getSettings().isPvpStats()) {
            final int value = getPvpValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        if (PluginListener.getEcon() != null && VariableList.ECONOMY.equals(key)) {
            return (int) PluginListener.getEcon().getBalance(player.getName());
        }

        if (PluginListener.isMcmmo()) {
            final int value = getMcmmoValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        if (PluginListener.getEssentials() != null && VariableList.TICKS.equals(key)) {
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
        if (VariableList.KILLS_CIVILIAN.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getCivilianKills();
        }

        if (VariableList.KILLS_NEUTRAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getNeutralKills();
        }

        if (VariableList.KILLS_RIVAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getRivalKills();
        }

        if (VariableList.KILLS_TOTAL.equals(key)) {
            final ClanPlayer clanPlayer = PluginListener.getSimpleclans().getClanPlayer(player);
            return clanPlayer.getCivilianKills() + clanPlayer.getNeutralKills() + clanPlayer.getRivalKills();
        }

        if (VariableList.DEATHS.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getDeaths();
        }

        if (VariableList.KDR.equals(key)) {
            return (int) PluginListener.getSimpleclans().getClanPlayer(player).getKDR();
        }

        if (VariableList.MEMBER.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getMembers().size();
        }

        if (VariableList.CLAN_KDR.equals(key)) {
            return (int) PluginListener.getSimpleclans().getClanPlayer(player).getClan().getTotalKDR();
        }

        if (VariableList.CLAN_MONEY.equals(key)) {
            return (int) PluginListener.getSimpleclans().getClanPlayer(player).getClan().getBalance();
        }

        if (VariableList.MEMBER_ONLINE.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getOnlineMembers().size();
        }

        if (VariableList.RIVAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getRivals().size();
        }

        if (VariableList.ALLIES.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getAllies().size();
        }

        if (VariableList.ALLIES_TOTAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getAllAllyMembers().size();
        }

        if (VariableList.CLAIMS.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getAllowedClaims();
        }

        if (VariableList.CLAN_RIVAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getTotalRival();
        }

        if (VariableList.CLAN_NEUTRAL.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getTotalNeutral();
        }

        if (VariableList.CLAN_DEATHS.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getTotalDeaths();
        }

        if (VariableList.CLAN_CIVILIAN.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getTotalCivilian();
        }

        if (VariableList.POWER.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getPower();
        }

        return -1;
    }

    @SuppressWarnings("deprecation")
    private static int getBukkitValues(final String key, final Player player) {
        if (VariableList.HEALTH.equals(key)) {
            return player.getHealth();
        }

        if (VariableList.ONLINE.equals(key)) {
            if (getSettings().isHideVanished()) {
                int online = 0;
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (player.canSee(other)) {
                        online++;
                    }
                }
                return online;
            } else {
                return Bukkit.getOnlinePlayers().length;
            }
        }

        if (VariableList.FREE_RAM.equals(key)) {
            return (int) (Runtime.getRuntime().freeMemory() / 1024 / 1024); // / 1024 / 1024
        }

        if (VariableList.MAX_RAM.equals(key)) {
            return (int) Runtime.getRuntime().maxMemory() / 1024 / 1024;
        }

        if (VariableList.USED_RAM.equals(key)) {
            return (int) Runtime.getRuntime().totalMemory() / 1024 / 1024;
        }

        if (VariableList.DATE.equals(key)) {
            return new Date().getDate();
        }

        if (VariableList.TIME.equals(key)) {
            return (int) (player.getWorld().getTime() / 1000);
        }

        if (VariableList.LIFETIME.equals(key)) {
            return player.getTicksLived() / 20 / 60;
        }

        if (VariableList.EXP.equals(key)) {
            return player.getTotalExperience();
        }

        if (VariableList.NODAMAGE.equals(key)) {
            return player.getNoDamageTicks() / 20 / 60;
        }

        if (VariableList.XPTOLEVEL.equals(key)) {
            return player.getExpToLevel();
        }

        if (VariableList.LASTDAMAGE.equals(key)) {
            return player.getLastDamage() / 20 / 60;
        }

        if (VariableList.MAXPLAYER.equals(key)) {
            return Bukkit.getMaxPlayers();
        }

        if (VariableList.PING.equals(key)) {
            return ((org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer) player).getHandle().ping;
        }

        return -1;
    }
}
