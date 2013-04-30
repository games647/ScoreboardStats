package me.games647.scoreboardstats.api;

import static me.games647.scoreboardstats.ScoreboardStats.getInstance;
import static me.games647.scoreboardstats.ScoreboardStats.getSettings;
import me.games647.scoreboardstats.api.pvpstats.TempScoreShow;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public final class Score {

    public static void createScoreboard(final Player player, final boolean type) {
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

        if (type) {
            if (getSettings().isTempscoreboard()) {
                getScheduler().runTaskLater(getInstance(), new TempScoreShow(player), getSettings().getTempshow() * 20L);
            }

            oldobjective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTitle()));
            player.setScoreboard(oldobjective.getScoreboard());
            getSettings().sendUpdate(player);
            return;
        }

        oldobjective.setDisplayName(translateAlternateColorCodes('&', getSettings().getTemptitle()));
    }

    public static void sendScore(final Player player, final String title, final int value, final boolean type) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

        if (objective == null) {
            return;
        }

        final org.bukkit.scoreboard.Score score = objective.getScore(Bukkit.getOfflinePlayer(translateAlternateColorCodes('&', title)));

        if ((score.getScore() != 0) && (score.getScore() == value)) { // Send not much packets
            return;
        }

        score.setScore(value);
        player.setScoreboard(objective.getScoreboard());
    }
}
