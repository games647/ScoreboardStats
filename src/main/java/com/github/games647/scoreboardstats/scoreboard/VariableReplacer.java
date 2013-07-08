package com.github.games647.scoreboardstats.scoreboard;

import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.listener.PluginListener;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.PlayerCache;
import com.github.games647.variables.Other;
import com.github.games647.variables.VariableList;

import com.gmail.nossr50.api.ExperienceAPI;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.entity.UPlayer;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class VariableReplacer {

    private VariableReplacer() {}

    public static int getReplacedInt(final String key, final Player player) {
        if (!player.isOnline()) {
            return -1;
        }

        if (Settings.isPvpStats()) {
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
            final int value = SimpleClansReplacer.getSimpleClans1Value(key, player);
            if (value != -1) {
                return value;
            }
        }

        if (PluginListener.getSimpleclans2() != null) {
            final int value = SimpleClansReplacer.getSimpleClans2Value(key, player);
            if (value != -1) {
                return value;
            }
        }

        if (PluginListener.getFactions() != null) {
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

    @SuppressWarnings("deprecation")
    private static int getBukkitValues(final String key, final Player player) {
        if (VariableList.HEALTH.equals(key)) {
            return (int) Math.round(player.getHealth());
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
            return (int) player.getLastDamage() / Other.TICKS_INT / Other.SECONDS;
        }

        if (VariableList.MAXPLAYER.equals(key)) {
            return Bukkit.getMaxPlayers();
        }

        if (VariableList.PING.equals(key)) {
            return ((org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer) player).getHandle().ping;
        }

        if (VariableList.HELMET.equals(key)) {
            final ItemStack helmet = player.getInventory().getHelmet();
            if (helmet != null
                    && helmet.getType().getMaxDurability() != 0) {
                return helmet.getDurability() * 100 / helmet.getType().getMaxDurability();
            } else {
                return -2;
            }
        }

        if (VariableList.BOOTS.equals(key)) {
            final ItemStack boots = player.getInventory().getBoots();
            if (boots != null
                    && boots.getType().getMaxDurability() != 0) {
                return boots.getDurability() * 100 / boots.getType().getMaxDurability();
            } else {
                return -2;
            }
        }

        if (VariableList.LEGGINGS.equals(key)) {
            final ItemStack leggings = player.getInventory().getLeggings();
            if (leggings != null
                    && leggings.getType().getMaxDurability() != 0) {
                return leggings.getDurability() * 100 / leggings.getType().getMaxDurability();
            } else {
                return -2;
            }
        }

        if (VariableList.CHESTPLATE.equals(key)) {
            final ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate != null
                    && chestplate.getType().getMaxDurability() != 0) {
                return chestplate.getDurability() * 100 / chestplate.getType().getMaxDurability();
            } else {
                return -2;
            }
        }

        return -1;
    }

    private static int getOnlinePlayers(final Player player) {
        if (Settings.isHideVanished()) {
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
        if (PluginListener.getFactions().startsWith("1")) {
            final FPlayer fplayer = FPlayers.i.get(player);

            if (VariableList.POWER.equals(key)) {
                return fplayer.getPowerRounded();
            }
            if (fplayer.getFaction() != null) {
                if (VariableList.F_POWER.equals(key)) {
                    return fplayer.getFaction().getPowerRounded();
                }

                if (VariableList.MEMBERS.equals(key)) {
                    return fplayer.getFaction().getFPlayers().size();
                }

                if (VariableList.MEMBERS_ONLINE.equals(key)) {
                    return fplayer.getFaction().getOnlinePlayers().size();
                }
            }
        } else {
            final UPlayer uplayer = UPlayer.get(player);

            if (VariableList.POWER.equals(key)) {
                return uplayer.getPowerRounded();
            }

            if (uplayer.getFaction() != null) {
                if (VariableList.F_POWER.equals(key)) {
                    return uplayer.getFaction().getPowerRounded();
                }

                if (VariableList.MEMBERS.equals(key)) {
                    return uplayer.getFaction().getUPlayers().size();
                }

                if (VariableList.MEMBERS_ONLINE.equals(key)) {
                    return uplayer.getFaction().getOnlinePlayers().size();
                }
            }
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
