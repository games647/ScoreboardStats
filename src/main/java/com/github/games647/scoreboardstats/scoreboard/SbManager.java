package com.github.games647.scoreboardstats.scoreboard;

import static com.github.games647.scoreboardstats.ScoreboardStats.getInstance;
import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import com.github.games647.variables.Permissions;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class SbManager {

    public static void createScoreboard(final Player player) {
        if (!player.hasPermission(Permissions.USE_PERMISSION)
                || getInstance().hidelist.contains(player.getName())
                || Settings.isDisabledWorld(player.getWorld().getName())) {
            return;
        }

        if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null
                && !player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().equals(Other.TOPLIST)) {
            return;
        }

        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective(Other.PLUGIN_NAME, Other.EMPTY_CRITERA);
        objective.setDisplayName(translateAlternateColorCodes(ChatColor.COLOR_CHAR, Settings.getTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (player.isOnline()) {
            try {
                player.setScoreboard(scoreboard);
            } catch (IllegalStateException ex) {
                Bukkit.getServer().getConsoleSender().sendMessage(Message.LOG_NAME + Message.SET_SCOREBOARD_FAIL);
            }

            Settings.sendUpdate(player, true);

            if (Settings.isTempScoreboard()) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), new com.github.games647.scoreboardstats.pvpstats.AppearTask(player), Settings.getTempShow() * Other.TICKS_PER_SECOND);
            }
        }
    }

    public static void createTopListScoreboard(final Player player) {
        final Scoreboard scoreboard = player.getScoreboard();

        if (!player.hasPermission(Permissions.USE_PERMISSION)
                || scoreboard   .getObjective(DisplaySlot.SIDEBAR) == null
                || !scoreboard  .getObjective(DisplaySlot.SIDEBAR).getName().startsWith(Other.PLUGIN_NAME)
                || getInstance().hidelist.contains(player.getName())) {
            return;
        }

        if (scoreboard.getObjective(Other.TOPLIST) != null) {
            scoreboard.getObjective(Other.TOPLIST).unregister();  //to remove the old scores
        }

        final Map<String, Integer> top = Database.getTop();
        final String color = Settings.getTempColor();

        final Objective objective = scoreboard.registerNewObjective(Other.TOPLIST, Other.EMPTY_CRITERA);
        objective.setDisplayName(translateAlternateColorCodes(ChatColor.COLOR_CHAR, Settings.getTempTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (player.isOnline()) {
            try {
                player.setScoreboard(scoreboard);
            } catch (IllegalStateException ex) {
                Bukkit.getServer().getConsoleSender().sendMessage(Message.LOG_NAME + Message.SET_SCOREBOARD_FAIL);
            }

            for (final Map.Entry<String, Integer> entry : top.entrySet()) {
                sendScore(objective, String.format("%s%s", color, checkLength(entry.getKey())), entry.getValue(), false);
            }

            Bukkit.getScheduler().runTaskLater(getInstance(), new com.github.games647.scoreboardstats.pvpstats.DisapperTask(player), Settings.getTempDisapper() * Other.TICKS_PER_SECOND);
        }
    }

    public static void sendScore(final Objective objective, final String title, final int value, final boolean complete) {
        final org.bukkit.scoreboard.Score score = objective.getScore(Bukkit.getOfflinePlayer(translateAlternateColorCodes(Other.CHATCOLOR_CHAR, title)));

        if (complete
                && value == 0) { //Have to use this because the score wouldn't send otherwise
            score.setScore(-1);
        }

        score.setScore(value);
    }

    private static String checkLength(final String check) {

        return check.length() > Other.MINECRAFT_LIMIT - 2 ? check.substring(0, Other.MINECRAFT_LIMIT - 2) : check; //Because adding the color
    }

    public static void regAll() {
        Bukkit.getScheduler().runTaskAsynchronously(getInstance(), new Runnable() {
            @Override
            public void run() {
                final boolean ispvpstats = Settings.isPvpStats();

                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.isOnline()) {
                        continue;
                    }

                    if (ispvpstats) {
                        Database.loadAccount(player.getName());
                    }
                }
            }
        });
    }

    public static void unregisterAll() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}
