package com.github.games647.scoreboardstats.scoreboard;

import static com.github.games647.scoreboardstats.ScoreboardStats.getSettings;
import com.github.games647.scoreboardstats.listener.PluginListener;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;
import com.github.games647.variables.Other;
import com.github.games647.variables.VariableList;
import com.gmail.nossr50.api.ExperienceAPI;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VariableReplacer {

    public static int getReplacedInt(final String key, final Player player) {
        if (!player.isOnline()) {
            return -1;
        }

        if (getSettings().isPvpStats()) {
            final int value = getPvpValue(key, player.getName());
            if (value != -1) {
                return value;
            }
        }

        if (PluginListener.getEconomy() != null
                && VariableList.ECONOMY.equals(key)) {
            return (int) Math.round(PluginListener.getEconomy().getBalance(player.getName()));
        }

        if (PluginListener.isMcmmo()) {
            final int value = getMcmmoValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        if (PluginListener.getEssentials() != null
                && VariableList.TICKS.equals(key)) {
            return (int) Math.round(PluginListener.getEssentials().getAverageTPS());
        }

        if (PluginListener.getSimpleclans() != null) {
            final int value = getSimpleClansValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        if (PluginListener.isFactions()) {
            final int value = getFactionsValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        if (PluginListener.getHeroes() != null) {
            final int value = getHeroesValue(key, player);
            if (value != -1) {
                return value;
            }
        }

        return getBukkitValues(key, player);
    }

    private static int getPvpValue(final String key, final String name) {
        final PlayerCache cache = Database.getCache(name);

        if (cache == null) {
            return -1;
        }

        if (VariableList.KILLS.equals(key)) {
            return cache.getKills();
        }

        if (VariableList.DEATHS.equals(key)) {
            return cache.getDeaths();
        }

        if (VariableList.MOB.equals(key)) {
            return cache.getMob();
        }

        if (VariableList.KDR.equals(key)) {
            return Database.getKdr(name);
        }

        if (VariableList.KILLSTREAK.equals(key)) {
            return cache.getStreak();
        }

        if (VariableList.CURRENTSTREAK.equals(key)) {
            return cache.getLastStreak();
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
            return Math.round(PluginListener.getSimpleclans().getClanPlayer(player).getKDR());
        }

        if (VariableList.MEMBER.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getMembers().size();
        }

        if (VariableList.CLAN_KDR.equals(key)) {
            return (int) PluginListener.getSimpleclans().getClanPlayer(player).getClan().getKDR();
        }

        if (VariableList.CLAN_MONEY.equals(key)) {
            return (int) PluginListener.getSimpleclans().getClanPlayer(player).getClan().getBalance();
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

        if (VariableList.CLAN_KILLS.equals(key)) {
            return PluginListener.getSimpleclans().getClanPlayer(player).getClan().getTotalKills().length;
        }

        return -1;
    }

    @SuppressWarnings("deprecation")
    private static int getBukkitValues(final String key, final Player player) {
        if (VariableList.HEALTH.equals(key)) {
            return player.getHealth();
        }

        if (VariableList.ONLINE.equals(key)) {
            return getOnlinePlayers(player);
        }

        if (VariableList.FREE_RAM.equals(key)) {
            return (int) (Runtime.getRuntime().freeMemory() / Other.INTO_NEXT_SIZE / Other.INTO_NEXT_SIZE); // / 1024 / 1024
        }

        if (VariableList.MAX_RAM.equals(key)) {
            return (int) Runtime.getRuntime().maxMemory() / Other.INTO_NEXT_SIZE / Other.INTO_NEXT_SIZE;
        }

        if (VariableList.USED_RAM.equals(key)) {
            return (int) (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / Other.INTO_NEXT_SIZE / Other.INTO_NEXT_SIZE;
        }

        if (VariableList.USED_RAM_PERCENT.equals(key)) {
            return (int) ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) * 100 / Runtime.getRuntime().maxMemory());
        }

        if (VariableList.DATE.equals(key)) {
            return new Date().getDate();
        }

        if (VariableList.LIFETIME.equals(key)) {
            return player.getTicksLived() / Other.TICKS_INT / Other.SECONDS;
        }

        if (VariableList.EXP.equals(key)) {
            return player.getTotalExperience();
        }

        if (VariableList.NODAMAGE.equals(key)) {
            return player.getNoDamageTicks() / Other.TICKS_INT / Other.SECONDS;
        }

        if (VariableList.XPTOLEVEL.equals(key)) {
            return player.getExpToLevel();
        }

        if (VariableList.LASTDAMAGE.equals(key)) {
            return player.getLastDamage() / Other.TICKS_INT / Other.SECONDS;
        }

        if (VariableList.MAXPLAYER.equals(key)) {
            return Bukkit.getMaxPlayers();
        }

        if (VariableList.PING.equals(key)) {
            return ((org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer) player).getHandle().ping;
        }

        return -1;
    }

    private static int getOnlinePlayers(final Player player) {
        if (getSettings().isHideVanished()) {
            int online = 0;

            for (final Player other : Bukkit.getOnlinePlayers()) {
                if (player.canSee(other)) {
                    online++;
                }
            }
            return online;
        } else {
            return Bukkit.getOnlinePlayers().length;
        }
    }

    private static int getFactionsValue(final String key, final Player player) {
        final FPlayer fplayer = FPlayers.i.get(player);

        if (VariableList.POWER.equals(key)) {
            return fplayer.getPowerRounded();
        }

        if (VariableList.F_POWER.equals(key)) {
            return fplayer.getFaction().getPowerRounded();
        }

        if (VariableList.MEMBERS.equals(key)) {
            return fplayer.getFaction().getFPlayers().size();
        }

        if (VariableList.MEMBERS_ONLINE.equals(key)) {
            return fplayer.getFaction().getOnlinePlayers().size();
        }

        return -1;
    }

    private static int getHeroesValue(String key, Player player) {
        if (VariableList.MANA.equals(key)) {
            return PluginListener.getHeroes().getHero(player).getMana();
        }

        if (VariableList.LEVEL.equals(key)) {
            return PluginListener.getHeroes().getHero(player).getLevel();
        }

        if (VariableList.MAX_MANA.equals(key)) {
            return PluginListener.getHeroes().getHero(player).getMaxMana();
        }

        if (VariableList.MANA_REGEN.equals(key)) {
            return PluginListener.getHeroes().getHero(player).getManaRegen();
        }

        return -1;
    }
}
