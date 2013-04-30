package me.games647.scoreboardstats.api;

import me.games647.scoreboardstats.ScoreboardStats;
import static me.games647.scoreboardstats.ScoreboardStats.getInstance;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import me.games647.scoreboardstats.api.pvpstats.Database;
import me.games647.scoreboardstats.api.pvpstats.TempScoreShow;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public final class Score {

    public static void createScoreboard(final Player player) {
        if (!player.hasPermission("scoreboardstats.use")) {
            return;
        }

        Objective oldobjective = player.getScoreboard().getObjective("ScoreboardStats");

        if (oldobjective == null) {
            oldobjective = player.getScoreboard().registerNewObjective("ScoreboardStats", "dummy");
        }

        if (oldobjective.getDisplaySlot() != DisplaySlot.SIDEBAR) {
            oldobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        if (getSettings().isTempscoreboard()) {
            getScheduler().runTaskLater(getInstance(), new TempScoreShow(player), getSettings().getTempshow() * 20L);
        }

        oldobjective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTitle()));
        player.setScoreboard(oldobjective.getScoreboard());
        getSettings().sendUpdate(player);
    }

    public static void createTopListScoreboard(final Player player) {
        if ((!player.hasPermission("scoreboardstats.use")) || (!player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().startsWith("ScoreboardStats"))) {
            return;
        }

        Objective oldobjective = player.getScoreboard().getObjective("ScoreboardStats T");

        if (oldobjective == null) {
            oldobjective = player.getScoreboard().registerNewObjective("ScoreboardStats T", "dummy");
        }

        if (oldobjective.getDisplaySlot() != DisplaySlot.SIDEBAR) {
            oldobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        oldobjective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTemptitle()));
        final java.util.Map<String, Integer> top = Database.getTop();

        for (String key : top.keySet()) {
            Score.sendScore(player, String.format("%s%s", ScoreboardStats.getSettings().getTempcolor(), checkLength(key)), top.get(key));
        }

        player.setScoreboard(oldobjective.getScoreboard());
    }

    public static void sendScore(final Player player, final String title, final int value) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

        if ((objective == null) || (!objective.getName().startsWith("ScoreboardStats"))) {
            return;
        }

        final org.bukkit.scoreboard.Score score = objective.getScore(Bukkit.getOfflinePlayer(translateAlternateColorCodes('&', title)));

        if (score.getScore() == value) { // Send not much packets
            return;
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
}
