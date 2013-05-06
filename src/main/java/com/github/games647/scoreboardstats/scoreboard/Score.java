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

public final class Score {

    public static void createScoreboard(final Player player) {
        if (!player.hasPermission("scoreboardstats.use")) {
            return;
        }

        final Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("ScoreboardStats");

        if (objective == null) {
            objective = scoreboard.registerNewObjective("ScoreboardStats", "dummy");
        }

        objective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (!player.isOnline()) {
            return;
        }

        player.setScoreboard(scoreboard);
        getSettings().sendUpdate(player, true);

        if (getSettings().isTempscoreboard()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), new com.github.games647.scoreboardstats.pvpstats.TempScoreShow(player), getSettings().getTempshow() * 20L);
        }
    }

    public static void createTopListScoreboard(final Player player) {
        if (!player.hasPermission("scoreboardstats.use") || player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null || !player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().startsWith("ScoreboardStats")) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(getInstance(), new com.github.games647.scoreboardstats.pvpstats.TempScoreDisapper(player), getSettings().getTempdisapper() * 20L);
        final Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard.getObjective("ScoreboardStatsT") != null) {
            scoreboard.getObjective("ScoreboardStatsT").unregister();  //to remove the old scores
        }

        final Objective objective = scoreboard.registerNewObjective("ScoreboardStatsT", "dummy");
        objective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTemptitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (!player.isOnline()) {
            return;
        }

        player.setScoreboard(scoreboard);
        final java.util.Map<String, Integer> top = Database.getTop();

        for (String key : top.keySet()) {
            Score.sendScore(player, String.format("%s%s", getSettings().getTempcolor(), checkLength(key)), top.get(key), false);
        }
    }

    public static void sendScore(final Player player, final String title, final int value, final boolean complete) {
        if (!player.isOnline() || !player.hasPermission("scoreboardstats.use")) {
            return;
        }

        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

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
        final boolean ispvpstats = getSettings().isPvpstats();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            if (ispvpstats) {
                Database.loadAccount(player.getName());
            }

            createScoreboard(player);
        }

        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("ScoreboardStats");

        if (objective == null) {
            objective = scoreboard.registerNewObjective("ScoreboardStats", "dummy");
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
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
