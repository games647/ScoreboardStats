package com.github.games647.scoreboardstats.scoreboard;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.Settings;
import com.github.games647.scoreboardstats.pvpstats.AppearTask;
import com.github.games647.scoreboardstats.pvpstats.Database;
import com.github.games647.scoreboardstats.pvpstats.DisapperTask;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import com.github.games647.variables.Permissions;

import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import org.fusesource.jansi.Ansi;

public final class SbManager {

    private SbManager() {}

    public static void createScoreboard(Player player) {
        if (!player.hasPermission(Permissions.USE_PERMISSION)
                || ScoreboardStats.getInstance().hidelist.contains(player.getName())
                || Settings.isDisabledWorld(player.getWorld().getName())) {
            return;
        }

        if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null
                && !player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().equals(Other.TOPLIST)) {
            return;
        }

        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective(Other.PLUGIN_NAME, Other.EMPTY_CRITERA);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, Settings.getTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (player.isOnline()) {
            try {
                player.setScoreboard(scoreboard);
            } catch (IllegalStateException ex) {
                Bukkit.getLogger().log(Level.FINE, "{0}" + Message.LOG_NAME + Message.SET_SCOREBOARD_FAIL + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
            }

            Settings.sendUpdate(player, true);

            if (Settings.isTempScoreboard()) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(ScoreboardStats.getInstance(), new AppearTask(player), Settings.getTempShow() * Other.TICKS_PER_SECOND);
            }
        }
    }

    public static void createTopListScoreboard(Player player) {
        final Scoreboard scoreboard = player.getScoreboard();

        if (!player.hasPermission(Permissions.USE_PERMISSION)
                || scoreboard   .getObjective(DisplaySlot.SIDEBAR) == null
                || !scoreboard  .getObjective(DisplaySlot.SIDEBAR).getName().startsWith(Other.PLUGIN_NAME)
                || ScoreboardStats.getInstance().hidelist.contains(player.getName())) {
            return;
        }

        if (scoreboard.getObjective(Other.TOPLIST) != null) {
            scoreboard.getObjective(Other.TOPLIST).unregister();  //to remove the old scores
        }

        final Map<String, Integer> top = Database.getTop();
        final String color = Settings.getTempColor();

        final Objective objective = scoreboard.registerNewObjective(Other.TOPLIST, Other.EMPTY_CRITERA);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, Settings.getTempTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (player.isOnline()) {
            try {
                player.setScoreboard(scoreboard);
            } catch (IllegalStateException ex) {
                Bukkit.getLogger().log(Level.FINE, "{0}" + Message.LOG_NAME + Message.SET_SCOREBOARD_FAIL + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
            }

            for (Map.Entry<String, Integer> entry : top.entrySet()) {
                sendScore(objective, String.format("%s%s", color, checkLength(entry.getKey())), entry.getValue(), false);
            }

            Bukkit.getScheduler().runTaskLater(ScoreboardStats.getInstance(), new DisapperTask(player), Settings.getTempDisapper() * Other.TICKS_PER_SECOND);
        }
    }

    public static void sendScore(Objective objective, String title, int value, boolean complete) {
        final Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.translateAlternateColorCodes(Other.CHATCOLOR_CHAR, title)));

        if (complete
                && value == 0) { //Have to use this because the score wouldn't send otherwise
            score.setScore(-1);
        }

        score.setScore(value);
    }

    private static String checkLength(String check) {

        return check.length() > Other.MINECRAFT_LIMIT - 2 ? check.substring(0, Other.MINECRAFT_LIMIT - 2) : check; //Because adding the color
    }

    public static void regAll() {
        Bukkit.getScheduler().runTaskAsynchronously(ScoreboardStats.getInstance(), new Runnable() {
            @Override
            public void run() {
                final boolean ispvpstats = Settings.isPvpStats();

                for (Player player : Bukkit.getOnlinePlayers()) {
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
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}
