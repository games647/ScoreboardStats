package me.games647.scoreboardstats.api;

import static me.games647.scoreboardstats.ScoreboardStats.getInstance;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import me.games647.scoreboardstats.api.pvpstats.Database;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public final class Score {

    public static void createScoreboard(final Player player) {
        if (!player.hasPermission("scoreboardstats.use")) {
            return;
        }

        if (getSettings().isTempscoreboard()) {
            Bukkit.getScheduler().runTaskLater(getInstance(), new me.games647.scoreboardstats.api.pvpstats.TempScoreShow(player), getSettings().getTempshow() * 20L);
        }

        final Objective objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("ScoreboardStats", "dummy"); //Use new Scoreboard because if something was removed it will no longer send it to the client
        objective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(objective.getScoreboard());
        getSettings().sendUpdate(player, true);
    }

    public static void createTopListScoreboard(final Player player) {
        if ((!player.hasPermission("scoreboardstats.use")) || (!player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().startsWith("ScoreboardStats"))) {
            return;
        }

        final Objective objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("ScoreboardStatsT", "dummy");
        objective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTemptitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(objective.getScoreboard());
        final java.util.Map<String, Integer> top = Database.getTop();

        for (String key : top.keySet()) {
            Score.sendScore(player, String.format("%s%s", getSettings().getTempcolor(), checkLength(key)), top.get(key), false);
        }
    }

    public static void sendScore(final Player player, final String title, final int value, final boolean complete) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

        if ((!player.hasPermission("scoreboardstats.use")) || (objective == null) || (!objective.getName().startsWith("ScoreboardStats"))) {
            return;
        }

        final org.bukkit.scoreboard.Score score = objective.getScore(Bukkit.getOfflinePlayer(translateAlternateColorCodes('&', title)));

        if ((!complete) && (score.getScore() == value)) { // Send not much packets
            return;
        }

        if (value == 0) { //Have to use this because the score wouldn't set otherwise
            score.setScore(-1);
        }
        score.setScore(value);
        player.setScoreboard(objective.getScoreboard());
    }

    private static String checkLength(final String check) {
        if (check.length() < 15) {
            return check;
        }

        return check.substring(0, 14);
    }

   public static void regAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            if (getSettings().isPvpstats()) {
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
