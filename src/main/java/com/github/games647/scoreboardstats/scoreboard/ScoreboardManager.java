package com.github.games647.scoreboardstats.scoreboard;

import static com.github.games647.scoreboardstats.ScoreboardStats.getInstance;
import static com.github.games647.scoreboardstats.ScoreboardStats.getSettings;
import com.github.games647.scoreboardstats.pvpstats.Database;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class ScoreboardManager {

    public static void createScoreboard(final Player player) {
        if (!player.hasPermission("scoreboardstats.use") || player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
            return;
        }

        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("ScoreboardStats", "dummy");
        objective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTitle()));

        if (!player.isOnline()) {
            return;
        }

        player.setScoreboard(scoreboard);
        getSettings().sendUpdate(player, true);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (getSettings().isTempScoreboard()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), new com.github.games647.scoreboardstats.pvpstats.TempScoreShow(player), getSettings().getTempShow() * 20L);
        }
    }

    public static void createTopListScoreboard(final Player player) {
        final Scoreboard scoreboard = player.getScoreboard();

        if (!player.hasPermission("scoreboardstats.use") || scoreboard.getObjective(DisplaySlot.SIDEBAR) == null || !scoreboard.getObjective(DisplaySlot.SIDEBAR).getName().startsWith("ScoreboardStats")) {
            return;
        }

        if (scoreboard.getObjective("ScoreboardStatsT") != null) {
            scoreboard.getObjective("ScoreboardStatsT").unregister();  //to remove the old scores
        }

        final Objective objective = scoreboard.registerNewObjective("ScoreboardStatsT", "dummy");
        objective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTempTitle()));

        if (!player.isOnline()) {
            return;
        }

        player.setScoreboard(scoreboard);
        final java.util.Map<String, Integer> top = Database.getTop();
        final String color = getSettings().getTempColor();

        for (String key : top.keySet()) {
            sendScore(player, String.format("%s%s", color, checkLength(key)), top.get(key), true);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Bukkit.getScheduler().runTaskLater(getInstance(), new com.github.games647.scoreboardstats.pvpstats.TempScoreDisapper(player), getSettings().getTempDisapper() * 20L);
    }

    public static void sendScore(final Player player, final String title, final int value, final boolean complete) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

        if (!player.isOnline() || !player.hasPermission("scoreboardstats.use")) {
            return;
        }

        if (objective == null && !complete) {
            createScoreboard(player);
            return;
        }

        if (objective == null || !objective.getName().startsWith("ScoreboardStats")) {
            return;
        }

        final org.bukkit.scoreboard.Score score = objective.getScore(Bukkit.getOfflinePlayer(translateAlternateColorCodes('&', title)));

        if (complete && value == 0) { //Have to use this because the score wouldn't send otherwise 
            score.setScore(-1);
        }

        score.setScore(value);
    }

    private static String checkLength(final String check) {

        return check.length() < 15 ? check : check.substring(0, 14);
    }

    public static void regAll() {
        final boolean ispvpstats = getSettings().isPvpStats();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            if (ispvpstats) {
                Database.loadAccount(player.getName());
            }

            createScoreboard(player);
        }
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
